package eu.europeana.annotation.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.output.ToStringConsumer;
import org.testcontainers.containers.output.WaitingConsumer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europeana.annotation.AnnotationBasePackageMapper;
import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.config.AnnotationConfigurationImpl;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.whitelist.WhitelistEntry;
import eu.europeana.annotation.dereferenciation.MetisDereferenciationClient;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.ModerationMongoException;
import eu.europeana.annotation.mongo.service.PersistentAnnotationService;
import eu.europeana.annotation.mongo.service.PersistentModerationRecordService;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.service.SolrAnnotationService;
import eu.europeana.annotation.tests.config.AnnotationTestsConfiguration;
import eu.europeana.annotation.tests.utils.AnnotationTestUtils;
import eu.europeana.annotation.tests.utils.MongoContainer;
import eu.europeana.annotation.tests.utils.SolrContainer;
import eu.europeana.annotation.web.service.WhitelistService;
import eu.europeana.api.commons.oauth2.utils.OAuthUtils;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

@ComponentScan(basePackageClasses = AnnotationBasePackageMapper.class)
@AutoConfigureMockMvc
@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AbstractIntegrationTest extends AnnotationTestsConstants {

  public static final String ORG_ID_SAZ = "saz-org-id";
  
  //FAKE Tokens format: user_id:user_name:role:apikey(optional):affiliation(optional)
  public static final String TOKEN_REGULAR = "regular-userid:regular-username:user:regular-user-apikey";
  public static final String TOKEN_PUBLISHER = "publisher-userid:publisher-username:publisher:publisher-apikey";
  
  public static final String TOKEN_PROVIDER_SAZ = "provider-saz-userid:saz-username:user:saz-apikey:"+ ORG_ID_SAZ;
  public static final String TOKEN_ADMIN = "admin-userid:admin-username:admin:admin-apikey";
  static String regularUserAuthorizationValue = OAuthUtils.TYPE_BEARER + " " + TOKEN_REGULAR;
  static String publisherUserAuthorizationValue = OAuthUtils.TYPE_BEARER + " " + TOKEN_PUBLISHER;
  static String providerSazUserAuthorizationValue = OAuthUtils.TYPE_BEARER + " " + TOKEN_PROVIDER_SAZ;
  static String adminUserAuthorizationValue = OAuthUtils.TYPE_BEARER + " " + TOKEN_ADMIN;
  
  
  public static final String SEARCH_API_FOUND = "{\"apikey\":\"testapikey\",\"success\":true,\"requestNumber\":999,\"itemsCount\":0,\"totalResults\":1,\"items\":[]}";
  public static final String SEARCH_API_NOT_FOUND = "{\"apikey\":\"testapikey\",\"success\":true,\"requestNumber\":999,\"itemsCount\":0,\"totalResults\":0,\"items\":[]}";
  
  public static final String KEY_SAZ_ITEM1 = buildAffiliationSetKey(ORG_ID_SAZ, "item1");
  public static final Set<String> SEARCH_API_AFFILIATION_CHECK = Set.of(KEY_SAZ_ITEM1); 
  
  private static List<Long> createdAnnotations = new ArrayList<Long>();
  protected static List<Long> createdModerationRecords = new ArrayList<Long>();

  protected static MockMvc mockMvc;
  private static MockWebServer mockMetisAndSearch;

  @Autowired
  protected WebApplicationContext webApplicationContext;

  @Autowired
  SolrAnnotationService solrService;

  @Autowired
  PersistentAnnotationService mongoPersistance;

  @Autowired
  PersistentModerationRecordService mongoPersistenceModeration;

  @Autowired
  protected WhitelistService whitelistService;

  @Autowired
  AnnotationConfiguration configuration;
  
  @Autowired
  @Qualifier(AnnotationConfiguration.BEAN_METIS_DEREFERENCE_CLIENT)
  protected MetisDereferenciationClient dereferenciationClient;
  
  private static final MongoContainer MONGO_CONTAINER;
  private static final SolrContainer SOLR_CONTAINER;
  
  static String buildAffiliationSetKey(String organization, String id) {
    String key = organization+":"+id;
    return key;
  }


  static {
    MONGO_CONTAINER = new MongoContainer("anno-it")
        .withLogConsumer(new WaitingConsumer().andThen(new ToStringConsumer()));

    MONGO_CONTAINER.start();

    SOLR_CONTAINER = new SolrContainer("anno-up")
        .withLogConsumer(new WaitingConsumer().andThen(new ToStringConsumer()));

    SOLR_CONTAINER.start();
    
    mockMetisAndSearch = new MockWebServer();
    mockMetisAndSearch.setDispatcher(setupMetisDispatcher());
    try {
      mockMetisAndSearch.start();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }
  

  @BeforeAll
  protected void initAppConextAndOauthTokens() throws Exception {
    // need to load white list for accepted URLs
    initWhiteListCollection();
    // initialize mockMvc
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    //disable OAuth.
    disableOauth();
  }
  
  private static Dispatcher setupMetisDispatcher() {
    return new Dispatcher() {
      @NotNull
      @Override
      public MockResponse dispatch(@NotNull RecordedRequest request) throws InterruptedException {
        String responseBody=null;
        String uri = request.getRequestUrl().queryParameter("uri");
        String query = request.getRequestUrl().queryParameter("query");
        
        try {
          if(request.getBody()!=null && request.getBody().size() > 0) {
            responseBody = AnnotationTestUtils.loadFile(
                AnnotationTestUtils.METIS_RESPONSE_MAP.getOrDefault(
                AnnotationTestsConstants.DEREFERENCE_MANY, AnnotationTestsConstants.EMPTY_METIS_RESPONSE));
          }
          else if(uri != null){
            //handle METIS deref request
            responseBody = AnnotationTestUtils.loadFile(
                AnnotationTestUtils.METIS_RESPONSE_MAP.getOrDefault(
                uri, AnnotationTestsConstants.EMPTY_METIS_RESPONSE));
          } else if(query != null){
            //handle SEARCH API request
            String itemId = StringUtils.substringAfterLast(query.replaceAll("\"", ""), '/');
            //currently we have only one qf, but we might have more
            String qf = request.getRequestUrl().queryParameter("qf");
            assertNotNull(qf);
            String organization = StringUtils.substringAfter(qf.replaceAll("\"", ""), ':'); 
            
            String key = buildAffiliationSetKey(organization, itemId);
            
            responseBody = SEARCH_API_AFFILIATION_CHECK.contains(key) ? 
                SEARCH_API_FOUND : SEARCH_API_NOT_FOUND; 
          }
          return new MockResponse().setResponseCode(200).setBody(responseBody);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };
  }


  private void initWhiteListCollection() {
    List<? extends WhitelistEntry> whiteList = whitelistService.getWhitelist();
    if(whiteList.isEmpty()) {
      whitelistService.loadWhitelistFromResources();
    }
  }

  private void disableOauth() {
    ((AnnotationConfigurationImpl) configuration).getAnnotationProperties()
        .put(AnnotationConfiguration.ANNOTATION_AUTH_DISABLED, "true");
  }


  @AfterEach
  private void removeCreatedAnnotations()
      throws AnnotationMongoException, AnnotationServiceException, ModerationMongoException {
    for (long identifier : createdAnnotations) {
      removeAnnotationManually(identifier);
    }
    createdAnnotations.clear();

    for (long identifier : createdModerationRecords) {
      mongoPersistenceModeration.remove(identifier);
    }
    createdModerationRecords.clear();
  }

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("mongodb.annotation.connectionUrl", MONGO_CONTAINER::getConnectionUrl);
    registry.add("solr.annotation.url", SOLR_CONTAINER::getConnectionUrl);
    registry.add(
        "metis.baseUrl",
        () -> String.format("http://%s:%s", mockMetisAndSearch.getHostName(), mockMetisAndSearch.getPort()));
    registry.add(
        "searchApi.baseUrl",
        () -> String.format("http://%s:%s?wskey=testapikey", mockMetisAndSearch.getHostName(), mockMetisAndSearch.getPort()));  
  }

  protected String getAuthorizationHeaderValue(String user) {
    String authorization;
    if(user == null) {
      //allow tests to use the default user without explicitly stating it
      user = USER_REGULAR;
    }
    switch (user) {
      case USER_ADMIN:
        authorization = adminUserAuthorizationValue;
        break;
      case USER_PUBLISHER: //TO CHECK why TOKEN_PUBLISHER was used before
        authorization = publisherUserAuthorizationValue;
        break;
      case USER_PROVIDER_SAZ:
        authorization = providerSazUserAuthorizationValue;
        break;
      case USER_REGULAR:
      default:
         authorization = regularUserAuthorizationValue;
         break;
    }
    
    return authorization;
  }

  protected Logger log = LogManager.getLogger(getClass());

  protected ResponseEntity<String> storeTestAnnotation(String inputFile, boolean indexOnCreate) throws Exception {

    return storeTestAnnotation(inputFile, indexOnCreate, USER_REGULAR);
  }
  
  protected ResponseEntity<String> storeTestAnnotation(String inputFile, boolean indexOnCreate,
      String user) throws Exception {

    String anno = null;
    if (inputFile != null) {
      anno = AnnotationTestUtils.getJsonStringInput(inputFile);
    }

    return storeTestAnnotationFromJson(anno, indexOnCreate, user);
  }
  
  protected ResponseEntity<String> storeTestAnnotationFromJson(String jsonAnno, boolean indexOnCreate,
	      String user) throws Exception {
	  String url = AnnotationTestsConfiguration.BASE_SERVICE_URL;

	  ResultActions mockMvcResult = null;
	  if (jsonAnno != null) {
	    mockMvcResult = mockMvc.perform(
	        post(url).queryParam(WebAnnotationFields.INDEX_ON_CREATE, String.valueOf(indexOnCreate))
	            .content(jsonAnno).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
	            .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeaderValue(user)));
	  } else {
	    mockMvcResult = mockMvc.perform(
	        post(url).queryParam(WebAnnotationFields.INDEX_ON_CREATE, String.valueOf(indexOnCreate))
	            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
	            .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeaderValue(user)));
	  }
	  
	  ResponseEntity<String> response = AnnotationTestUtils.buildResponseEntity(mockMvcResult);
	  //store annotation for deletion
	  if(response.getStatusCode().is2xxSuccessful()) {	    
	    long identifier = AnnotationTestUtils.parseResponseBody(response).getIdentifier();
        addToCreatedAnnotations(identifier);
	  }
	
	  return AnnotationTestUtils.buildResponseEntity(mockMvcResult);
  }

  protected void addToCreatedAnnotations(long identifier) {
    if(!createdAnnotations.contains(Long.valueOf(identifier))) {
      createdAnnotations.add(identifier);
    }
  }
  

  protected ResponseEntity<String> storeTestAnnotationReport(String apiKey, long identifier,
      String userToken) throws Exception {

    String url = AnnotationTestsConfiguration.BASE_SERVICE_URL;
    url += identifier + WebAnnotationFields.SLASH;
    url += WebAnnotationFields.PATH_FIELD_REPORT;
    url += WebAnnotationFields.PAR_CHAR;
    url += WebAnnotationFields.IDENTIFIER + "=" + identifier;

    ResultActions mockMvcResult = mockMvc.perform(
        post(url).header(HttpHeaders.AUTHORIZATION, getAuthorizationHeaderValue(USER_REGULAR)));

    return AnnotationTestUtils.buildResponseEntity(mockMvcResult);
  }

  protected ResponseEntity<String> getModerationReport(String apiKey, long identifier,
      String userToken) throws Exception {

    String url = AnnotationTestsConfiguration.BASE_SERVICE_URL;
    url += identifier + WebAnnotationFields.SLASH;
    url += WebAnnotationFields.PATH_FIELD_MODERATION_SUMMARY;
    url += WebAnnotationFields.PAR_CHAR;
    url += WebAnnotationFields.IDENTIFIER + "=" + identifier + "&";
    url += WebAnnotationFields.PARAM_WSKEY + "=" + apiKey;

    ResultActions mockMvcResult = mockMvc.perform(
        get(url).header(HttpHeaders.AUTHORIZATION, getAuthorizationHeaderValue(USER_REGULAR)));

    return AnnotationTestUtils.buildResponseEntity(mockMvcResult);
  }

  protected Annotation createTestAnnotation(String inputFile, boolean indexOnCreate)
      throws Exception {
    ResponseEntity<String> response = storeTestAnnotation(inputFile, indexOnCreate, USER_REGULAR);
    Annotation annotation = AnnotationTestUtils.parseAndVerifyTestAnnotation(response);
    return annotation;
  }
  
  protected Annotation createTestAnnotation(String inputFile, boolean indexOnCreate, String user)
      throws Exception {
    ResponseEntity<String> response = storeTestAnnotation(inputFile, indexOnCreate, user);
    Annotation annotation = AnnotationTestUtils.parseAndVerifyTestAnnotation(response);
    return annotation;
  }
  
  protected Annotation createTestAnnotationFromJson(String annoJson, boolean indexOnCreate, String user)
		  throws Exception {
	  ResponseEntity<String> response = storeTestAnnotationFromJson(annoJson, indexOnCreate, user);
	  Annotation annotation = AnnotationTestUtils.parseAndVerifyTestAnnotation(response);
	  return annotation;
  }

  
  protected ResponseEntity<String> storeTestAnnotationByType(boolean indexOnCreate, String body,
      String annoType, String user) throws Exception {

    String url = AnnotationTestsConfiguration.BASE_SERVICE_URL;
    if (annoType != null)
      url += annoType + ".jsonld";

    ResultActions mockMvcResult = mockMvc.perform(
        post(url).queryParam(WebAnnotationFields.INDEX_ON_CREATE, String.valueOf(indexOnCreate))
            .content(body).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeaderValue(user)));

    return AnnotationTestUtils.buildResponseEntity(mockMvcResult);
  }

  protected Annotation createTag(String requestBody) throws Exception {
    ResponseEntity<String> response =
        storeTestAnnotationByType(true, requestBody, WebAnnotationFields.TAG, null);
    Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
    return storedAnno;
  }

  protected Annotation createTag(String inputFile, boolean validate, boolean indexOnCreate)
      throws Exception {

    log.debug("Input File: " + inputFile);

    String requestBody = AnnotationTestUtils.getJsonStringInput(inputFile);

    Annotation storedAnno = createTestAnnotation(inputFile, indexOnCreate, null);

    Annotation inputAnno = parseTag(requestBody);

    // validate the reflection of input in output!
    if (validate) {
      AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
    }

    return storedAnno;
  }

  protected Annotation createLink(String requestBody) throws Exception {
    ResponseEntity<String> response =
        storeTestAnnotationByType(true, requestBody, WebAnnotationFields.LINK, null);
    Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
    return storedAnno;
  }

  protected ResponseEntity<String> deleteAnnotation(long identifier) throws Exception {
    String url = AnnotationTestsConfiguration.BASE_SERVICE_URL;
    url += identifier;
    ResultActions mockMvcResult = mockMvc
        .perform(delete(url).header(HttpHeaders.AUTHORIZATION, getAuthorizationHeaderValue(null)));
    return AnnotationTestUtils.buildResponseEntity(mockMvcResult);
  }

  protected void removeAnnotationManually(long identifier)
      throws AnnotationMongoException, AnnotationServiceException {
    mongoPersistance.remove(identifier);
    solrService.delete(identifier);
    log.trace("Annotation deleted: /" + identifier);
  }

  protected List<String> getDeleted(String motivation, String from, String to, int page, int limit)
      throws Exception {
    String url = AnnotationTestsConfiguration.BASE_SERVICE_URL_WITH_S;
    url += "deleted";
    boolean hasAtLeastOneParam = false;
    if (from != null) {
      if (!hasAtLeastOneParam) {
        url += "?from" + "=" + from;
        hasAtLeastOneParam = true;
      } else
        url += "&" + "from" + "=" + from;
    }
    if (to != null) {
      if (!hasAtLeastOneParam) {
        url += "?to" + "=" + to;
        hasAtLeastOneParam = true;
      } else
        url += "&" + "to" + "=" + to;
    }
    url += "&" + "page" + "=" + page;
    url += "&" + "limit" + "=" + limit;
    if (motivation != null) {
      if (!hasAtLeastOneParam) {
        url += WebAnnotationFields.MOTIVATION + "=" + motivation;
        hasAtLeastOneParam = true;
      } else
        url += "&" + WebAnnotationFields.MOTIVATION + "=" + motivation;
    }
    url += "&" + WebAnnotationFields.PARAM_WSKEY + "="
        + AnnotationTestsConfiguration.getInstance().getApiKey();

    ResultActions mockMvcResult = mockMvc.perform(get(url));
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(mockMvcResult.andReturn().getResponse().getContentAsString(),
        new TypeReference<List<String>>() {});

  }

  protected Annotation[] createMultipleTestAnnotations(String defaultRequestBody,
      Integer numTestAnno) throws Exception {

    Annotation[] testAnnotations = new Annotation[numTestAnno];
    for (int i = 0; i < numTestAnno; i++) {
      String adaptedRequestBody =
          StringUtils.replace(defaultRequestBody, "%test_body_to_replace%", "test_body_" + i);
      ResponseEntity<String> response =
          storeTestAnnotationByType(true, adaptedRequestBody, null, null);
      Annotation annotation = AnnotationTestUtils.parseAndVerifyTestAnnotation(response);
      assertNotNull(annotation);
      testAnnotations[i] = annotation;
    }
    return testAnnotations;
  }

  protected ResponseEntity<String> getAnnotation(Annotation anno) throws Exception {
    return getAnnotation(AnnotationTestsConfiguration.getInstance().getApiKey(),
        anno.getIdentifier(), false, null);
  }

  protected ResponseEntity<String> getAnnotation(String apiKey, long identifier,
      boolean isTypeJsonld, SearchProfiles searchProfile) throws Exception {
    String url = AnnotationTestsConfiguration.BASE_SERVICE_URL;
    url += identifier;
    if (isTypeJsonld) {
      url += WebAnnotationFields.JSONLD_REST;
    }
    url += WebAnnotationFields.PAR_CHAR;
    url += WebAnnotationFields.PARAM_WSKEY + "=" + apiKey;
    if (searchProfile != null) {
      url += "&" + WebAnnotationFields.PARAM_PROFILE + "=" + searchProfile.toString();
    }

    ResultActions mockMvcResult = mockMvc.perform(get(url));

    return AnnotationTestUtils.buildResponseEntity(mockMvcResult);

  }

  protected ResponseEntity<String> updateAnnotation(long identifier, String requestBody,
      String user) throws Exception {
    String url = AnnotationTestsConfiguration.BASE_SERVICE_URL;
    url += identifier;

    ResultActions mockMvcResult = mockMvc.perform(put(url).content(requestBody)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeaderValue(user)));

    return AnnotationTestUtils.buildResponseEntity(mockMvcResult);

  }

  protected AnnotationPage searchAnnotationsAddQueryField(String query, String page,
      String pageSize, String field, String language, String searchProfile,
      String paramLanguage) throws Exception {

    if (StringUtils.isNotEmpty(field) && StringUtils.isNotEmpty(language)) {
      query = AnnotationTestUtils.addFieldToQuery(query, field, language);
    }
    return searchAnnotations(query, null, null, null, null, page, pageSize, searchProfile, paramLanguage);
  }

  protected AnnotationPage searchAnnotations(String query, String[] qf, String[] facets, String sort, String sortOrder,
      String page, String pageSize, String searchProfile, String language)
      throws Exception {

    String url = AnnotationTestUtils.buildUrl(query, qf, facets, sort, sortOrder, page, pageSize, searchProfile, language);
    ResultActions mockMvcResult = mockMvc.perform(get(url));
    return AnnotationTestUtils
        .getAnnotationPage(mockMvcResult.andReturn().getResponse().getContentAsString());

  }

  protected ResponseEntity<String> reindexOutdated() throws Exception {

    String url = AnnotationTestsConfiguration.BASE_SERVICE_URL_ADMIN + "reindexoutdated";
    log.trace("(Re)index outdated annotations request URL: " + url);

    ResultActions mockMvcResult = mockMvc.perform(
        put(url).header(HttpHeaders.AUTHORIZATION, getAuthorizationHeaderValue(USER_ADMIN)));

    return AnnotationTestUtils.buildResponseEntity(mockMvcResult);
  }

  protected ResponseEntity<String> uploadAnnotations(String tag, Boolean indexOnCreate)
      throws Exception {
    String url = AnnotationTestsConfiguration.BASE_SERVICE_URL_WITH_S;
    log.debug("Upload annotations request URL: " + url);
    /**
     * Execute Europeana API request
     */
    ResultActions mockMvcResult = mockMvc.perform(
        post(url).content(tag).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeaderValue(USER_REGULAR)));

    return AnnotationTestUtils.buildResponseEntity(mockMvcResult);
  }

  protected String searchAnnotationLd(String target, String resourceId) throws Exception {
    String url = AnnotationTestsConfiguration.BASE_SERVICE_URL;
    url += WebAnnotationFields.SEARCH_JSON_LD_REST + WebAnnotationFields.PAR_CHAR;
    url += WebAnnotationFields.PARAM_WSKEY + "=" + "ws" + "&";
    if (StringUtils.isNotEmpty(target))
      url += WebAnnotationFields.TARGET + "=" + target;
    if (StringUtils.isNotEmpty(resourceId))
      url += WebAnnotationFields.RESOURCE_ID + "=" + resourceId;

    ResultActions mockMvcResult = mockMvc.perform(get(url));
    return mockMvcResult.andReturn().getResponse().getContentAsString();
  }

  protected Annotation createAnnotationLd(String motivation, Long annotationNr,
      String europeanaLdStr, String apikey) throws Exception {

    String url = AnnotationTestsConfiguration.BASE_SERVICE_URL;
    url += WebAnnotationFields.PAR_CHAR;
    String resApiKey = AnnotationTestsConfiguration.getInstance().getApiKey();
    if (apikey != null) {
      resApiKey = apikey;
    }
    url += WebAnnotationFields.PARAM_WSKEY + "=" + resApiKey + "&";
    url += WebAnnotationFields.USER_TOKEN + "=" + "tester1" + "&";
    if (annotationNr != null)
      url += WebAnnotationFields.IDENTIFIER + "=" + annotationNr + "&";
    url += WebAnnotationFields.INDEX_ON_CREATE + "=" + "true";

    ResultActions mockMvcResult = mockMvc.perform(post(url).content(europeanaLdStr)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeaderValue(USER_REGULAR)));

    return AnnotationTestUtils
        .parseAnnotation(mockMvcResult.andReturn().getResponse().getContentAsString(), null);
  }

  protected Annotation parseTag(String jsonString) throws JsonParseException {
    MotivationTypes motivationType = MotivationTypes.TAGGING;
    return AnnotationTestUtils.parseAnnotation(jsonString, motivationType);
  }

  public AnnotationConfiguration getConfiguration() {
    return configuration;
  }


}

package eu.europeana.annotation.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
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
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.ModerationMongoException;
import eu.europeana.annotation.mongo.service.PersistentAnnotationService;
import eu.europeana.annotation.mongo.service.PersistentModerationRecordService;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.service.SolrAnnotationService;
import eu.europeana.annotation.tests.config.AnnotationTestsConfiguration;
import eu.europeana.annotation.tests.utils.AnnotationTestUtils;
import eu.europeana.annotation.tests.utils.EuropeanaOauthClient;
import eu.europeana.annotation.tests.utils.MongoContainer;
import eu.europeana.annotation.tests.utils.SolrContainer;
import eu.europeana.annotation.web.service.WhitelistService;

//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(locations = { "classpath:annotation-web-context.xml" })
//@WebAppConfiguration

@ComponentScan(basePackageClasses = AnnotationBasePackageMapper.class)
@AutoConfigureMockMvc
@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AbstractIntegrationTest extends AnnotationTestsConstants{
  
    static String regularUserAuthorizationValue = null;
    static String adminUserAuthorizationValue = null;
    protected static List<Long> createdAnnotations = new ArrayList<Long>();
    protected static List<Long> createdModerationRecords = new ArrayList<Long>();
  
    protected MockMvc mockMvc;
    
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
    
    private static final MongoContainer MONGO_CONTAINER;
    private static final SolrContainer SOLR_CONTAINER;

    static {
      MONGO_CONTAINER =
          new MongoContainer("annotation_test")
              .withLogConsumer(new WaitingConsumer().andThen(new ToStringConsumer()));

      MONGO_CONTAINER.start();

      SOLR_CONTAINER =
          new SolrContainer("anno-up")
              .withLogConsumer(new WaitingConsumer().andThen(new ToStringConsumer()));

      SOLR_CONTAINER.start();
    }
    
    @BeforeEach
    protected void initTest() {
      mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    @BeforeAll
    protected void initAppConextAndOauthTokens() throws Exception {
      regularUserAuthorizationValue = EuropeanaOauthClient.getOauthToken(USER_REGULAR, AnnotationTestsConfiguration.getInstance().getOauthServiceUri(), AnnotationTestsConfiguration.getInstance().getOauthRequestParams(USER_REGULAR));
      adminUserAuthorizationValue = EuropeanaOauthClient.getOauthToken(USER_ADMIN, AnnotationTestsConfiguration.getInstance().getOauthServiceUri(), AnnotationTestsConfiguration.getInstance().getOauthRequestParams(USER_ADMIN));
      whitelistService.loadWhitelistFromResources();
    }
    
    @AfterAll
    protected void deleteWhitelists() throws Exception {
      whitelistService.deleteWholeWhitelist();
    }
    
    @AfterEach
    private void removeCreatedAnnotations () throws AnnotationMongoException, AnnotationServiceException, ModerationMongoException {
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
      Supplier<Object> hostSupplier= ()-> MONGO_CONTAINER.getHost();
      registry.add("mongodb.annotation.host", hostSupplier);
      Supplier<Object> portSupplier= ()-> MONGO_CONTAINER.getMappedPort(27017);
      registry.add("mongodb.annotation.port", portSupplier);
      Supplier<Object> dbnameSupplier= ()-> MONGO_CONTAINER.getAnnotationDb();
      registry.add("mongodb.annotation.databasename", dbnameSupplier);
      Supplier<Object> adminUserSupplier= ()-> MONGO_CONTAINER.getAdminUsername();
      registry.add("mongodb.annotation.username", adminUserSupplier);
      Supplier<Object> adminPassSupplier= ()-> MONGO_CONTAINER.getAdminPassword();
      registry.add("mongodb.annotation.password", adminPassSupplier);
      registry.add("solr.annotation.url", SOLR_CONTAINER::getConnectionUrl);
    }
    
    protected String getAuthorizationHeaderValue (String user) {
      if (user!=null && user.equals(USER_ADMIN)) {
          return adminUserAuthorizationValue;
      }
      else {
          return regularUserAuthorizationValue;
      }
    }
  
	protected Logger log = LogManager.getLogger(getClass());
	
	protected ResponseEntity<String> storeTestAnnotation(String inputFile, boolean indexOnCreate, String user) throws Exception {

	  String anno = null;
	  if(inputFile!=null) {
	    anno = AnnotationTestUtils.getJsonStringInput(inputFile);
	  }
	      
	  String url = AnnotationTestsConfiguration.BASE_SERVICE_URL;
	  
	  ResultActions mockMvcResult = null;
	  if(anno!=null) {
	    mockMvcResult= mockMvc.perform(
	    post(url)
		.queryParam(WebAnnotationFields.INDEX_ON_CREATE, String.valueOf(indexOnCreate))
	    .content(anno)
	    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
	    .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeaderValue(user)));
	  }
	  else {
	    mockMvcResult = mockMvc.perform(
        post(url)
        .queryParam(WebAnnotationFields.INDEX_ON_CREATE, String.valueOf(indexOnCreate))
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeaderValue(user)));	    
	  }

	  return AnnotationTestUtils.buildResponseEntity(mockMvcResult);
	}		

	protected ResponseEntity<String> storeTestAnnotationReport(
			String apiKey
			, long identifier
			, String userToken) throws Exception {
  
	    String url = AnnotationTestsConfiguration.BASE_SERVICE_URL;
	    url += identifier + WebAnnotationFields.SLASH;
	    url += WebAnnotationFields.PATH_FIELD_REPORT;
	    url += WebAnnotationFields.PAR_CHAR;
	    url += WebAnnotationFields.IDENTIFIER + "=" + identifier;

        ResultActions mockMvcResult = mockMvc.perform(
            post(url)
            .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeaderValue(USER_REGULAR)));
	  
        return AnnotationTestUtils.buildResponseEntity(mockMvcResult);
	}

	protected ResponseEntity<String> getModerationReport(
			String apiKey
			, long identifier
			, String userToken) throws Exception {

	    String url = AnnotationTestsConfiguration.BASE_SERVICE_URL;
	    url += identifier + WebAnnotationFields.SLASH;
	    url += WebAnnotationFields.PATH_FIELD_MODERATION_SUMMARY;
	    url += WebAnnotationFields.PAR_CHAR;
	    url += WebAnnotationFields.IDENTIFIER + "=" + identifier + "&";
	    url += WebAnnotationFields.PARAM_WSKEY + "=" + apiKey;
	  
        ResultActions mockMvcResult = mockMvc.perform(
            get(url)
            .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeaderValue(USER_REGULAR)));
      
        return AnnotationTestUtils.buildResponseEntity(mockMvcResult);
	}

	protected Annotation createTestAnnotation(String inputFile, boolean indexOnCreate, String user) throws Exception {
		ResponseEntity<String> response = storeTestAnnotation(inputFile, indexOnCreate, user);
		Annotation annotation = AnnotationTestUtils.parseAndVerifyTestAnnotation(response);
		return annotation;		
	}

    protected ResponseEntity<String> storeTestAnnotationByType(boolean indexOnCreate, String body, String annoType, String user) throws Exception {

      String url = AnnotationTestsConfiguration.BASE_SERVICE_URL;
      if (annoType != null)
          url += annoType + ".jsonld";
      
      ResultActions mockMvcResult = mockMvc.perform(
          post(url)
          .queryParam(WebAnnotationFields.INDEX_ON_CREATE, String.valueOf(indexOnCreate))
          .content(body)
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
          .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeaderValue(user)));

      return AnnotationTestUtils.buildResponseEntity(mockMvcResult);
    }
    
	protected Annotation createTag(String requestBody) throws Exception {
	    ResponseEntity<String> response = storeTestAnnotationByType(true, requestBody,  WebAnnotationFields.TAG, null);
	    Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
	    return storedAnno;
	}

	protected Annotation createLink(String requestBody) throws Exception {
	    ResponseEntity<String> response = storeTestAnnotationByType(true, requestBody,  WebAnnotationFields.LINK, null);
		Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
		return storedAnno;
	}
	
	protected ResponseEntity<String> deleteAnnotation (long identifier) throws Exception {
	  String url = AnnotationTestsConfiguration.BASE_SERVICE_URL;
	  url += identifier;
      ResultActions mockMvcResult = mockMvc.perform(
          delete(url)
          .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeaderValue(null)));
      return AnnotationTestUtils.buildResponseEntity(mockMvcResult);
	}
	
    protected void removeAnnotationManually(long identifier) throws AnnotationMongoException, AnnotationServiceException {
        mongoPersistance.remove(identifier);
        solrService.delete(identifier);
		log.trace("Annotation deleted: /" + identifier);
	}
    
    protected List<String> getDeleted (String motivation, String from, String to, int page, int limit) throws Exception {
      String url = AnnotationTestsConfiguration.BASE_SERVICE_URL_WITH_S;
      url += "deleted";
      boolean hasAtLeastOneParam = false;
      if(from!=null) {
          if(!hasAtLeastOneParam) {
              url += "?from" + "=" + from;
              hasAtLeastOneParam=true;
          }
          else url += "&" + "from" + "=" + from;
      }
      if(to!=null) {
          if(!hasAtLeastOneParam) {
              url += "?to" + "=" + to;
              hasAtLeastOneParam=true;
          }
          else url += "&" + "to" + "=" + to;
      }
      url += "&" + "page" + "=" + page;
      url += "&" + "limit" + "=" + limit;
      if(motivation!=null) {
          if(!hasAtLeastOneParam) {
              url += WebAnnotationFields.MOTIVATION + "=" + motivation;
              hasAtLeastOneParam=true;
          }
          else url += "&" + WebAnnotationFields.MOTIVATION + "=" + motivation;
      }
      url += "&" + WebAnnotationFields.PARAM_WSKEY + "=" + AnnotationTestsConfiguration.getInstance().getApiKey();
      
      ResultActions mockMvcResult = mockMvc.perform(get(url));          
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(mockMvcResult.andReturn().getResponse().getContentAsString(), new TypeReference<List<String>>(){});    

    }
    
    protected Annotation[] createMultipleTestAnnotations(String defaultRequestBody, Integer numTestAnno) throws Exception {

      Annotation[] testAnnotations = new Annotation[numTestAnno];
      for (int i = 0; i < numTestAnno; i++) {
        String adaptedRequestBody = StringUtils.replace(defaultRequestBody, "%test_body_to_replace%", "test_body_" + i);
        ResponseEntity<String> response = storeTestAnnotationByType(
                true, adaptedRequestBody, null, null);
        Annotation annotation = AnnotationTestUtils.parseAndVerifyTestAnnotation(response); 
        assertNotNull(annotation);
        testAnnotations[i] = annotation;
      }
      return testAnnotations;
    }

    protected ResponseEntity<String> getAnnotation(Annotation anno) throws Exception {
      return getAnnotation(AnnotationTestsConfiguration.getInstance().getApiKey(), anno.getIdentifier(), false, null);
    }

	protected ResponseEntity<String> getAnnotation(String apiKey, long identifier, boolean isTypeJsonld, SearchProfiles searchProfile) throws Exception {
	    String url = AnnotationTestsConfiguration.BASE_SERVICE_URL;
	    url += identifier;
	    if (isTypeJsonld) {
	        url += WebAnnotationFields.JSONLD_REST;
	    }
	    url += WebAnnotationFields.PAR_CHAR;
	    url += WebAnnotationFields.PARAM_WSKEY + "=" + apiKey;
	    if(searchProfile!=null) {
	      url += "&" + WebAnnotationFields.PARAM_PROFILE + "=" + searchProfile.toString();
	    }
	  
	    ResultActions mockMvcResult = mockMvc.perform(
	        get(url));

	    return AnnotationTestUtils.buildResponseEntity(mockMvcResult);

	}
	
	protected ResponseEntity<String> updateAnnotation(long identifier, String requestBody, String user) throws Exception {
	  String url = AnnotationTestsConfiguration.BASE_SERVICE_URL;
	  url += identifier;
	  
      ResultActions mockMvcResult = mockMvc.perform(
          put(url)          
          .content(requestBody)
          .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
          .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeaderValue(user)));
      
      return AnnotationTestUtils.buildResponseEntity(mockMvcResult);

	}
		
    protected AnnotationPage searchAnnotationsAddQueryField(
            String query, String page, String pageSize, String field, String language, SearchProfiles searchProfile, String paramLanguage) throws Exception {
        
        if (StringUtils.isNotEmpty(field) && StringUtils.isNotEmpty(language))  {
            query = AnnotationTestUtils.addFieldToQuery(query, field, language);
        }        
        return searchAnnotations(query, null, null, null, page, pageSize, searchProfile, paramLanguage);
    }
    
    protected AnnotationPage searchAnnotations(String query, String qf, String sort, String sortOrder, String page, String pageSize,
        SearchProfiles searchProfile, String language) throws Exception {

    String url = AnnotationTestUtils.buildUrl(query, qf, sort, sortOrder, page, pageSize, searchProfile.toString(), language);
    ResultActions mockMvcResult = mockMvc.perform(get(url));          
    return AnnotationTestUtils.getAnnotationPage(mockMvcResult.andReturn().getResponse().getContentAsString());
    
    }

    

    protected ResponseEntity<String> reindexOutdated() throws Exception {

    String url = AnnotationTestsConfiguration.BASE_SERVICE_URL_ADMIN + "reindexoutdated";
    log.trace("(Re)index outdated annotations request URL: " + url);
    
    ResultActions mockMvcResult = mockMvc.perform(
        put(url)
        .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeaderValue(USER_ADMIN)));
    
    return AnnotationTestUtils.buildResponseEntity(mockMvcResult);
    }
	
    protected ResponseEntity<String> uploadAnnotations(String tag, Boolean indexOnCreate) throws Exception {
    String url = AnnotationTestsConfiguration.BASE_SERVICE_URL_WITH_S;
    log.debug("Upload annotations request URL: " + url);
    /**
     * Execute Europeana API request
     */
    ResultActions mockMvcResult = mockMvc.perform(
        post(url)
        .content(tag)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
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

    ResultActions mockMvcResult = mockMvc.perform(
        post(url)
        .content(europeanaLdStr)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeaderValue(USER_REGULAR)));
    
    return AnnotationTestUtils.parseAnnotation(mockMvcResult.andReturn().getResponse().getContentAsString(), null);
    }
    
//    /**
//     * Sample HTTP request http://localhost:8080/whitelist/load?apiKey=apidemo
//     * 
//     * @return WhitelistOperationResponse
//     * @throws Exception 
//     */
//    protected WhitelistOperationResponse loadWhitelist() throws Exception {
//      String action = "load";
//      String url = AnnotationTestsConfiguration.BASE_SERVICE_URL_WHITELIST + action;
//      ResultActions mockMvcResult = mockMvc.perform(
//          get(url)
//          .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeaderValue(USER_ADMIN)));
//      String jsonResponse = mockMvcResult.andReturn().getResponse().getContentAsString();
//  
//      WhitelistOperationResponse aor = new WhitelistOperationResponse(AnnotationTestsConfiguration.getInstance().getApiKey(), action);
//      List<WhitelistEntry> resList = new ArrayList<WhitelistEntry>();
//      JSONObject mainObject = new JSONObject(jsonResponse);
//      JSONArray whitelistEntries = mainObject.getJSONArray("items");
//      for (int i = 0; i < whitelistEntries.length(); i++) {
//        JSONObject entry = (JSONObject) whitelistEntries.get(i);
//        WhitelistEntry whitelistEntry = WhiteListParser.toWhitelistEntry(entry.toString());
//        resList.add(whitelistEntry);
//      }
//      aor.setWhitelist(resList);
//      return aor;
//    }
//    
//    protected WhitelistOperationResponse deleteAllWhitelists () throws Exception {
//      String url = AnnotationTestsConfiguration.BASE_SERVICE_URL_WHITELIST + "deleteall";
//      ResultActions mockMvcResult = mockMvc.perform(
//          delete(url)
//          .header(HttpHeaders.AUTHORIZATION, getAuthorizationHeaderValue(USER_ADMIN)));
//      String jsonResponse = mockMvcResult.andReturn().getResponse().getContentAsString();
//      
//      ObjectMapper mapper = new ObjectMapper();
//      return mapper.readValue(jsonResponse, WhitelistOperationResponse.class);
//    }

}

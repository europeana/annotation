package eu.europeana.annotation.tests.web;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.body.GraphBody;
import eu.europeana.annotation.definitions.model.body.TagBody;
import eu.europeana.annotation.definitions.model.body.impl.EdmPlaceBody;
import eu.europeana.annotation.definitions.model.body.impl.FullTextResourceBody;
import eu.europeana.annotation.definitions.model.body.impl.SemanticTagBody;
import eu.europeana.annotation.definitions.model.body.impl.TextBody;
import eu.europeana.annotation.definitions.model.entity.impl.EdmPlace;
import eu.europeana.annotation.definitions.model.graph.Graph;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.tests.AbstractIntegrationTest;
import eu.europeana.annotation.tests.config.AnnotationTestsConfiguration;
import eu.europeana.annotation.tests.utils.AnnotationTestUtils;
import eu.europeana.annotation.utils.GeneralUtils;

/**
 * Annotation search API test class
 * 
 * @author Sergiu Gordea @ait
 * @author Sven Schlarb @ait
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AnnotationSearchIT extends AbstractIntegrationTest {

  static final String VALUE_ALL = "*:*";
  static final int TOTAL_IN_PAGE = 10;
  static final int TOTAL_IN_COLLECTION = 21;
  public static final String TAG_STANDARD_TESTSET = "/tag/standard_testset.json";
  static final String VALUE_ID = "anno_id:";
  static final String VALUE_DESCRIBING_BODY_VALUE =
      "body_value:\"... this is the textual description of the item ...\"";
  static final String VALUE_BODY_VALUE = "body_value:\"trombone\"";
  static final String VALUE_BODY_SPECIFIC_RESOURCE = "body_uri:\"http://www.geonames.org/2988507\""; // source
  static final int TOTAL_BY_ID_FOUND = 1;
  static final String SIMPLE_LINK_ANNOTATION = "/link/simple-annotation.json";
  static final String QUERY_ID = "http://viaf.org/viaf/51961439";
  static final String SEARCH_VALUE_TEST = "body_uri:\"" + QUERY_ID + "\"";
  static final String TEST_LANGUAGE = "en,en-US";
  static final String TEST_LANGUAGE_MULTI = "en,en-US,it,fr";

  private Annotation[] annotations;

  private void createAnnotationDataSet() throws Exception {
    String defaultRequestBody = AnnotationTestUtils.getJsonStringInput(TAG_STANDARD_TESTSET);
    annotations = createMultipleTestAnnotations(defaultRequestBody, TOTAL_IN_COLLECTION);
    for (Annotation anno : annotations) {
      addToCreatedAnnotations(anno.getIdentifier());
    }
    assertEquals(TOTAL_IN_COLLECTION, annotations.length);
  }

  /**
   * Check if the next page URI points to the correct page number
   * 
   * @param annPg Annotation page object
   * @param expPgNum Expected page number
   * @throws MalformedURLException
   */
  private void assertNextPageNumber(AnnotationPage annPg, Integer expPgNum)
      throws MalformedURLException {
    String nextPageUri = annPg.getNextPageUri();
    Integer nextPgNum = GeneralUtils.getQueryParamNumValue(nextPageUri, WebAnnotationFields.PAGE);
    log.debug(nextPageUri);
    log.debug(nextPgNum);
    assertEquals(expPgNum, nextPgNum);
  }

  /**
   * Test search query and verify search result
   * 
   * @throws Exception
   */
  @Test
  public void testSearchAnnotationPaging() throws Exception {
    createAnnotationDataSet();
    // first page
    String query = "*:*";
    AnnotationPage annPg =
        searchAnnotationsAddQueryField(query, null, null, null, null, SearchProfiles.MINIMAL.toString(), null);
    assertNotNull(annPg, "AnnotationPage must not be null");
    // there might be old annotations of failing tests in the database
    assertTrue(TOTAL_IN_COLLECTION <= annPg.getTotalInCollection());
    assertEquals(annPg.getCurrentPage(), 0);
    assertEquals(TOTAL_IN_PAGE, annPg.getTotalInPage());
    assertEquals(TOTAL_IN_PAGE, annPg.getItems().getResultSize());
    assertNextPageNumber(annPg, 1);

    // second page
    String npUri = annPg.getNextPageUri();
    // adapt the base url
    String annoNpUriNew = StringUtils.replace(npUri, StringUtils.substringBefore(npUri, "search?"),
        AnnotationTestsConfiguration.BASE_SERVICE_URL);
    // here we need to decode the value returned
    annoNpUriNew = URLDecoder.decode(annoNpUriNew, StandardCharsets.UTF_8.toString());
    String nextPageJson =
        mockMvc.perform(get(annoNpUriNew)).andReturn().getResponse().getContentAsString();
    AnnotationPage secondAnnoPg = AnnotationTestUtils.getAnnotationPage(nextPageJson);
    String currentPageUri = secondAnnoPg.getCurrentPageUri();
    log.debug("currentPageUri" + currentPageUri);
    String nextCurrentPageUri = secondAnnoPg.getNextPageUri();
    log.debug("nextCurrentPageUri" + nextCurrentPageUri);
    assertNotNull(secondAnnoPg);
    assertEquals(secondAnnoPg.getCurrentPage(), 1);
    assertNextPageNumber(secondAnnoPg, 2);
    assertEquals(TOTAL_IN_PAGE, secondAnnoPg.getTotalInPage());
    assertEquals(TOTAL_IN_PAGE, secondAnnoPg.getItems().getResultSize());

    // last page
    int lastPageNum = (int) Math.ceil((TOTAL_IN_COLLECTION - 1) / TOTAL_IN_PAGE);
    AnnotationPage lastPage = searchAnnotationsAddQueryField(query, Integer.toString(lastPageNum),
        Integer.toString(TOTAL_IN_PAGE), null, null, SearchProfiles.STANDARD.toString(), null);
    assertEquals(lastPage.getCurrentPage(), lastPageNum);
  }

  /*
   * Test search query and verify search result
   * 
   * @throws Exception
   */
  @Test
  public void testSearchAnyAnnotation() throws Exception {
    createAnnotationDataSet();
    // first page
    // to search for active annotations, the first parameter can be provided as, e.g. "q=*:*" or
    // just "*:*"
    // AnnotationPage annPg =
    // annSearchApi.searchAnnotations("*:*&fq=modified:[2019-11-24T16:10:49.624Z TO
    // 2019-11-27T16:10:49.624Z]", SearchProfiles.STANDARD, null);
    AnnotationPage annPg = searchAnnotationsAddQueryField(VALUE_ALL, null, null, null, null,
        SearchProfiles.STANDARD.toString(), null);
    // to search for disabled(deleted) annotations, the parameter "disabled" must be provided
    // AnnotationPage annPg = annSearchApi.searchAnnotations("disabled=true&lastUpdate=25-11-2019",
    // SearchProfiles.STANDARD);
    assertNotNull(annPg, "AnnotationPage must not be null");
    // there might be old annotations of failing tests in the database
    assertEquals(annPg.getCurrentPage(), 0);

    List<? extends Annotation> annos = annPg.getAnnotations();

    assertTrue(0 < annos.size());
  }

  @Test
  public void testProfileInSearch() throws Exception {
    createAnnotationDataSet();
    //minimal profile
    AnnotationPage annPg =
        searchAnnotationsAddQueryField("*", null, null, null, null, SearchProfiles.MINIMAL.toString(), null);
    assertNotNull(annPg, "AnnotationPage must not be null");
    assertNull(annPg.getAnnotations());
    assertNotNull(annPg.getItems());
    //debug profile
    annPg = searchAnnotationsAddQueryField("*", null, null, null, null, SearchProfiles.DEBUG.toString(), null);
    assertNotNull(annPg, "AnnotationPage must not be null");
    //in case of standard profile (computed in case of debug), the whole annotations are returned
    assertNotNull(annPg.getAnnotations());
    assertNull(annPg.getItems());
  }

  @Test
  public void searchWithFacetsBadRequests() throws Exception {
    String url = AnnotationTestUtils.buildUrl("*:*", null, null, null, null, null, null, "minimal, facets, standard", null);
    ResultActions mockMvcResult = mockMvc.perform(get(url));
    assertEquals(HttpStatus.BAD_REQUEST, HttpStatus.valueOf(mockMvcResult.andReturn().getResponse().getStatus()));

    url = AnnotationTestUtils.buildUrl("*:*", null, null, null, null, null, null, "minimal, dereference", null);
    mockMvcResult = mockMvc.perform(get(url));
    assertEquals(HttpStatus.BAD_REQUEST, HttpStatus.valueOf(mockMvcResult.andReturn().getResponse().getStatus()));
  }
  
  @Test
  public void searchWithFacets() throws Exception {
    createAnnotationDataSet();

    String[] facets = {"motivation", "generator_uri"};
    AnnotationPage annPg = searchAnnotations("*:*", null, facets, null, null, null, null, "minimal", null);
    assertNotNull(annPg, "AnnotationPage must not be null");
    assertNull(annPg.getFacets());
    assertNotNull(annPg.getItems());

    annPg = searchAnnotations("*:*", null, facets, null, null, null, null, "minimal, facets", null);
    assertNotNull(annPg, "AnnotationPage must not be null");
    assertTrue(annPg.getFacets().get("motivation").size()>0);
    assertTrue(annPg.getFacets().get("generator_uri").size()>0);
    assertNotNull(annPg.getItems());
    
    annPg = searchAnnotations("*:*", null, facets, null, null, null, null, "minimal, facets, debug", null);
    assertNotNull(annPg, "AnnotationPage must not be null");
    assertTrue(annPg.getFacets().get("motivation").size()>0);
    assertTrue(annPg.getFacets().get("generator_uri").size()>0);
    assertNotNull(annPg.getItems());
  }

  // @Test
  public void searchGeoTag1() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_GEOTAG);

    Annotation inputAnno = parseTag(requestBody);

    // create indexed tag
    Annotation createdAnno = createTag(requestBody);
    addToCreatedAnnotations(createdAnno.getIdentifier());

    assertTrue(BodyInternalTypes.isGeoTagBody(createdAnno.getBody().getInternalType()));
    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(createdAnno, inputAnno);

    // search for indexed id and textual values
    Annotation retrievedAnno =
        searchLastCreated(VALUE_ID + "\"" + createdAnno.getIdentifier() + "\"");
    String VALUE_TARGET = "target_uri:\""
        + AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
        + "/09102/_UEDIN_214\"";
    retrievedAnno = searchLastCreated(VALUE_TARGET);
    // assertNo

    // validate fields
    assertTrue(retrievedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
    assertEquals(retrievedAnno.getBody().getInternalType(), BodyInternalTypes.GEO_TAG.name());
    EdmPlace placeBody = (EdmPlace) ((EdmPlaceBody) retrievedAnno.getBody()).getPlace();
    assertNotNull(placeBody.getLatitude());
    assertTrue(placeBody.getLatitude().equals("48.853415"));
    assertNotNull(placeBody.getLongitude());
    assertTrue(placeBody.getLongitude().equals("-102.348800"));
    Target target = retrievedAnno.getTarget();
    assertNotNull(target.getHttpUri());
    assertTrue(target.getHttpUri()
        .equals(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
            + "/09102/_UEDIN_214"));
  }

  // @Test
  public void searchTag1() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_BODY_TEXT);

    Annotation inputAnno = parseTag(requestBody);

    // create indexed tag
    Annotation createdAnno = createTag(requestBody);
    addToCreatedAnnotations(createdAnno.getIdentifier());

    assertTrue(BodyInternalTypes.isTagBody(createdAnno.getBody().getInternalType()));
    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(createdAnno, inputAnno);

    // search for indexed id and textual values
    Annotation retrievedAnno =
        searchLastCreated(VALUE_ID + "\"" + createdAnno.getIdentifier() + "\"");
    retrievedAnno = searchLastCreated(VALUE_BODY_VALUE);
    String VALUE_TARGET_TAG = "target_uri:\""
        + AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
        + "/000002/_UEDIN_214\"";
    retrievedAnno = searchLastCreated(VALUE_TARGET_TAG);

    // validate fields
    assertTrue(retrievedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
    assertEquals(retrievedAnno.getBody().getInternalType(), BodyInternalTypes.TAG.name());
    TagBody tagBody = ((TagBody) retrievedAnno.getBody());
    assertNotNull(tagBody.getValue());
    assertTrue(tagBody.getValue().equals("trombone"));
    Target target = retrievedAnno.getTarget();
    assertNotNull(target.getHttpUri());
    assertTrue(target.getHttpUri()
        .equals(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
            + "/000002/_UEDIN_214"));
  }

  // @Test
  public void searchTagText() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(DESCRIBING_WEB_RESOURCE);

    // create indexed tag
    Annotation createdAnno = createTag(requestBody);
    addToCreatedAnnotations(createdAnno.getIdentifier());

    assertTrue(BodyInternalTypes.isTextualBody(createdAnno.getBody().getInternalType()));

    // search for indexed id and textual values
    Annotation retrievedAnno =
        searchLastCreated(VALUE_ID + "\"" + createdAnno.getIdentifier() + "\"");
    retrievedAnno = searchLastCreated(VALUE_DESCRIBING_BODY_VALUE);
    String VALUE_DESCRIBING_TARGET_SCOPE = "target_uri:\""
        + AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
        + "/07931/diglit_uah_m1\"";
    retrievedAnno = searchLastCreated(VALUE_DESCRIBING_TARGET_SCOPE);

    // validate fields
    assertTrue(
        retrievedAnno.getMotivation().equals(MotivationTypes.DESCRIBING.name().toLowerCase()));
    assertEquals(retrievedAnno.getBody().getInternalType(), BodyInternalTypes.TEXT.name());
    TextBody textBody = ((TextBody) retrievedAnno.getBody());
    assertNotNull(textBody.getValue());
    assertTrue(textBody.getValue().equals("... this is the textual description of the item ..."));
    Target target = retrievedAnno.getTarget();
    assertNotNull(target.getSource());
    assertTrue(target.getSource()
        .equals("http://www.europeana1914-1918.eu/attachments/2020601/20841.235882.full.jpg"));
    assertNotNull(target.getScope());
    assertTrue(target.getScope()
        .equals(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
            + "/07931/diglit_uah_m1"));
  }

  // @Test
  public void searchLink1() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(LINK_STANDARD);

    // create indexed tag
    Annotation createdAnno = createLink(requestBody);
    addToCreatedAnnotations(createdAnno.getIdentifier());

    // search for indexed id and textual values
    Annotation retrievedAnno =
        searchLastCreated(VALUE_ID + "\"" + createdAnno.getIdentifier() + "\"");
    String VALUE_TARGET_LINK = "target_uri:\""
        + AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
        + "/2020601/https___1914_1918_europeana_eu_contributions_19584\"";
    retrievedAnno = searchLastCreated(VALUE_TARGET_LINK);

    // validate fields
    assertTrue(retrievedAnno.getMotivation().equals(MotivationTypes.LINKING.name().toLowerCase()));
    Target target = retrievedAnno.getTarget();
    assertNotNull(target.getValues());
    assertTrue(target.getValues()
        .contains(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
            + "/2020601/https___1914_1918_europeana_eu_contributions_19584"));
  }

  // @Test
  public void searchSemanticLink1() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(LINK_SEMANTIC);

    // create indexed tag
    Annotation createdAnno = createLink(requestBody);
    addToCreatedAnnotations(createdAnno.getIdentifier());

    // search for indexed id and textual values
    Annotation retrievedAnno =
        searchLastCreated(VALUE_ID + "\"" + createdAnno.getIdentifier() + "\"");
    String VALUE_TARGET_LINK_SEMANTIC = "target_uri:\""
        + AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
        + "/2048410/item_I5DUPVW2Q5HT2OQFSVXV7VYODA5P32P6\"";
    retrievedAnno = searchLastCreated(VALUE_TARGET_LINK_SEMANTIC);

    // validate fields
    assertTrue(retrievedAnno.getMotivation().equals(MotivationTypes.LINKING.name().toLowerCase()));
    Target target = retrievedAnno.getTarget();
    assertNotNull(target.getHttpUri());
    assertTrue(target.getHttpUri()
        .equals(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
            + "/2048410/item_I5DUPVW2Q5HT2OQFSVXV7VYODA5P32P6"));
  }

  // @Test
  public void searchGraph1() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(LINK_SEMANTIC);

    // create indexed tag
    Annotation createdAnno = createLink(requestBody);
    addToCreatedAnnotations(createdAnno.getIdentifier());

    // search for indexed id and textual values
    Annotation retrievedAnno =
        searchLastCreated(VALUE_ID + "\"" + createdAnno.getIdentifier() + "\"");
    retrievedAnno = searchLastCreated(VALUE_BODY_LINK_RESOURCE_URI);
    retrievedAnno = searchLastCreated(VALUE_BODY_LINK_RELATION);
    String VALUE_TARGET_LINK_SEMANTIC = "target_uri:\""
        + AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
        + "/2048410/item_I5DUPVW2Q5HT2OQFSVXV7VYODA5P32P6\"";
    retrievedAnno = searchLastCreated(VALUE_TARGET_LINK_SEMANTIC);

    // validate fields
    assertTrue(retrievedAnno.getMotivation().equals(MotivationTypes.LINKING.name().toLowerCase()));
    assertEquals(retrievedAnno.getBody().getInternalType(), BodyInternalTypes.GRAPH.name());
    Graph graphBody = ((GraphBody) retrievedAnno.getBody()).getGraph();
    assertNotNull(graphBody.getNode());
    assertTrue(graphBody.getNode().getHttpUri().equals("https://www.wikidata.org/wiki/Q762"));
    assertTrue(graphBody.getRelationName().equals("isSimilarTo"));
    assertTrue(graphBody.getResourceUri()
        .equals(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
            + "/2048410/item_I5DUPVW2Q5HT2OQFSVXV7VYODA5P32P6"));
    Target target = retrievedAnno.getTarget();
    assertNotNull(target.getHttpUri());
    assertTrue(target.getHttpUri()
        .equals(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
            + "/2048410/item_I5DUPVW2Q5HT2OQFSVXV7VYODA5P32P6"));
  }

  // @Test
  public void searchSemanticTagSpecific1() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(SEMANTICTAG_SPECIFIC_STANDARD);

    Annotation inputAnno = parseTag(requestBody);

    // create indexed tag
    Annotation createdAnno = createTag(requestBody);
    addToCreatedAnnotations(createdAnno.getIdentifier());

    assertTrue(BodyInternalTypes.isSemanticTagBody(createdAnno.getBody().getInternalType()));
    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(createdAnno, inputAnno);

    // search for indexed id and textual values
    Annotation retrievedAnno =
        searchLastCreated(VALUE_ID + "\"" + createdAnno.getIdentifier() + "\"");
    retrievedAnno = searchLastCreated(VALUE_BODY_SPECIFIC_RESOURCE);
    String VALUE_TARGET = "target_uri:\""
        + AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
        + "/09102/_UEDIN_214\"";
    retrievedAnno = searchLastCreated(VALUE_TARGET);

    // validate fields
    assertTrue(retrievedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
    assertEquals(retrievedAnno.getBody().getInternalType(), BodyInternalTypes.SEMANTIC_TAG.name());
    TagBody tagBody = ((SemanticTagBody) retrievedAnno.getBody());
    assertNotNull(tagBody.getHttpUri());
    assertTrue(tagBody.getHttpUri().equals("http://sws.geonames.org/2988506"));
    assertNotNull(tagBody.getLanguage());
    assertTrue(tagBody.getLanguage().equals("en"));
    Target target = retrievedAnno.getTarget();
    assertNotNull(target.getHttpUri());
    assertTrue(target.getHttpUri()
        .equals(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
            + "/09102/_UEDIN_214"));
  }

  /**
   * Search annotations by textual body value for different body types
   * 
   * @param query
   * @param foundAnnotationsNumber
   * @throws Exception
   */
  protected Annotation searchLastCreated(String query) throws Exception {
    AnnotationPage annPg = search(query, SearchProfiles.STANDARD.toString(), "1");
    assertNotNull(annPg);
    assert (annPg.getTotalInPage() > 0);
    assert (annPg.getTotalInCollection() > 0);
    assertNotNull(annPg.getAnnotations());
    Annotation anno = annPg.getAnnotations().get(0);
    assertNotNull(anno);
    // assertTrue(foundAnnotationsNumber <= annPg.getTotalInCollection());
    return anno;
  }

  protected AnnotationPage search(String bodyValue, String profile, String limit)
      throws Exception {
    AnnotationPage annPg = searchAnnotations(bodyValue, null, null, WebAnnotationFields.CREATED, "desc",
        "0", limit, profile, null);

    assertNotNull(annPg, "AnnotationPage must not be null");
    return annPg;
  }

  // @Test
  public void searchAnnotationsWithMultipleTargetsByEachTarget() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(SIMPLE_LINK_ANNOTATION);

    long annotationNr = System.currentTimeMillis();
    Annotation annotation =
        createAnnotationLd(MotivationTypes.LINKING.name(), annotationNr, requestBody, null);
    addToCreatedAnnotations(annotation.getIdentifier());
    String resJson = "";
    // List<Long> idList = new ArrayList<Long>();
    Iterator<String> itr = annotation.getTarget().getValues().iterator();
    while (itr.hasNext()) {
      String target = itr.next();
      String annotationStrRes = searchAnnotationLd(target, null);
      log.debug("historypin search annotation by target test: " + annotationStrRes);
      assertNotNull(annotationStrRes);
      resJson = resJson + annotationStrRes;
      // AnnotationSearchResults asr = europeanaLdApi.getAnnotationSearchResults(annotationStrRes);
      // List<Annotation> annotationList = asr.getItems();
      // Iterator<Annotation> itrAnnotationList = annotationList.iterator();
      // while (itrAnnotationList.hasNext()) {
      // Annotation anno = itrAnnotationList.next();
      // idList.add(anno.getAnnotationId().getAnnotationNr());
      // }
    }
    assertTrue(resJson.contains("\"" + WebAnnotationFields.IDENTIFIER + "\":" + annotationNr));
    // assertTrue(idList.contains(annotationNr));
  }

  // @Test
  public void searchAnnotationsWithMultipleTargetsByEachResearchId() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(SIMPLE_LINK_ANNOTATION);

    long annotationNr = System.currentTimeMillis();
    Annotation annotation =
        createAnnotationLd(MotivationTypes.LINKING.name(), annotationNr, requestBody, null);
    addToCreatedAnnotations(annotation.getIdentifier());
    Iterator<String> itr = annotation.getTarget().getResourceIds().iterator();
    while (itr.hasNext()) {
      String resourceId = itr.next();
      String annotationStrRes = searchAnnotationLd(null, resourceId);
      log.debug("historypin search annotation by target test: " + annotationStrRes);
      assertNotNull(annotationStrRes);
      assertTrue(annotationStrRes.contains(resourceId));
    }
  }

  @Test
  public void searchGraph2() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(LINK_SEMANTIC);

    // create indexed tag
    Annotation createdAnno = createLink(requestBody);
    addToCreatedAnnotations(createdAnno.getIdentifier());

    // search for indexed id and textual values
    Annotation retrievedAnno =
        searchLastCreated(VALUE_ID + "\"" + createdAnno.getIdentifier() + "\"");
    validateGraph(retrievedAnno);
    retrievedAnno = searchLastCreated(VALUE_SEARCH_BODY_LINK_RESOURCE_URI);
    validateGraph(retrievedAnno);
    retrievedAnno = searchLastCreated(VALUE_SEARCH_BODY_LINK_RELATION);
    validateGraph(retrievedAnno);
    String VALUE_TARGET_LINK_SEMANTIC_URI =
        AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
            + "/2048410/item_I5DUPVW2Q5HT2OQFSVXV7VYODA5P32P6";
    String VALUE_SEARCH_TARGET_LINK_SEMANTIC =
        "target_uri:\"" + VALUE_TARGET_LINK_SEMANTIC_URI + "\"";
    retrievedAnno = searchLastCreated(VALUE_SEARCH_TARGET_LINK_SEMANTIC);
    validateGraph(retrievedAnno);
  }

  /**
   * Validate graph fields after search.
   * 
   * @param storedAnno
   * @throws IOException
   */
  private void validateGraph(Annotation storedAnno) throws IOException {
    assertEquals(MotivationTypes.LINKING.name().toLowerCase(), storedAnno.getMotivation());
    assertEquals(BodyInternalTypes.GRAPH.name(), storedAnno.getBody().getInternalType());
    Graph graphBody = ((GraphBody) storedAnno.getBody()).getGraph();
    assertNotNull(graphBody.getNode());
    assertEquals(VALUE_BODY_LINK_RESOURCE_URI, graphBody.getNode().getHttpUri());
    assertEquals(VALUE_BODY_LINK_RELATION, graphBody.getRelationName());
    String VALUE_TARGET_LINK_SEMANTIC_URI =
        AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
            + "/2048410/item_I5DUPVW2Q5HT2OQFSVXV7VYODA5P32P6";
    assertEquals(VALUE_TARGET_LINK_SEMANTIC_URI, graphBody.getResourceUri());
    Target target = storedAnno.getTarget();
    assertNotNull(target.getHttpUri());
    assertEquals(VALUE_TARGET_LINK_SEMANTIC_URI, target.getHttpUri());
  }

  @Test
  public void searchLink2() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(LINK_STANDARD);

    // create indexed tag
    Annotation createdAnno = createLink(requestBody);
    addToCreatedAnnotations(createdAnno.getIdentifier());

    // search for indexed id and textual values
    Annotation retrievedAnno =
        searchLastCreated(VALUE_ID + "\"" + createdAnno.getIdentifier() + "\"");
    validateLink(retrievedAnno);
    String VALUE_TARGET_LINK_URI =
        AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
            + "/2020601/https___1914_1918_europeana_eu_contributions_19584";
    String VALUE_SEARCH_TARGET_LINK = "target_uri:\"" + VALUE_TARGET_LINK_URI + "\"";
    retrievedAnno = searchLastCreated(VALUE_SEARCH_TARGET_LINK);
    validateLink(retrievedAnno);
  }

  /**
   * Validate link fields after search.
   * 
   * @param storedAnno
   * @throws IOException
   */
  private void validateLink(Annotation storedAnno) throws IOException {
    assertTrue(storedAnno.getMotivation().equals(MotivationTypes.LINKING.name().toLowerCase()));
    Target target = storedAnno.getTarget();
    assertNotNull(target.getValues());
    String VALUE_TARGET_LINK_URI =
        AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
            + "/2020601/https___1914_1918_europeana_eu_contributions_19584";
    assertTrue(target.getValues().contains(VALUE_TARGET_LINK_URI));
  }

  @Test
  public void searchSemanticLink2() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(LINK_SEMANTIC);

    // create indexed tag
    Annotation createdAnno = createLink(requestBody);
    addToCreatedAnnotations(createdAnno.getIdentifier());

    // search for indexed id and textual values
    Annotation retrievedAnno =
        searchLastCreated(VALUE_ID + "\"" + createdAnno.getIdentifier() + "\"");
    validateSemanticLink(retrievedAnno);
    String VALUE_TARGET_LINK_SEMANTIC_URI =
        AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
            + "/2048410/item_I5DUPVW2Q5HT2OQFSVXV7VYODA5P32P6";
    String VALUE_SEARCH_TARGET_LINK_SEMANTIC =
        "target_uri:\"" + VALUE_TARGET_LINK_SEMANTIC_URI + "\"";
    retrievedAnno = searchLastCreated(VALUE_SEARCH_TARGET_LINK_SEMANTIC);
    validateSemanticLink(retrievedAnno);
  }

  /**
   * Validate semantic link fields after search.
   * 
   * @param storedAnno
   * @throws IOException
   */
  private void validateSemanticLink(Annotation storedAnno) throws IOException {
    assertTrue(storedAnno.getMotivation().equals(MotivationTypes.LINKING.name().toLowerCase()));
    Target target = storedAnno.getTarget();
    assertNotNull(target.getHttpUri());
    String VALUE_TARGET_LINK_SEMANTIC_URI =
        AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
            + "/2048410/item_I5DUPVW2Q5HT2OQFSVXV7VYODA5P32P6";
    assertTrue(target.getHttpUri().equals(VALUE_TARGET_LINK_SEMANTIC_URI));
  }

  @Test
  public void searchSubtitles() throws Exception {


    String requestBody = AnnotationTestUtils.getJsonStringInput(SUBTITLE_MINIMAL);
    MotivationTypes motivationType = MotivationTypes.SUBTITLING;
    Annotation inputAnno = AnnotationTestUtils.parseAnnotation(requestBody, motivationType);

    // create indexed subtitle
    Annotation storedAnno = createTestAnnotation(SUBTITLE_MINIMAL, true, null);
    addToCreatedAnnotations(storedAnno.getIdentifier());

    assertTrue(BodyInternalTypes.isFullTextResourceTagBody(storedAnno.getBody().getInternalType()));
    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);

    // search for indexed id and textual values
    // TODO: restore after updating schema
    AnnotationPage annoPage = search(VALUE_SEARCH_BODY_VALUE_IT, SearchProfiles.STANDARD.toString(), "1");
    Annotation retrievedAnnotation = (Annotation) annoPage.getAnnotations().get(0);
    assertEquals(storedAnno.getIdentifier(), retrievedAnnotation.getIdentifier());
    validateSubtitle(retrievedAnnotation);
  }

  /**
   * Validate full text resource tag fields after search.
   * 
   * @param storedAnno
   * @throws IOException
   */
  private void validateSubtitle(Annotation storedAnno) throws IOException {
    assertTrue(storedAnno.getMotivation().equals(MotivationTypes.SUBTITLING.name().toLowerCase()));
    assertEquals(storedAnno.getBody().getInternalType(),
        BodyInternalTypes.FULL_TEXT_RESOURCE.name());
    FullTextResourceBody textBody = ((FullTextResourceBody) storedAnno.getBody());
    assertNotNull(textBody.getValue());
    assertTrue(textBody.getValue().contains("con il grande finale"));
    Target target = storedAnno.getTarget();
    assertNotNull(target.getSource());
    assertTrue(target.getSource()
        .equals("http://www.euscreen.eu/item.html?id=EUS_D61E8DF003E30114621A92ABDE846AD7"));
    assertNotNull(target.getScope());
    assertTrue(target.getScope()
        .equals(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
            + "/2051933/data_euscreenXL_EUS_D61E8DF003E30114621A92ABDE846AD7"));
  }

  @Test
  public void searchSemanticTag2() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(SEMANTICTAG_SIMPLE_STANDARD);

    Annotation inputAnno = parseTag(requestBody);

    // create indexed tag
    Annotation createdAnno = createTag(requestBody);
    addToCreatedAnnotations(createdAnno.getIdentifier());

    assertTrue(BodyInternalTypes.isSemanticTagBody(createdAnno.getBody().getInternalType()));
    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(createdAnno, inputAnno);

    // search for indexed id and textual values
    Annotation retrievedAnno =
        searchLastCreated(VALUE_ID + "\"" + createdAnno.getIdentifier() + "\"");
    validateSemanticTag(retrievedAnno);
    retrievedAnno = searchLastCreated(VALUE_SEARCH_TAG_BODY_URI);
    validateSemanticTag(retrievedAnno);
    String VALUE_TARGET_URI =
        AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
            + "/09102/_UEDIN_214";
    String VALUE_SEARCH_TARGET = "target_uri:\"" + VALUE_TARGET_URI + "\"";
    retrievedAnno = searchLastCreated(VALUE_SEARCH_TARGET);
    validateSemanticTag(retrievedAnno);
  }

  /**
   * Validate semantic tag fields after search.
   * 
   * @param storedAnno
   * @throws IOException
   */
  private void validateSemanticTag(Annotation storedAnno) throws IOException {
    assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
    assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.SEMANTIC_TAG.name());
    TagBody tagBody = ((SemanticTagBody) storedAnno.getBody());
    assertNotNull(tagBody.getHttpUri());
    assertTrue(tagBody.getHttpUri().equals("http://www.geonames.org/2988507"));
    assertNotNull(tagBody.getLanguage());
    assertTrue(tagBody.getLanguage().equals("en"));
    Target target = storedAnno.getTarget();
    assertNotNull(target.getHttpUri());
    assertTrue(target.getHttpUri()
        .equals(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
            + "/09102/_UEDIN_214"));
  }

  @Test
  public void searchGeoTag2() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_GEOTAG);

    Annotation inputAnno = parseTag(requestBody);

    // create indexed tag
    Annotation createdAnno = createTag(requestBody);
    addToCreatedAnnotations(createdAnno.getIdentifier());

    assertTrue(BodyInternalTypes.isGeoTagBody(createdAnno.getBody().getInternalType()));
    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(createdAnno, inputAnno);

    // search for indexed id and textual values
    Annotation retrievedAnno =
        searchLastCreated(VALUE_ID + "\"" + createdAnno.getIdentifier() + "\"");
    validateGeoTag(retrievedAnno);
    String VALUE_TARGET_URI =
        AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
            + "/09102/_UEDIN_214";
    String VALUE_SEARCH_TARGET = "target_uri:\"" + VALUE_TARGET_URI + "\"";
    retrievedAnno = searchLastCreated(VALUE_SEARCH_TARGET);
    validateGeoTag(retrievedAnno);
  }

  /**
   * Validate geo tag fields after search.
   * 
   * @param storedAnno
   * @throws IOException
   */
  private void validateGeoTag(Annotation storedAnno) throws IOException {
    assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
    assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.GEO_TAG.name());
    EdmPlace placeBody = (EdmPlace) ((EdmPlaceBody) storedAnno.getBody()).getPlace();
    assertNotNull(placeBody.getLatitude());
    assertTrue(placeBody.getLatitude().equals("48.853415"));
    assertNotNull(placeBody.getLongitude());
    assertTrue(placeBody.getLongitude().equals("-102.348800"));
    Target target = storedAnno.getTarget();
    assertNotNull(target.getHttpUri());
    assertTrue(target.getHttpUri()
        .equals(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
            + "/09102/_UEDIN_214"));
  }

  @Test
  public void searchTag2() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_BODY_TEXT);

    Annotation inputAnno = parseTag(requestBody);

    // create indexed tag
    Annotation createdAnno = createTag(requestBody);
    addToCreatedAnnotations(createdAnno.getIdentifier());

    assertTrue(BodyInternalTypes.isTagBody(createdAnno.getBody().getInternalType()));
    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(createdAnno, inputAnno);

    // search for indexed id and textual values
    Annotation retrievedAnno =
        searchLastCreated(VALUE_ID + "\"" + createdAnno.getIdentifier() + "\"");
    validateTag(retrievedAnno);
    retrievedAnno = searchLastCreated(VALUE_SEARCH_TAG_BODY_VALUE);
    validateTag(retrievedAnno);
    String VALUE_TARGET_TAG_URI =
        AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
            + "/000002/_UEDIN_214";
    String VALUE_SEARCH_TARGET_TAG = "target_uri:\"" + VALUE_TARGET_TAG_URI + "\"";
    retrievedAnno = searchLastCreated(VALUE_SEARCH_TARGET_TAG);
    validateTag(retrievedAnno);
  }

  /**
   * Validate tag fields after search.
   * 
   * @param storedAnno
   * @throws IOException
   */
  private void validateTag(Annotation storedAnno) throws IOException {
    assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
    assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.TAG.name());
    TagBody tagBody = ((TagBody) storedAnno.getBody());
    assertNotNull(tagBody.getValue());
    assertTrue(tagBody.getValue().equals("trombone"));
    Target target = storedAnno.getTarget();
    assertNotNull(target.getHttpUri());
    assertTrue(target.getHttpUri()
        .equals(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
            + "/000002/_UEDIN_214"));
  }


  @Test
  public void searchSemanticTagSpecific2() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(SEMANTICTAG_SPECIFIC_STANDARD);

    Annotation inputAnno = parseTag(requestBody);

    // create indexed tag
    Annotation createdAnno = createTag(requestBody);
    addToCreatedAnnotations(createdAnno.getIdentifier());

    assertTrue(BodyInternalTypes.isSemanticTagBody(createdAnno.getBody().getInternalType()));
    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(createdAnno, inputAnno);

    // search for indexed id and textual values
    Annotation retrievedAnno =
        searchLastCreated(VALUE_ID + "\"" + createdAnno.getIdentifier() + "\"");
    validateSemanticTagSpecific(retrievedAnno);
    retrievedAnno = searchLastCreated(VALUE_SEARCH_BODY_SPECIFIC_RESOURCE);
    validateSemanticTagSpecific(retrievedAnno);
    String VALUE_TARGET_URI =
        AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
            + "/09102/_UEDIN_214";
    String VALUE_SEARCH_TARGET = "target_uri:\"" + VALUE_TARGET_URI + "\"";
    retrievedAnno = searchLastCreated(VALUE_SEARCH_TARGET);
    validateSemanticTagSpecific(retrievedAnno);
  }

  /**
   * Validate semantic tag specific fields after search.
   * 
   * @param storedAnno
   * @throws IOException
   */
  private void validateSemanticTagSpecific(Annotation storedAnno) throws IOException {
    assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
    assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.SEMANTIC_TAG.name());
    TagBody tagBody = ((SemanticTagBody) storedAnno.getBody());
    assertNotNull(tagBody.getHttpUri());
    assertTrue(tagBody.getHttpUri().equals("http://sws.geonames.org/2988506"));
    assertNotNull(tagBody.getLanguage());
    assertTrue(tagBody.getLanguage().equals("en"));
    Target target = storedAnno.getTarget();
    assertNotNull(target.getHttpUri());
    assertTrue(target.getHttpUri()
        .equals(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
            + "/09102/_UEDIN_214"));
  }

  @Test
  public void searchDescriptionBody() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(DESCRIBING_WEB_RESOURCE);

    // create indexed tag
    // TODO: change to create annotation
    Annotation createdAnno = createTag(requestBody);
    addToCreatedAnnotations(createdAnno.getIdentifier());

    // "scope": "http://data.europeana.eu/item/07931/diglit_uah_m1",
    // "source":
    // "http://www.europeana1914-1918.eu/attachments/2020601/20841.235882.full.jpg"

    assertTrue(BodyInternalTypes.isTextualBody(createdAnno.getBody().getInternalType()));

    // search for indexed id and textual values
    Annotation retrievedAnno =
        searchLastCreated(VALUE_ID + "\"" + createdAnno.getIdentifier() + "\"");
    validateDescriptionBody(retrievedAnno);
    retrievedAnno = searchLastCreated(VALUE_SEARCH_DESCRIBING_BODY_VALUE);
    validateDescriptionBody(retrievedAnno);
    String VALUE_DESCRIBING_TARGET_SCOPE_URI =
        AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
            + "/07931/diglit_uah_m1";
    String VALUE_SEARCH_DESCRIBING_TARGET_SCOPE =
        "target_uri:\"" + VALUE_DESCRIBING_TARGET_SCOPE_URI + "\"";
    retrievedAnno = searchLastCreated(VALUE_SEARCH_DESCRIBING_TARGET_SCOPE);
    validateDescriptionBody(retrievedAnno);
    retrievedAnno = searchLastCreated(VALUE_SEARCH_DESCRIBING_TARGET_SOURCE);
    validateDescriptionBody(retrievedAnno);
  }

  /**
   * Validate tag text fields after search.
   * 
   * @param storedAnno
   * @throws IOException
   */
  private void validateDescriptionBody(Annotation storedAnno) throws IOException {
    assertTrue(storedAnno.getMotivation().equals(MotivationTypes.DESCRIBING.name().toLowerCase()));
    assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.TEXT.name());
    TextBody textBody = ((TextBody) storedAnno.getBody());
    assertNotNull(textBody.getValue());
    assertTrue(textBody.getValue().equals("... this is the textual description of the item ..."));
    Target target = storedAnno.getTarget();
    assertNotNull(target.getSource());
    assertTrue(target.getSource()
        .equals("http://www.europeana1914-1918.eu/attachments/2020601/20841.235882.full.jpg"));
    assertNotNull(target.getScope());
    assertTrue(target.getScope()
        .equals(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
            + "/07931/diglit_uah_m1"));
  }

  @Test
  public void searchTranscriptionBody() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(TRANSCRIPTION_WITH_RIGHTS);

    Annotation inputAnno = parseTag(requestBody);

    // create indexed tag
    // TODO: change to create annotation
    Annotation createdAnno = createTag(requestBody);
    addToCreatedAnnotations(createdAnno.getIdentifier());

    assertTrue(
        BodyInternalTypes.isFullTextResourceTagBody(createdAnno.getBody().getInternalType()));
    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(createdAnno, inputAnno);

    // search for indexed id and textual values
    Annotation retrievedAnno =
        searchLastCreated(VALUE_ID + "\"" + createdAnno.getIdentifier() + "\"");
    validateTranscriptionBody(retrievedAnno);
    retrievedAnno = searchLastCreated(VALUE_SEARCH_BODY_FULL_TEXT_RESOURCE);
    validateTranscriptionBody(retrievedAnno);
    String VALUE_SEARCH_TARGET_URI = "target_uri:\""
        + AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
        + "/07931/diglit_uah_m1\"";
    retrievedAnno = searchLastCreated(VALUE_SEARCH_TARGET_URI);
    validateTranscriptionBody(retrievedAnno);
  }

  /**
   * Validate full text resource tag fields after search.
   * 
   * @param storedAnno
   * @throws IOException
   */
  private void validateTranscriptionBody(Annotation storedAnno) throws IOException {
    assertTrue(
        storedAnno.getMotivation().equals(MotivationTypes.TRANSCRIBING.name().toLowerCase()));
    assertEquals(storedAnno.getBody().getInternalType(),
        BodyInternalTypes.FULL_TEXT_RESOURCE.name());
    FullTextResourceBody textBody = ((FullTextResourceBody) storedAnno.getBody());
    assertNotNull(textBody.getValue());
    assertTrue(textBody.getValue().equals("... complete transcribed text in HTML ..."));
    Target target = storedAnno.getTarget();
    assertNotNull(target.getSource());
    assertTrue(target.getSource()
        .equals("http://www.europeana1914-1918.eu/attachments/2020601/20841.235882.full.jpg"));
    assertNotNull(target.getScope());
    assertTrue(target.getScope()
        .equals(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()
            + "/07931/diglit_uah_m1"));
  }

  /**
   * Test search query and verify dereferenced search result
   * 
   * @throws Exception
   */
  @Test
  public void testSearchDereferencedAnnotation() throws Exception {
    Annotation storedAnno = createTag(DEREFERENCED_SEMANTICTAG_TEST_ENTITY, false, true);
    addToCreatedAnnotations(storedAnno.getIdentifier());
    Annotation storedAnno2 = createTag(DEREFERENCED_SEMANTICTAG_TEST_ENTITY_2, false, true);
    addToCreatedAnnotations(storedAnno2.getIdentifier());
    Annotation storedAnno3 = createTag(DEREFERENCED_SEMANTICTAG_TEST_ENTITY_3, false, true);
    addToCreatedAnnotations(storedAnno3.getIdentifier());

    // first page
    AnnotationPage annPg = searchAnnotationsAddQueryField(SEARCH_VALUE_TEST, null, null, null, null,
        SearchProfiles.DEREFERENCE.toString(), TEST_LANGUAGE);
    assertNotNull(annPg, "AnnotationPage must not be null");
    // there must be annotations in database after initial insert in this test class
    assertTrue(0 <= annPg.getTotalInCollection());
    assertEquals(annPg.getCurrentPage(), 0);
    for (Annotation foundAnnotation : annPg.getAnnotations()) {
      log.info(foundAnnotation.getIdentifier());
      log.info(foundAnnotation.getBody().getHttpUri());
      assertEquals(foundAnnotation.getBody().getHttpUri(), QUERY_ID);
    }
  }

  /**
   * Test search query with multiple languages and verify dereferenced search result
   * 
   * @throws Exception
   */
  @Test
  public void testSearchDereferencedAnnotationMultiLanguage() throws Exception {
    Annotation storedAnno = createTag(DEREFERENCED_SEMANTICTAG_TEST_ENTITY, false, true);
    addToCreatedAnnotations(storedAnno.getIdentifier());
    Annotation storedAnno2 = createTag(DEREFERENCED_SEMANTICTAG_TEST_ENTITY_2, false, true);
    addToCreatedAnnotations(storedAnno2.getIdentifier());
    Annotation storedAnno3 = createTag(DEREFERENCED_SEMANTICTAG_TEST_ENTITY_3, false, true);
    addToCreatedAnnotations(storedAnno3.getIdentifier());

    // first page
    AnnotationPage annPg = searchAnnotationsAddQueryField(SEARCH_VALUE_TEST, null, null, null, null,
        SearchProfiles.DEREFERENCE.toString(), TEST_LANGUAGE_MULTI);
    assertNotNull(annPg, "AnnotationPage must not be null");
    // there must be annotations in database after initial insert in this test class
    assertTrue(0 <= annPg.getTotalInCollection());
    assertEquals(annPg.getCurrentPage(), 0);
    for (Annotation foundAnnotation : annPg.getAnnotations()) {
      log.info(foundAnnotation.getIdentifier());
      log.info(foundAnnotation.getBody().getHttpUri());
      assertEquals(foundAnnotation.getBody().getHttpUri(), QUERY_ID);
    }
  }

}

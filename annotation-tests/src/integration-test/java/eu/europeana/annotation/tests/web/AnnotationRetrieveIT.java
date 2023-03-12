package eu.europeana.annotation.tests.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.agent.impl.EdmAgent;
import eu.europeana.annotation.definitions.model.body.impl.EdmAgentBody;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.tests.AbstractIntegrationTest;
import eu.europeana.annotation.tests.config.AnnotationTestsConfiguration;
import eu.europeana.annotation.tests.utils.AnnotationTestUtils;
import eu.europeana.api.commons.definitions.utils.DateUtils;

@SpringBootTest
@AutoConfigureMockMvc
public class AnnotationRetrieveIT extends AbstractIntegrationTest {

  public long WRONG_GENERATED_IDENTIFIER = -1;
  public String UNKNOWN_WSKEY = "invalid_wskey";
  private static final String URI_WKD_VERMEER = "http://www.wikidata.org/entity/Q41264";
  private static final String URI_VIAF_VERMEER = "http://viaf.org/viaf/51961439";
  private static final String URI_WKT_PARK = "http://www.wikidata.org/entity/Q22698";
  private static final String URI_GETTY_COLD = "http://vocab.getty.edu/aat/300068991";
  private static final String URI_WKD_DA_VINCI = "http://www.wikidata.org/entity/Q762";

  @Test
  public void getDeletedAnnotations() throws Exception {
    // store 2 annotations
    Annotation anno_tag = createTestAnnotation(TAG_STANDARD, true, null);
    addToCreatedAnnotations(anno_tag.getIdentifier());
    Annotation anno_subtitle = createTestAnnotation(SUBTITLE_MINIMAL, true, null);
    addToCreatedAnnotations(anno_subtitle.getIdentifier());

    // delete both annotations
    deleteAnnotation(anno_tag.getIdentifier());
    deleteAnnotation(anno_subtitle.getIdentifier());

    Date now = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(now);
    calendar.add(Calendar.DATE, -1);
    Date startDate = calendar.getTime();
    calendar.add(Calendar.DATE, 2);
    Date stopDate = calendar.getTime();

    List<String> result = getDeleted(null, DateUtils.convertDateToStr(startDate),
        DateUtils.convertDateToStr(stopDate), 0, 100);
    assertTrue(result.contains(String.valueOf(anno_tag.getIdentifier())));
    assertTrue(result.contains(String.valueOf(anno_subtitle.getIdentifier())));
  }

  @Test
  public void getWebAnnotationWithWrongIdentifier() throws Exception {
    ResponseEntity<String> response =
        getAnnotation(AnnotationTestsConfiguration.getInstance().getApiKey(),
            WRONG_GENERATED_IDENTIFIER, false, null);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  @Disabled("This test is successfull only when the authorization is enabled")
  public void getWebAnnotationWithWrongWskey() throws Exception {
    ResponseEntity<String> response =
        getAnnotation(UNKNOWN_WSKEY, WRONG_GENERATED_IDENTIFIER, false, null);
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  public void getAnnotation() throws Exception {

    ResponseEntity<String> createResponse = storeTestAnnotation(TAG_STANDARD, true, null);
    Annotation annotation = AnnotationTestUtils.parseAndVerifyTestAnnotation(createResponse);
    /**
     * get annotation by provider and identifier
     */
    ResponseEntity<String> response =
        getAnnotation(AnnotationTestsConfiguration.getInstance().getApiKey(),
            annotation.getIdentifier(), false, null);

    // validateResponse(response, HttpStatus.OK);
    Annotation storedAnno =
        AnnotationTestUtils.parseAndVerifyTestAnnotation(response, HttpStatus.OK);
    AnnotationTestUtils.validateOutputAgainstInput(storedAnno, annotation);

    addToCreatedAnnotations(storedAnno.getIdentifier());
  }

  /**
   * This is an example dereferenciation test for PoC.
   * 
   * @throws Exception
   */
  @Test
  public void dereferencedSemanticTag() throws Exception {
    Annotation storedAnno1 = createTag(DEREFERENCED_SEMANTICTAG_TEST_ENTITY, false, true);
    addToCreatedAnnotations(storedAnno1.getIdentifier());
    Annotation storedAnno2 = createTag(DEREFERENCED_SEMANTICTAG_TEST_ENTITY_2, false, true);
    addToCreatedAnnotations(storedAnno2.getIdentifier());
    Annotation storedAnno3 = createTag(DEREFERENCED_SEMANTICTAG_TEST_ENTITY_3, false, true);
    addToCreatedAnnotations(storedAnno3.getIdentifier());

    log.info(storedAnno1.getBody().getInternalType());
    assertTrue(storedAnno1.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));

    // retrieve dereferenced annotation
    ResponseEntity<String> response =
        getAnnotation(AnnotationTestsConfiguration.getInstance().getApiKey(),
            storedAnno1.getIdentifier(), false, SearchProfiles.DEREFERENCE);

    assertNotNull(response.getBody());

    Annotation retrievedAnnotation = AnnotationTestUtils.parseResponseBody(response);
    assertNotNull(retrievedAnnotation.getBody().getHttpUri());
    assertEquals(retrievedAnnotation.getBody().getHttpUri(), storedAnno1.getBody().getValue());
    // date of birth and date of death are not currently mapped in the xslt template
    // assertNotNull(((EdmAgent) ((EdmAgentBody)
    // retrievedAnnotation.getBody()).getAgent()).getDateOfBirth());
    // assertNotNull(((EdmAgent) ((EdmAgentBody)
    // retrievedAnnotation.getBody()).getAgent()).getDateOfDeath());
    assertNotNull(
        ((EdmAgent) ((EdmAgentBody) retrievedAnnotation.getBody()).getAgent()).getPrefLabel());
    log.info("Input body:" + storedAnno1.getBody());
    log.info("Output body dereferenced:" + retrievedAnnotation.getBody());
    log.info("Identifier of the dereferenced annotation:" + retrievedAnnotation.getIdentifier());
  }
  
  /*
   * This test uses only the metis dereference client, but it is in the integration tests since the metis mock is needed,
   * which is configured in AbstractIntegrationTest.
   */
  @Test
  void testDereferenceMany() throws IOException {
    List<String> uris = Arrays.asList(new String[] { URI_WKD_VERMEER, URI_WKT_PARK, URI_GETTY_COLD, URI_VIAF_VERMEER, URI_WKD_DA_VINCI });
  
    Map<String, String> dereferenced = dereferenciationClient.dereferenceMany(uris, "en,de");
  
    assertNotNull(dereferenced);
    assertEquals(uris.size(), dereferenced.size());
    assertTrue(dereferenced.containsKey(URI_WKD_VERMEER));
    assertTrue(dereferenced.containsKey(URI_WKT_PARK));
    assertTrue(dereferenced.containsKey(URI_GETTY_COLD));
    assertTrue(dereferenced.containsKey(URI_VIAF_VERMEER));
    assertTrue(dereferenced.containsKey(URI_WKD_DA_VINCI));
    
    assertTrue(StringUtils.isNotBlank(dereferenced.get(URI_WKD_VERMEER)));
    assertTrue(StringUtils.isNotBlank(dereferenced.get(URI_WKT_PARK)));
    assertTrue(StringUtils.isNotBlank(dereferenced.get(URI_GETTY_COLD)));
    assertTrue(StringUtils.isNotBlank(dereferenced.get(URI_VIAF_VERMEER)));
    assertTrue(StringUtils.isNotBlank(dereferenced.get(URI_WKD_DA_VINCI)));
  }

  /**
   * This is a retrieval test for dereferenciation entity with only JWT token - without wskey.
   * 
   * @throws Exception
   */
  @Test
  @Disabled("This test needs to be updated, the search is still performed using the APIKEY and not the JWT Token")
  public void retrieveByJwtTokenDereferencedSemanticTagEntity() throws Exception {
    Annotation storedAnno = createTag(DEREFERENCED_SEMANTICTAG_TEST_ENTITY, false, true);
    addToCreatedAnnotations(storedAnno.getIdentifier());
    Annotation storedAnno2 = createTag(DEREFERENCED_SEMANTICTAG_TEST_ENTITY_2, false, true);
    addToCreatedAnnotations(storedAnno2.getIdentifier());
    Annotation storedAnno3 = createTag(DEREFERENCED_SEMANTICTAG_TEST_ENTITY_3, false, true);
    addToCreatedAnnotations(storedAnno3.getIdentifier());

    log.info(storedAnno.getBody().getInternalType());
    assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));

    // retrieve dereferenced annotation
    ResponseEntity<String> response =
        getAnnotation(AnnotationTestsConfiguration.getInstance().getApiKey(),
            storedAnno.getIdentifier(), false, SearchProfiles.DEREFERENCE);

    assertNotNull(response.getBody());

    Annotation retrievedAnnotation = AnnotationTestUtils.parseResponseBody(response);
    assertNotNull(retrievedAnnotation.getBody().getHttpUri());
    assertEquals(retrievedAnnotation.getBody().getHttpUri(), storedAnno.getBody().getValue());
    // date of birth and date of death are not currently mapped in the xslt template
    // assertNotNull(((EdmAgent) ((EdmAgentBody)
    // retrievedAnnotation.getBody()).getAgent()).getDateOfBirth());
    // assertNotNull(((EdmAgent) ((EdmAgentBody)
    // retrievedAnnotation.getBody()).getAgent()).getDateOfDeath());
    assertNotNull(
        ((EdmAgent) ((EdmAgentBody) retrievedAnnotation.getBody()).getAgent()).getPrefLabel());
    log.info("Input body:" + storedAnno.getBody());
    log.info("Output body dereferenced:" + retrievedAnnotation.getBody());
    log.info("Identifier of the dereferenced annotation:" + retrievedAnnotation.getIdentifier());
  }

}

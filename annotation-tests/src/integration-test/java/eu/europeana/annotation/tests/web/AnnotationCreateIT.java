package eu.europeana.annotation.tests.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.agent.impl.EdmAgent;
import eu.europeana.annotation.definitions.model.body.impl.EdmAgentBody;
import eu.europeana.annotation.definitions.model.body.impl.VcardAddressBody;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.tests.AbstractIntegrationTest;
import eu.europeana.annotation.tests.config.AnnotationTestsConfiguration;
import eu.europeana.annotation.tests.utils.AnnotationTestUtils;

/**
 * Annotation API Batch Upload Test class
 * 
 * @author Sven Schlarb
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AnnotationCreateIT extends AbstractIntegrationTest {

    private String get_CORRUPTED_JSON() throws IOException {
      return START + get_LINK_CORE(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()) + "\"motivation\",=\"oa:linking\"" + END;
    }
    private String get_LINK_JSON_WITH_WRONG_MOTIVATION() throws IOException {
      return START + get_LINK_CORE(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()) + "\"motiv\":\"oa:wrong\"" + END;
    }
    
    public String INVALID_ANNO_TYPE = "taglink";
    public static final String FULL_AGENT = "/tag/full_agent.json";
    public static final String WRONG_AGENT_ID_NOT_URL = "/tag/wrong/agent_wrong_id_not_url.json";
    public static final String DESCRIBING_CHO = "/describing/cho.json";
    public static final String LINK_EDM_IS_SIMMILAR_TO = "/link/edmIsSimilarTo.json";
    public static final String LINK_EDM_IS_SIMMILAR_TO_MINIMAL = "/link/edmIsSimilarTo_minimal.json";
    
	static final String VALUE_BATCH_TESTSET = "body_value: \"*-ff45d28b-8717-42f4-a486-f3a62f97fb64\"";

    @Test
    void createWebannoAnnotationWithoutBody() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(null, false, null);
        assertEquals( HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    void createWebAnnotationWithCorruptedBody() throws Exception {
        
        ResponseEntity<String> response = storeTestAnnotationByType(
            false, get_CORRUPTED_JSON(), null, null);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    void createWebannoAnnotationLinkWithWrongMotivation() throws Exception {
        ResponseEntity<String> response = storeTestAnnotationByType(
                false, get_LINK_JSON_WITH_WRONG_MOTIVATION(), null, null
                );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    } 
    
    @Test
    void createWebannoAnnotationByWrongAnnoTypeJsonld() throws Exception {
        ResponseEntity<String> response = storeTestAnnotationByType(
                false, get_TAG_JSON_BY_TYPE_JSONLD(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()), INVALID_ANNO_TYPE, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    void createDescribingAnnoWithoutBodyLanguage() throws Exception { 
        String requestBody = AnnotationTestUtils.getJsonStringInput(DESCRIBING_WITHOUT_BODY_LANGUAGE);
        ResponseEntity<String> response = storeTestAnnotationByType(
            false, requestBody, null, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    void createTaggingAnnoBodyAddressWithoutType() throws Exception { 
        String requestBody = AnnotationTestUtils.getJsonStringInput(TAGGING_BODY_ADDRESS_NO_TYPE);
        ResponseEntity<String> response = storeTestAnnotationByType(
            false, requestBody, null, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    void createTaggingAnnoBodyAddressWithoutStreetAddress() throws Exception { 
        String requestBody = AnnotationTestUtils.getJsonStringInput(TAGGING_BODY_ADDRESS_NO_STREET_ADDRESS);
        ResponseEntity<String> response = storeTestAnnotationByType(
            false, requestBody, null, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }    
    
    @Test
    void createWebannoAnnotationTag() throws Exception {
        
        ResponseEntity<String> response = storeTestAnnotation(TAG_STANDARD, true);

        AnnotationTestUtils.validateResponse(response);
        
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        addToCreatedAnnotations(storedAnno.getIdentifier());
    }
        
    @Test
    void createWebannoAnnotationLink() throws Exception {
        
        String requestBody = AnnotationTestUtils.getJsonStringInput(LINK_STANDARD);
        
        ResponseEntity<String> response = storeTestAnnotationByType(
            false, requestBody, null, null);
        
        AnnotationTestUtils.validateResponse(response);
        
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        addToCreatedAnnotations(storedAnno.getIdentifier());
    }
        
    @Test
    void createWebannoAnnotationLinkWithoutBlanksInMotivation() throws Exception {
        ResponseEntity<String> response = storeTestAnnotationByType(
                true, get_LINK_JSON_WITHOUT_BLANK(), null, null);
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        addToCreatedAnnotations(storedAnno.getIdentifier());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createWebannoAnnotationTagByTypeJsonld() throws Exception {
        
        ResponseEntity<String> response = storeTestAnnotationByType(
                true, get_TAG_JSON_BY_TYPE_JSONLD(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()), WebAnnotationFields.TAG, null);
        
        AnnotationTestUtils.validateResponse(response);
        
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        addToCreatedAnnotations(storedAnno.getIdentifier());
    }    
    
    /**
     * {
     *     "@context": "http://www.europeana.eu/annotation/context.jsonld",
     *     "@type": "oa:Annotation",
     *     "motivation": "oa:tagging",
     *     "annotatedBy": {
     *         "@id": "https://www.historypin.org/en/person/55376/",
     *         "@type": "foaf:Person",
     *         "name": "John Smith"
     *     },
     *     "annotatedAt": "2015-02-27T12:00:43Z",
     *     "serializedAt": "2015-02-28T13:00:34Z",
     *     "serializedBy": "http://www.historypin.org",
     *     "body": " überhaupt ",
     *     "target": "http://data.europeana.eu/item/123/xyz",
     *     "oa:equivalentTo": "https://www.historypin.org/en/item/456"
     * }
     * @throws Exception 
     */
    @Test
    void createWebannoAnnotationTagForValidation() throws Exception {
        
        ResponseEntity<String> response = storeTestAnnotationByType(
            true, get_TAG_JSON_VALIDATION(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()), WebAnnotationFields.TAG, null);
        
        AnnotationTestUtils.validateResponseForTrimming(response);
        
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        addToCreatedAnnotations(storedAnno.getIdentifier());
    }    
    
    @Test
    void createWebannoAnnotationLinkByTypeJsonld() throws Exception {
        
        ResponseEntity<String> response = storeTestAnnotationByType(
                true, get_LINK_JSON_BY_TYPE_JSONLD(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()), WebAnnotationFields.LINK, null);
        
        AnnotationTestUtils.validateResponse(response);
        
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        addToCreatedAnnotations(storedAnno.getIdentifier());
    }   
    
    @Test
    void createAnnotationAgentDetails() throws Exception {
        
        ResponseEntity<String> response = storeTestAnnotation(FULL_AGENT, true);
        AnnotationTestUtils.validateResponse(response);
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        addToCreatedAnnotations(storedAnno.getIdentifier());
        
        String requestBody = AnnotationTestUtils.getJsonStringInput(FULL_AGENT);
        Annotation inputAnno = parseTag(requestBody);
        
        //validate the reflection of input in output!
        //but ignore generated timestamp which is always set by the server
        inputAnno.setGenerated(storedAnno.getGenerated());
        AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
    }

//  @Test
    void agentIDNotUrlMustThrowException() throws Exception {
        
        String requestBody = AnnotationTestUtils.getJsonStringInput(WRONG_AGENT_ID_NOT_URL);
        
        ResponseEntity<String> response = storeTestAnnotationByType(
            false, requestBody, null, null);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());     
        
    }
    
    protected Annotation createAndValidateTag(String inputFile) throws Exception {
      return createTag(inputFile, true, true);
    }
  
    @Test
    void createDescribing1() throws Exception {
        
        String requestBody = AnnotationTestUtils.getJsonStringInput(DESCRIBING_CHO);
        
        ResponseEntity<String> response = storeTestAnnotationByType(
                true, requestBody, null, null);
                                
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        addToCreatedAnnotations(storedAnno.getIdentifier());
                                
        assertTrue(storedAnno.getMotivation().equals(MotivationTypes.DESCRIBING.name().toLowerCase()));
        assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.TEXT.name());
    }
    
    @Test
    void createDescribing2() throws Exception {
        
        String requestBody = AnnotationTestUtils.getJsonStringInput(DESCRIBING_WEB_RESOURCE);
        
        ResponseEntity<String> response = storeTestAnnotationByType(
                true, requestBody, 
                null, null
                );
                                
        assertNotNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        addToCreatedAnnotations(storedAnno.getIdentifier());
                
        assertTrue(storedAnno.getMotivation().equals(MotivationTypes.DESCRIBING.name().toLowerCase()));
        assertTrue(storedAnno.getTarget().getSource() != null);
        assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.TEXT.name());
    }
    
    @Test
    void createLinkAnnotation() throws Exception {
        ResponseEntity<String> response = storeTestAnnotation(LINK_STANDARD, true);
        AnnotationTestUtils.validateResponse(response);  
        
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        addToCreatedAnnotations(storedAnno.getIdentifier());
    }
    
    @Test
    void createIsSimilarToLink() throws Exception {
        String requestBody = AnnotationTestUtils.getJsonStringInput(LINK_EDM_IS_SIMMILAR_TO);
        Annotation inputAnno = AnnotationTestUtils.parseAnnotation(requestBody, MotivationTypes.LINKING);
        Annotation storedAnno = createLink(requestBody);
        addToCreatedAnnotations(storedAnno.getIdentifier());
        //validate the reflection of input in output!
        AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
    }
    
    @Test
    void createIsSimilarToMinimalLink() throws Exception {
        String requestBody = AnnotationTestUtils.getJsonStringInput(LINK_EDM_IS_SIMMILAR_TO_MINIMAL);
        Annotation inputAnno = AnnotationTestUtils.parseAnnotation(requestBody, MotivationTypes.LINKING);
        Annotation storedAnno = createLink(requestBody);
        addToCreatedAnnotations(storedAnno.getIdentifier());
        //validate the reflection of input in output!
        AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
    }

    @Test
    void createLinkForContributingBodyObject() throws Exception {        
        ResponseEntity<String> response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_OBJECT, true);
        AnnotationTestUtils.validateResponse(response); 
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        addToCreatedAnnotations(storedAnno.getIdentifier());
    }
    
    @Test
    void createLinkForContributingBodyString() throws Exception {        
        ResponseEntity<String> response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_BODY_STRING, true);
        AnnotationTestUtils.validateResponse(response);     
        Annotation storedAnno = AnnotationTestUtils.parseResponseBody(response);
        addToCreatedAnnotations(storedAnno.getIdentifier());
    }

    
    @Test
    void createLinkForContributingSpecificTarget_ShouldFail() throws Exception {        
        ResponseEntity<String> response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_TARGET_SPECIFIC, true);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    void createLinkForContributingSpecificTargetWithId_shouldFail() throws Exception {        
        ResponseEntity<String> response = storeTestAnnotation(LINK_FOR_CONTRIBUTING_TARGET_SPECIFIC_ID, true);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    protected Annotation parseCaption(String jsonString) throws JsonParseException {
    MotivationTypes motivationType = MotivationTypes.CAPTIONING;
    return AnnotationTestUtils.parseAnnotation(jsonString, motivationType);
    }

    @Test
    void createMinimalCaption() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(CAPTION_MINIMAL);
    Annotation inputAnno = parseCaption(requestBody);

    Annotation storedAnno = createTestAnnotation(CAPTION_MINIMAL, true);
    addToCreatedAnnotations(storedAnno.getIdentifier());

    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
    }
      
    protected Annotation parseSubtitle(String jsonString) throws JsonParseException {
    MotivationTypes motivationType = MotivationTypes.SUBTITLING;
    return AnnotationTestUtils.parseAnnotation(jsonString, motivationType);
    }
    
    @Test
    void createMinimalSubtitle() throws Exception {

    String requestBody = AnnotationTestUtils.getJsonStringInput(SUBTITLE_MINIMAL);

    Annotation inputAnno = parseSubtitle(requestBody);

    Annotation storedAnno = createTestAnnotation(SUBTITLE_MINIMAL, true);
    addToCreatedAnnotations(storedAnno.getIdentifier());

    // validate the reflection of input in output!
    AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
    
    Assertions.assertTrue(true);
    }
    
    @Test
    void createSemanticTagSimpleMinimal() throws Exception {

        Annotation anno = createAndValidateTag(SEMANTICTAG_SIMPLE_MINIMAL);
        assertNotNull(anno);
        addToCreatedAnnotations(anno.getIdentifier());   
    }

    @Test
    void createSemanticTagSimpleStandard() throws Exception {

        Annotation anno = createAndValidateTag(SEMANTICTAG_SIMPLE_STANDARD);
        assertNotNull(anno);
        addToCreatedAnnotations(anno.getIdentifier());
    }

    @Test
    void createSemanticTagSpecificMinimal() throws Exception {

        Annotation anno = createAndValidateTag(SEMANTICTAG_SPECIFIC_MINIMAL);
        assertNotNull(anno);
        addToCreatedAnnotations(anno.getIdentifier());
    }

    @Test
    void createSemanticTagSpecificStandard() throws Exception {

        Annotation anno = createAndValidateTag(SEMANTICTAG_SPECIFIC_STANDARD);
        assertNotNull(anno);
        addToCreatedAnnotations(anno.getIdentifier());
    }

    @Test
    void createSemanticTagWebResource() throws Exception {

        Annotation storedAnno = createTag(SEMANTICTAG_WEB_RESOURCE, false, true);
        addToCreatedAnnotations(storedAnno.getIdentifier());
        assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
        assertTrue(storedAnno.getTarget().getSource() != null);
        assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.SEMANTIC_TAG.name());
    }

    @Test
    void createSemanticTagEntity() throws Exception {

        Annotation storedAnno = createTag(SEMANTICTAG_ENTITY, false, true);
        addToCreatedAnnotations(storedAnno.getIdentifier());
        assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
        assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.AGENT.name());
        EdmAgent agentBody = (EdmAgent) ((EdmAgentBody) storedAnno.getBody()).getAgent();
        assertNotNull(agentBody.getDateOfBirth());
        assertNotNull(agentBody.getDateOfDeath());
        assertNotNull(agentBody.getPlaceOfBirth());
        assertNotNull(agentBody.getPlaceOfDeath());
        assertNotNull(agentBody.getPrefLabel());
    }

    @Test
    void createSemanticTagAgentEntity() throws Exception {

        Annotation storedAnno = createTag(SEMANTICTAG_AGENT_ENTITY, false, true);
        addToCreatedAnnotations(storedAnno.getIdentifier());
        assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
        assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.AGENT.name());
        EdmAgent agentBody = (EdmAgent) ((EdmAgentBody) storedAnno.getBody()).getAgent();
        assertNotNull(agentBody.getPrefLabel());
        assertTrue(agentBody.getPrefLabel().size() == 43);
        assertNotNull(agentBody.getDateOfBirth());
        assertNotNull(agentBody.getDateOfDeath());
    }

    @Test
    void createTagBodyText() throws Exception {
        
        String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_BODY_TEXT);
        
        Annotation storedAnno = createTag(requestBody);
        assertNotNull(storedAnno);
        addToCreatedAnnotations(storedAnno.getIdentifier());
        
        Annotation inputAnno = parseTag(requestBody);
        //validate the reflection of input in output!
        AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
    }
    
    @Test
    void createTagMinimal() throws Exception {
        
        String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_MINIMAL);
        
        Annotation storedAnno = createTag(requestBody);
        assertNotNull(storedAnno);
        addToCreatedAnnotations(storedAnno.getIdentifier());
        
        Annotation inputAnno = parseTag(requestBody);
        
        //validate the reflection of input in output!
        AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
    }
    

    @Test
    void createTagStandard() throws Exception {
        
        String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_STANDARD);
        
        Annotation storedAnno = createTag(requestBody);
        addToCreatedAnnotations(storedAnno.getIdentifier());
        
        Annotation inputAnno = parseTag(requestBody);
        //validate the reflection of input in output!
        //but ignore generated timestamp which is always set by the server
        inputAnno.setGenerated(storedAnno.getGenerated());
        AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
    }
    
    @Test
    void createGeoTag() throws Exception {
        
        String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_GEOTAG);
        
        Annotation inputAnno = parseTag(requestBody);
        
        Annotation storedAnno = createTag(requestBody);
        assertNotNull(storedAnno);
        
        addToCreatedAnnotations(storedAnno.getIdentifier());
        
        assertTrue(BodyInternalTypes.isGeoTagBody(storedAnno.getBody().getInternalType()));
        //validate the reflection of input in output!
        AnnotationTestUtils.validateOutputAgainstInput(storedAnno, inputAnno);
    }
    
    @Test
    void createWrongTag() throws Exception {
        
        String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_MINIMAL_WRONG);
        
        ResponseEntity<String> response = storeTestAnnotationByType(true, requestBody,  WebAnnotationFields.TAG, null);
        
        assertEquals(response.getStatusCode().value(), HttpStatus.BAD_REQUEST.value());
    }
    
    @Test
    void createTagWithoutBody() throws Exception {
        
        String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_WITHOUT_BODY);
        
        ResponseEntity<String> response = storeTestAnnotationByType(true, requestBody,  WebAnnotationFields.TAG, null);
        
        assertEquals(response.getStatusCode().value(), HttpStatus.BAD_REQUEST.value());
    }
    
    
    @Test
    void createWrongGeoTagLat() throws Exception {
        
        String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_GEO_WRONG_LAT);
        
        ResponseEntity<String> response = storeTestAnnotationByType(true, requestBody,  WebAnnotationFields.TAG, null);
        
        assertEquals(response.getStatusCode().value(), HttpStatus.BAD_REQUEST.value());
    }
    
    @Test
    void createWrongGeoTagLong() throws Exception {
        
        String requestBody = AnnotationTestUtils.getJsonStringInput(TAG_GEO_WRONG_LONG);
        
        ResponseEntity<String> response = storeTestAnnotationByType(true, requestBody,  WebAnnotationFields.TAG, null);
        
        assertEquals(response.getStatusCode().value(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void createCanonicalTag() throws Exception {
            
      Annotation storedAnno = createAndValidateTag(TAG_CANONICAL);
      assertNotNull(storedAnno);
      addToCreatedAnnotations(storedAnno.getIdentifier());
    }
    
    @Test
    void createViaTagString() throws Exception {
        
        Annotation storedAnno = createAndValidateTag(TAG_VIA_STRING);
        assertNotNull(storedAnno);
        addToCreatedAnnotations(storedAnno.getIdentifier());
    }
    
    @Test
    void createViaTagArray() throws Exception {

        Annotation storedAnno = createAndValidateTag(TAG_VIA_ARRAY);
        assertNotNull(storedAnno);
        addToCreatedAnnotations(storedAnno.getIdentifier());
    }

    @Test
    void createSemanticTagWithVcardAddress() throws Exception {

        Annotation storedAnno = createTag(SEMANTICTAG_VCARD_ADDRESS, false, true);
        addToCreatedAnnotations(storedAnno.getIdentifier());
        assertTrue(storedAnno.getMotivation().equals(MotivationTypes.TAGGING.name().toLowerCase()));
        assertEquals(storedAnno.getBody().getInternalType(), BodyInternalTypes.VCARD_ADDRESS.name());
        assertEquals(
                ((VcardAddressBody) storedAnno.getBody()).getAddress().getVcardCountryName()
                ,"The Netherlands");
        assertNotNull(
                ((VcardAddressBody) storedAnno.getBody()).getAddress().getVcardHasGeo());
        assertNotNull(
                ((VcardAddressBody) storedAnno.getBody()).getAddress().getVcardLocality());
        assertNotNull(
                ((VcardAddressBody) storedAnno.getBody()).getAddress().getVcardPostalCode());
        assertNotNull(
                ((VcardAddressBody) storedAnno.getBody()).getAddress().getVcardPostOfficeBox());
        assertNotNull(
                ((VcardAddressBody) storedAnno.getBody()).getAddress().getVcardStreetAddress());
    }
}

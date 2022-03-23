package eu.europeana.annotation.web.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.util.AnnotationTestObjectBuilder;
import eu.europeana.annotation.definitions.model.utils.AnnotationIdHelper;
import eu.europeana.annotation.definitions.model.view.AnnotationView;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.service.SolrAnnotationService;
import eu.europeana.annotation.utils.parse.AnnotationLdParser;
import eu.europeana.annotation.utils.serialize.AnnotationLdSerializer;
import eu.europeana.annotation.web.exception.InternalServerException;
import eu.europeana.annotation.web.service.AdminService;
import eu.europeana.annotation.web.service.AnnotationService;


/**
 * Unit test for the Web Annotation service
 * @deprecated adapt to use AnnotationLdParser and AnnotationLdDeserializerDeprecated
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration({ "/annotation-web-context.xml", "/annotation-mongo-test.xml"//, "/annotation-solr-test.xml" 
	})
public class WebAnnotationServiceTest extends AnnotationTestObjectBuilder{

	public static String TEST_RO_VALUE = "Vlad Tepes";
	public static String TEST_EN_VALUE = "Vlad the Impaler";
	
    @Resource
    AnnotationLdSerializer annotationLdSerializer;

	@Resource 
	AnnotationService webAnnotationService;
	
	@Resource
	SolrAnnotationService solrService;
	
	@Resource 
	AdminService adminService;
	
	
	protected AnnotationIdHelper annotationIdHelper = new AnnotationIdHelper();
	
	@Test
	public void testStoreAnnotationInDbRetrieveAndSerialize() 
			throws MalformedURLException, IOException, AnnotationServiceException, JsonParseException {
		
		Annotation testAnnotation = createTestAnnotation();		

		/**
		 * Serialize an original Annotation test object.
		 */
//        AnnotationLdSerializer origAnnotationLd = new AnnotationLdSerializer(testAnnotation);
        
//        String original = origAnnotationLd.toString();
//        AnnotationLd.toConsole("", original);
//        String expectedOrig = "{\"@context\":{\"oa\":\"http://www.w3.org/ns/oa-context-20130208.json\"},\"@id\":\"http://data.europeana.eu/annotation/webanno/null\",\"@type\":\"OBJECT_TAG\",\"annotatedAt\":\"2012-11-10T09:08:07\",\"annotatedBy\":{\"@id\":\"open_id_1\",\"@type\":\"foaf:Person\",\"name\":\"annonymous web user\"},\"body\":{\"@type\":\"[SEMANTIC_TAG,oa:Tag,cnt:ContentAsText,dctypes:Text]\",\"chars\":\"Vlad Tepes\",\"foaf:page\":\"https://www.freebase.com/m/035br4\",\"format\":\"text/plain\",\"language\":\"ro\",\"multilingual\":\"\"},\"equivalentTo\":\"http://historypin.com/annotation/1234\",\"motivation\":\"TAGGING\",\"serializedAt\":\"2012-11-10T09:08:07\",\"serializedBy\":{\"@id\":\"open_id_2\",\"@type\":\"prov:Software\",\"foaf:homepage\":\"http://annotorious.github.io/\",\"name\":\"Annotorious\"},\"styledBy\":{\"@type\":\"oa:CSS\",\"source\":\"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\",\"styleClass\":\"annotorious-popup\"},\"target\":{\"@type\":\"[oa:IMAGE]\",\"contentType\":\"image/jpeg\",\"httpUri\":\"http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE\",\"selector\":{\"@type\":\"\",\"dimensionMap\":\"\"},\"source\":{\"@id\":\"http://europeana.eu/portal/record//testCollection/testObject.html\",\"contentType\":\"text/html\",\"format\":\"dctypes:Text\"},\"type\":\"oa:IMAGE\"},\"type\":\"OBJECT_TAG\"}";
        
//        assertEquals(expectedOrig, original);
		
//        String origIndent = origAnnotationLd.toString(4);
//        AnnotationLd.toConsole("", origIndent);
        		
		/**
		 * Store Annotation in database.
		 */
		Annotation webAnnotation = webAnnotationService.storeAnnotation(testAnnotation);
		
		if (StringUtils.isBlank(webAnnotation.getType())) {
			webAnnotation.setType(AnnotationTypes.OBJECT_TAG.name());
		}
		
		System.out.println("testAnnotation: " + testAnnotation.toString());
		System.out.println("webAnnotation: " + webAnnotation.toString());
		
		assertTrue(webAnnotation.getIdentifier() != 0);
		assertEquals(testAnnotation.getBody(), webAnnotation.getBody());
		assertEquals(testAnnotation.getTarget(), webAnnotation.getTarget());
		
		/**
		 * Serialize Annotation object that was retrieved from a database.
		 */
//		(JsonLd) webAnnotation
		annotationLdSerializer.setAnnotation(webAnnotation);
        AnnotationLdParser parser = new AnnotationLdParser();
        
        String actual = annotationLdSerializer.toString();
        System.out.println(actual);
//        AnnotationLd.toConsole("", actual);
        
        String actualIndent = annotationLdSerializer.toString(4);
//        AnnotationLd.toConsole("", actualIndent);
        
        /**
         * read Annotation object from the serialized AnnotationLd object.
         */
        Annotation annotationFromAnnotationLd = parser.parseAnnotation(null, actualIndent);
        
        /**
         * Compare original Annotation object with retrieved serialized Annotation object.
         */
        //TODO: update test criteria
        assertEquals(annotationFromAnnotationLd.getTarget(), testAnnotation.getTarget());
        assertEquals(annotationFromAnnotationLd.getBody(), testAnnotation.getBody());
	}
		
	@Test
	public void testCreateAnnotationWebanno() 
			throws MalformedURLException, IOException, AnnotationServiceException {
		
		Annotation testAnnotation = createTestAnnotation();		

		/**
		 * Store Annotation in database.
		 */
		Annotation webAnnotation = webAnnotationService.storeAnnotation(testAnnotation);
		
		if (StringUtils.isBlank(webAnnotation.getType())) {
			webAnnotation.setType(AnnotationTypes.OBJECT_TAG.name());
		}
		
		System.out.println("testAnnotation: " + testAnnotation.toString());
		System.out.println("webAnnotation: " + webAnnotation.toString());
		
		assertTrue(webAnnotation.getIdentifier() != 0);
	}

	/**
	 * Create a test annotation object.
	 * @return Annotation
	 */
	Annotation createTestAnnotation() {
		Annotation testAnnotation = createBaseObjectTagInstance();			
		return testAnnotation;
	}

	protected String getBaseUrl() {
		return ((BaseAnnotationServiceImpl)webAnnotationService).getConfiguration().getAnnotationBaseUrl();
	}

	@Test
	public void testDeleteAnnotation() 
			throws MalformedURLException, IOException, AnnotationServiceException, InternalServerException {
		
		Annotation testAnnotation = createTestAnnotation();		
        
		/**
		 * Store Annotation in database.
		 */
		Annotation storedAnnotation = webAnnotationService.storeAnnotation(testAnnotation);
		
		/**
		 * Delete Annotation.
		 */
		adminService.deleteAnnotation(
				storedAnnotation.getIdentifier());
		
		/**
		 * Search Annotation.
		 */
		AnnotationView anno = solrService.searchById(((PersistentAnnotation) storedAnnotation).getId().toString());
//		List<? extends Annotation> resList = webAnnotationService. search Annotations(
//				, "0", "10");
//		assertTrue(resList == null || resList.size() == 0);
		assertNull(anno);
	}
		
	@Test
	public void testIndexAnnotation() 
			throws MalformedURLException, IOException, AnnotationServiceException {
		
		Annotation testAnnotation = createTestAnnotation();		
        
		/**
		 * Search Annotation.
		 */
		 long oldEntries = solrService.search(
				testAnnotation.getBody().getValue(), "0", "1").getResultSize();

		/**
		 * Store Annotation in database.
		 */
		Annotation storedAnnotation = webAnnotationService.storeAnnotation(testAnnotation);
		
		/**
		 * Reindex Annotation.
		 */
		//to do use store or update for reindexing
//		webAnnotationService.up  (
//				storedAnnotation.getAnnotationId());
		
		/**
		 * Search Annotation.
		 */
		long currentEntries = solrService.search(
			testAnnotation.getBody().getValue(), "0", "1").getResultSize();
		assertTrue(currentEntries == oldEntries);
	}
		
	@Test
	public void testDisableAnnotation() 
			throws MalformedURLException, IOException, AnnotationServiceException {
		
		Annotation testAnnotation = createTestAnnotation();		

		/**
		 * Store Annotation in database.
		 */
		Annotation storedAnnotation = webAnnotationService.storeAnnotation(testAnnotation);
		
		/**
		 * Reindex Annotation.
		 */
		Annotation disabledAnnotation = webAnnotationService.disableAnnotation(
//				storedAnnotation.getAnnotationId().getResourceId()
				storedAnnotation.getIdentifier());
		
		/**
		 * Search Annotation.
		 */
		//disabled annotations will not be returned by the service 
		assertTrue(disabledAnnotation == null);
	}
		
}

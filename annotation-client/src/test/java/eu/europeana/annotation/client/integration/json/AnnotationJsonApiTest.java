package eu.europeana.annotation.client.integration.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import eu.europeana.annotation.client.AnnotationJsonApi;
import eu.europeana.annotation.client.AnnotationJsonApiImpl;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.util.AnnotationTestObjectBuilder;


public class AnnotationJsonApiTest extends AnnotationTestObjectBuilder{

    private AnnotationJsonApiImpl annotationJsonApi;
    
    @Before
    public void initObjects() {
    	annotationJsonApi = new AnnotationJsonApiImpl();
    }

	public static String annotationJson = 
		"{" +
		"\"annotatedAt\": 1403852113248," + 
		"\"type\": \"OBJECT_TAG\"," + 
		"\"annotatedBy\": {" +
			"\"agentType\": \"foaf:Person\"," +
			"\"name\": \"annonymous web user\"," +
			"\"homepage\": null," +
			"\"mbox\": null," + 
			"\"openId\": null" + 
		"}," +
		"\"body\": {" + 
			"\"contentType\": \"Link\"," + 
			"\"mediaType\": null," + 
			"\"httpUri\": \"https://www.freebase.com/m/035br4\"," + 
			"\"language\": \"ro\"," + 
			"\"value\": \"Vlad Tepes\"," + 
			"\"multilingual\": \"[ro:Vlad Tepes,en:Vlad the Impaler]\"," + 
			"\"bodyType\": \"[oa:Tag,euType:SEMANTIC_TAG]\"" + 
		"}," + 
		"\"target\": {" + 
			"\"contentType\": \"text-html\"," + 
			"\"mediaType\": \"image\"," + 
			"\"language\": \"en\"," + 
			"\"value\": \"Vlad IV. Tzepesch, der Pfaehler, Woywode der Walachei 1456-1462 (gestorben 1477)\"," + 
			"\"httpUri\": \"http://europeana.eu/portal/record/15502/GG_8285.html\"," + 
			"\"targetType\": \"oa:WebPage\"" + 
		"}," + 
		"\"serializedAt\": \"\"," + 
		"\"serializedBy\": {" + 
			"\"agentType\": \"prov:Software\"," + 
			"\"name\": \"annonymous web user\"," + 
			"\"homepage\": null," + 
			"\"mbox\": null," + 
			"\"openId\": null" + 
		"}," + 
		"\"styledBy\":{" + 
			"\"contentType\": \"style\"," + 
			"\"mediaType\": \"text/css\"," + 
			"\"httpUri\": \"http://annotorious.github.io/latest/themes/dark/annotorious-dark.css\"," + 
			"\"value\": null," + 
			"\"annotationClass\": \".annotorious-popup\"" + 
		"}" + 
		"}";

//	private static final String TEST_COLLECTION_CLIENT_TEST_OBJECT = "/testCollection/testObject";

//	@Test
//	public void createSemanticTag(){
//		
//		AnnotationManagerApi annotationManager = new AnnotationManagerApi();
//		SemanticTag tag = new SemanticTagImpl();
//		tag.setCreator("sergiu");
//		tag.setEuropeanaId(TEST_COLLECTION_CLIENT_TEST_OBJECT);
//		tag.setLabel("Co-Creation Workshop");
//		
//		List<String> entityUrls = new ArrayList<String>();
//		entityUrls.add("http://en.wikipedia.org/wiki/Co-creation");
//		tag.setNamedEntityIdList(entityUrls);
//		
//		List<String> entities = new ArrayList<String>();
//		entities.add("Co-Creation");
//		tag.setNamedEntityLabelList(entities);
//				
//		Annotation annotation = annotationManager.createAnnotation(tag);
//		assertNotNull(annotation);
//		assertNotNull(annotation.getAnnotationNr());
//		//check the concrete class because ImageAnnotation extends SemanticTag
//		assertTrue(annotation instanceof SemanticTagImpl);		
//	} 
	
//	@Test
//	public void createImageAnnotation(){
//		
//		AnnotationManagerApi annotationManager = new AnnotationManagerApi();
//		ImageAnnotation userAnnotation = new ImageAnnotationImpl();
//		userAnnotation.setCreator("sergiu");
//		userAnnotation.setEuropeanaId(TEST_COLLECTION_CLIENT_TEST_OBJECT);
//		userAnnotation.setImageUrl("http://pro.europeana.eu/image/image_gallery?uuid=71c5cfa3-3b85-41e8-8eaa-b2ff7513aa13&groupId=1538974&t=1383063277580");
//		userAnnotation.setText("Brendan Knowlton and Steven Stegers at Europeana Creative Co-Creation Workshop ");
////		Point p1 = ;
////		Point p = new PointImpl(0, 0);
////		Point p1 = new PointImpl(0, 0);
////		Point p1 = new PointImpl(0, 0);
//		
//		List<Point> shape = new ArrayList<Point>();
//		shape.add(new PointImpl(0, 0));
//		shape.add(new PointImpl(0, 5));
//		shape.add(new PointImpl(5, 5));
//		shape.add(new PointImpl(5, 0));
//		
//		userAnnotation.setShape(shape);
//		
//		List<String> entities = new ArrayList<String>();
//		entities.add("Breandán Knowlton");
//		entities.add("Breandán Knowlton");
//		entities.add("Steven Stegers");
//		userAnnotation.setNamedEntityLabelList(entities);
//		
//		List<String> entityUrls = new ArrayList<String>();
//		entityUrls.add("https://twitter.com/bfk");
//		entityUrls.add("https://basecamp.com/1768384/people/323635-breandan-knowlton");
//		entityUrls.add("https://basecamp.com/1768384/people/323654-steven-stegers");
//		userAnnotation.setNamedEntityIdList(entityUrls);
//		
//		ImageAnnotation annotation = (ImageAnnotation)annotationManager.createAnnotation(userAnnotation);
//		assertNotNull(annotation);
//		assertNotNull(annotation.getAnnotationNr());
//		assertTrue(annotation instanceof ImageAnnotation);
//		assertEquals(entities.size(), annotation.getNamedEntityLabelList().size());
//		assertEquals(entityUrls.size(), annotation.getNamedEntityIdList().size());
//		assertEquals(4, annotation.getShape().size());
//		
//				
//	} 
	
	@Test
	public void createAnnotation() {
		
//		AnnotationJsonApiImpl annotationJsonApi = new AnnotationJsonApiImpl();
		
		/**
		 * Create a test annotation object.
		 */
		Annotation testAnnotation = createBaseObjectTagInstance();	
//		Annotation annotation = annotationJsonApi.createAnnotation(testAnnotation);
		String annotation = annotationJsonApi.createAnnotation(testAnnotation);
//		String annotation = annotationJsonApi.createAnnotation(annotationJson);
		assertNotNull(annotation);
	}
	
	@Test
	public void getAnnotations() {
		
//		AnnotationJsonApiImpl annotationJsonApi = new AnnotationJsonApiImpl();
		
		/**
		 * Create object within the test and do not rely on the objects stored in the database.
		 */
		Annotation testAnnotation = createBaseObjectTagInstance();	
		annotationJsonApi.createAnnotation(testAnnotation);
		
		AnnotationJsonApi retrievalApi = new AnnotationJsonApiImpl();
//		List<Annotation> results = retrievalApi.getAnnotations("testCollection", "testObject", "webanno");
		String results = retrievalApi.getAnnotations("testCollection", "testObject", "webanno");
		assertNotNull(results);
//		assertTrue(results.size() > 0);
//		Gson gson = new Gson();
//		
//		for (Annotation annotation : results) {
//			System.out.println(gson.toJson(annotation));			
//		}
		
	}
	
//	@Test(expected = TechnicalRuntimeException.class)
//	public void getAnnotationError() {
//		AnnotationJsonApi retrievalApi = new AnnotationJsonApiImpl();
//		retrievalApi.getAnnotation("testCollection", "testObject", -1);		
//	}
	
//	@Test
	public void getAnnotation() {
		
//		AnnotationJsonApiImpl annotationJsonApi = new AnnotationJsonApiImpl();
		
		/**
		 * Create object within the test and do not rely on the objects stored in the database.
		 */
		Annotation testAnnotation = createBaseObjectTagInstance();	
		annotationJsonApi.createAnnotation(testAnnotation);
		
		List<Annotation> results = annotationJsonApi.getAnnotations("testCollection", "testObject");
		
		if(results.isEmpty()){
			System.out.println("No objects found in the database, test skipped");
			return;
		}
		
		Annotation anno = results.get(0);
		
		assertNotNull(anno);
		assertEquals(anno.getType(), testAnnotation.getType());
		
		
	}
	
}

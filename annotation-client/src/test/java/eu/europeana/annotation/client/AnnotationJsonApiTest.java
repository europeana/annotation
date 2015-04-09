package eu.europeana.annotation.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.gson.Gson;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.util.AnnotationTestObjectBuilder;


public class AnnotationJsonApiTest extends AnnotationTestObjectBuilder{

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
		
		AnnotationJsonApiImpl annotationJsonApi = new AnnotationJsonApiImpl();
		
		/**
		 * Create a test annotation object.
		 */
		Annotation testAnnotation = createBaseObjectTagInstance();	
		Annotation annotation = annotationJsonApi.createAnnotation(testAnnotation);
		assertNotNull(annotation);
	}
	
	@Test
	public void getAnnotations() {
		
		AnnotationJsonApiImpl annotationJsonApi = new AnnotationJsonApiImpl();
		
		/**
		 * Create object within the test and do not rely on the objects stored in the database.
		 */
		Annotation testAnnotation = createBaseObjectTagInstance();	
		annotationJsonApi.createAnnotation(testAnnotation);
		
		AnnotationJsonApi retrievalApi = new AnnotationJsonApiImpl();
		List<Annotation> results = retrievalApi.getAnnotations("testCollection", "testObject");
		assertNotNull(results);
		assertTrue(results.size() > 0);
		Gson gson = new Gson();
		
		for (Annotation annotation : results) {
			System.out.println(gson.toJson(annotation));			
		}
		
	}
	
//	@Test(expected = TechnicalRuntimeException.class)
//	public void getAnnotationError() {
//		AnnotationJsonApi retrievalApi = new AnnotationJsonApiImpl();
//		retrievalApi.getAnnotation("testCollection", "testObject", -1);		
//	}
	
	@Test
	public void getAnnotation() {
		
		AnnotationJsonApiImpl annotationJsonApi = new AnnotationJsonApiImpl();
		
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

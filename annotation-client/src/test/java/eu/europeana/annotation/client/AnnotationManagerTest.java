package eu.europeana.annotation.client;


public class AnnotationManagerTest {

	private static final String TEST_COLLECTION_CLIENT_TEST_OBJECT = "/testCollection/testObject";

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
}

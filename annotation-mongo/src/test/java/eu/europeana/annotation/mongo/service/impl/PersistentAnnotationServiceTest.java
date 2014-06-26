package eu.europeana.annotation.mongo.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.ObjectTag;
import eu.europeana.annotation.definitions.model.body.TagBody;
import eu.europeana.annotation.definitions.model.body.impl.PlainTagBody;
import eu.europeana.annotation.definitions.model.body.impl.SemanticTagBody;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.annotation.mongo.service.PersistentAnnotationService;
import eu.europeana.corelib.db.dao.NosqlDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/annotation-mongo-context.xml",
		"/annotation-mongo-test.xml" })
public class PersistentAnnotationServiceTest extends AnnotationTestDataBuilder {

	final static String TEST_EUROPEANA_ID = "/testCollection/testObject";

	// @Resource(name="corelib_solr_mongoProvider")
	// private MongoProvider mongoProvider;

	@Resource PersistentAnnotationService annotationService;

	@Resource(name = "annotation_db_annotationDao")
	NosqlDao<PersistentAnnotation, AnnotationId> annotationDao;

	/**
	 * Initialize the testing session
	 * 
	 * @throws IOException
	 */
	@Before
	public void setup() throws IOException {
		annotationDao.getCollection().drop();
	}

	/**
	 * Cleaning the testing session's data
	 * 
	 * @throws IOException
	 */
	@After
	public void tearDown() throws IOException {
		// annotationDao.getCollection().drop();
	}

	@Test
	public void testStoreObjectTag() {

		ObjectTag persistentObject = buildObjectTag();

		PlainTagBody body = buildPlainTagBody();
		persistentObject.setHasBody(body);

		// persistentObject.setLabel("testTag");
		Annotation storedTag = annotationService.store(persistentObject); 
		checkAnnotation(persistentObject, storedTag);

		assertTrue(storedTag instanceof ObjectTag);
	}
	
	@Test
	public void testStoreObjectTagWithMotivation() {

		ObjectTag persistentObject = buildObjectTag();

		// set body
		PlainTagBody body = buildPlainTagBody();
		persistentObject.setHasBody(body);
		
		persistentObject.setMotivatedBy(MotivationTypes.TAGGING.name());
		
		// persistentObject.setLabel("testTag");
		Annotation storedTag = annotationService.store(persistentObject); 
		
		assertEquals(MotivationTypes.TAGGING.name(), storedTag.getMotivatedBy());
		assertEquals(MotivationTypes.TAGGING, storedTag.getMotivationType());
	}

	@Test
	public void testStoreSemanticObjectTag() {

		ObjectTag persistentObject = buildObjectTag();
		
		//set body
		SemanticTagBody body = buildSemanticTagBody();
		persistentObject.setHasBody(body);
		
		//persistentObject.setLabel("testTag");
		Annotation storedTag = annotationService.store(persistentObject); 
		checkAnnotation(persistentObject, storedTag);

		assertTrue(storedTag instanceof ObjectTag);
		assertTrue(storedTag.getHasBody() instanceof SemanticTagBody);
		assertNotNull(((TagBody)storedTag.getHasBody()).getTagId());
	}

	@Test
	public void testUniqueSemanticTag() {

		ObjectTag firstObject = buildObjectTag();

		SemanticTagBody body = buildSemanticTagBody();
		firstObject.setHasBody(body);

		// store first annotation
		Annotation firstAnnotation = annotationService.store(firstObject);

		ObjectTag secondObject = buildObjectTag();

		// set body
		SemanticTagBody bodyEn = buildSemanticTagBody();
		// TODO: change to predefined values
		bodyEn.setValue("Vlad The Impaler");
		bodyEn.setLanguage("en");
		secondObject.setHasBody(bodyEn);
		
		// store first annotation
		Annotation secondAnnotation = annotationService.store(secondObject);

		assertFalse(firstAnnotation.getAnnotationId().equals(secondAnnotation.getAnnotationId()));
		assertFalse(firstAnnotation.getAnnotationId().toString().equals(secondAnnotation.getAnnotationId().toString()));
		
		assertFalse(firstAnnotation.getHasBody().equals(secondAnnotation.getHasBody()));
		
		//both annotations must use the same semantic tag
		String firstTagId = ((TagBody) firstAnnotation.getHasBody()).getTagId();
		String secondTagId = ((TagBody) secondAnnotation.getHasBody()).getTagId();
		
		assertEquals(firstTagId, secondTagId);

	}

	
	

	// @Test(expected=AnnotationValidationException.class)
	// public void testStoreValidationError(){
	// ImageAnnotation annotation = createSimpleAnnotationInstance();
	// annotation.setEuropeanaId(null);
	// //expect exception
	// annotationService.store(annotation);
	//
	// }
	//
	// @Test(expected=AnnotationValidationException.class)
	// public void testStoreValidationErrorObjectId(){
	// ImageAnnotation annotation = createSimpleAnnotationInstance();
	// Annotation storedAnnotation = storeAnCheck(annotation);
	// //expect exception
	// annotationService.store(storedAnnotation);
	//
	// }
	//
	// @Test
	// public void testStoreTag() {
	//
	// SemanticTag persistantObject = new SemanticTagImpl();
	// persistantObject.setEuropeanaId(TEST_EUROPEANA_ID);
	//
	// persistantObject.setLabel("testTag");
	//
	// storeAnCheck(persistantObject);
	//
	// }
	//
	// @Test
	// public void testStoreSimpleImageAnnotation() {
	//
	// ImageAnnotation annotation = createSimpleAnnotationInstance();
	// ImageAnnotation storedAnnotation = (ImageAnnotation)
	// storeAnCheck(annotation);
	// assertNotNull(storedAnnotation.getText());
	// }
	//
	// @Test
	// public void testStoreShapedImageAnnotation() {
	//
	// ImageAnnotation annotation = createSimpleAnnotationInstance();
	// List<Point> rectangle = new LinkedList<Point>();
	// rectangle.add(new MongoPointImpl(1, 1));
	// rectangle.add(new MongoPointImpl(5, 1));
	// rectangle.add(new MongoPointImpl(5, 5));
	// rectangle.add(new MongoPointImpl(1, 5));
	//
	// annotation.setShape(rectangle);
	//
	// ImageAnnotation storedAnnotation = (ImageAnnotation) storeAnCheck(
	// annotation);
	// assertNotNull(storedAnnotation.getText());
	// assertEquals(4, storedAnnotation.getShape().size());
	// }
	//
	// private ImageAnnotation createSimpleAnnotationInstance() {
	// ImageAnnotation annotation = new ImageAnnotationImpl();
	// annotation.setEuropeanaId(TEST_EUROPEANA_ID);
	// annotation.setImageUrl("http://localhost/europeanaImageId.jpg");
	// annotation.setText("this is a test image annotation");
	// annotation.setCreator("testCreator");
	// return annotation;
	// }
	//
	// @Test
	// public void testAnnotations() {
	// // test all individual methods together ... without deleting the
	// // database
	// testStoreTag();
	// testStoreSimpleImageAnnotation();
	// testStoreShapedImageAnnotation();
	// }
	//
	// @Test
	// public void testListAnnotations() {
	// // test all individual methods together ... without deleting the
	// // database
	// testStoreTag();
	// testStoreSimpleImageAnnotation();
	// testStoreShapedImageAnnotation();
	//
	// List<? extends Annotation> results =
	// annotationService.getAnnotationList(TEST_EUROPEANA_ID);
	// assertTrue(results.size() > 0);
	// for (Annotation annotation : results) {
	// assertEquals(TEST_EUROPEANA_ID, annotation.getResourceId());
	// System.out.println("annotationType/annotationNumber: " +
	// annotation.getType() + "/" + annotation.getAnnotationNr());
	// }
	// }
	//
	//
	// @Test
	// public void getAnnotation() {
	//
	// //need a stored annotation in order to have an annotationNr
	// Annotation anno = createSimpleAnnotationInstance();
	// Annotation storedAnnotation = annotationService.store(anno);
	// assertNotNull(storedAnnotation.getAnnotationId().getAnnotationNr());
	//
	// //retrieve annotation
	// Annotation retrievedAnnotation =
	// annotationService.getAnnotation(storedAnnotation.getResourceId(),
	// storedAnnotation.getAnnotationNr());
	//
	// assertNotNull(retrievedAnnotation);
	// assertEquals(retrievedAnnotation.getAnnotationId().getResourceId(),
	// storedAnnotation.getAnnotationId().getResourceId());
	// assertEquals(retrievedAnnotation.getAnnotationId().getAnnotationNr(),
	// storedAnnotation.getAnnotationId().getAnnotationNr());
	//
	//
	// }
	//
	//
}

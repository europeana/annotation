package eu.europeana.annotation.mongo.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europeana.annotation.definitions.exception.AnnotationValidationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.ImageAnnotation;
import eu.europeana.annotation.definitions.model.SemanticTag;
import eu.europeana.annotation.definitions.model.shape.Point;
import eu.europeana.annotation.mongo.model.ImageAnnotationImpl;
import eu.europeana.annotation.mongo.model.SemanticTagImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.annotation.mongo.model.shape.MongoPointImpl;
import eu.europeana.annotation.mongo.service.PersistentAnnotationService;
import eu.europeana.corelib.db.dao.NosqlDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/annotation-mongo-context.xml",
		"/annotation-mongo-test.xml" })
public class PersistentAnnotationServiceTest {

	final static String TEST_EUROPEANA_ID = "/testCollection/testObject";
	
	// @Resource(name="corelib_solr_mongoProvider")
	// private MongoProvider mongoProvider;

	@Resource
	private PersistentAnnotationService annotationService;

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

	private Annotation storeAnCheck(Annotation persistantObject) {
		persistantObject.setCreator("testCreator");

		Annotation storedAnnotation = annotationService.store(persistantObject);

		assertEquals(persistantObject.getAnnotationId().getEuropeanaId(),
				storedAnnotation.getAnnotationId().getEuropeanaId());
		
		assertNotNull(storedAnnotation.getAnnotationId().getAnnotationNr());
		assertNotNull(((PersistentAnnotation)storedAnnotation).getCreationTimestamp());
		assertEquals(((PersistentAnnotation)storedAnnotation).getCreationTimestamp(),
				((PersistentAnnotation)storedAnnotation).getLastUpdateTimestamp());

		return storedAnnotation;
	}

	@Test(expected=AnnotationValidationException.class) 
	public void testStoreValidationError(){
		ImageAnnotation annotation = createSimpleAnnotationInstance();
		annotation.setEuropeanaId(null);
		//expect exception
		annotationService.store(annotation);
		
	}
	
	@Test(expected=AnnotationValidationException.class) 
	public void testStoreValidationErrorObjectId(){
		ImageAnnotation annotation = createSimpleAnnotationInstance();
		Annotation storedAnnotation = storeAnCheck(annotation);
		//expect exception
		annotationService.store(storedAnnotation);
		
	}
	
	@Test
	public void testStoreTag() {

		SemanticTag persistantObject = new SemanticTagImpl();
		persistantObject.setEuropeanaId(TEST_EUROPEANA_ID);
		
		persistantObject.setLabel("testTag");

		storeAnCheck(persistantObject);

	}

	@Test
	public void testStoreSimpleImageAnnotation() {

		ImageAnnotation annotation = createSimpleAnnotationInstance();
		ImageAnnotation storedAnnotation = (ImageAnnotation) storeAnCheck(annotation);
		assertNotNull(storedAnnotation.getText());
	}

	@Test
	public void testStoreShapedImageAnnotation() {

		ImageAnnotation annotation = createSimpleAnnotationInstance();
		List<Point> rectangle = new LinkedList<Point>();
		rectangle.add(new MongoPointImpl(1, 1));
		rectangle.add(new MongoPointImpl(5, 1));
		rectangle.add(new MongoPointImpl(5, 5));
		rectangle.add(new MongoPointImpl(1, 5));

		annotation.setShape(rectangle);

		ImageAnnotation storedAnnotation = (ImageAnnotation) storeAnCheck(
				annotation);
		assertNotNull(storedAnnotation.getText());
		assertEquals(4, storedAnnotation.getShape().size());
	}

	private ImageAnnotation createSimpleAnnotationInstance() {
		ImageAnnotation annotation = new ImageAnnotationImpl();
		annotation.setEuropeanaId(TEST_EUROPEANA_ID);
		annotation.setImageUrl("http://localhost/europeanaImageId.jpg");
		annotation.setText("this is a test image annotation");
		return annotation;
	}

	@Test
	public void testAnnotations() {
		// test all individual methods together ... without deleting the
		// database
		testStoreTag();
		testStoreSimpleImageAnnotation();
		testStoreShapedImageAnnotation();
	}
	
	@Test
	public void testListAnnotations() {
		// test all individual methods together ... without deleting the
		// database
		testStoreTag();
		testStoreSimpleImageAnnotation();
		testStoreShapedImageAnnotation();
		
		List<? extends Annotation> results = annotationService.getAnnotationList(TEST_EUROPEANA_ID);
		assertTrue(results.size() > 0);
		for (Annotation annotation : results) {
			assertEquals(TEST_EUROPEANA_ID, annotation.getEuropeanaId());
			System.out.println("annotationType/annotationNumber: " + annotation.getType() + "/" + annotation.getAnnotationNr());
		}
	}
	
	
	

}

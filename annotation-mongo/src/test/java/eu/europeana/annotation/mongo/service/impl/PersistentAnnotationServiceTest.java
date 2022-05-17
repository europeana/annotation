package eu.europeana.annotation.mongo.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.exception.AnnotationValidationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.ImageAnnotation;
import eu.europeana.annotation.definitions.model.ObjectTag;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.agent.impl.SoftwareAgent;
import eu.europeana.annotation.definitions.model.body.TagBody;
import eu.europeana.annotation.definitions.model.body.impl.PlainTagBody;
import eu.europeana.annotation.definitions.model.body.impl.SemanticTagBody;
import eu.europeana.annotation.definitions.model.body.impl.TextBody;
import eu.europeana.annotation.definitions.model.resource.selector.Rectangle;
import eu.europeana.annotation.definitions.model.resource.selector.Selector;
import eu.europeana.annotation.definitions.model.resource.selector.impl.SvgRectangleSelector;
import eu.europeana.annotation.definitions.model.resource.state.State;
import eu.europeana.annotation.definitions.model.resource.state.impl.BaseState;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.target.impl.ImageTarget;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.BulkOperationException;
import eu.europeana.annotation.mongo.model.PersistentAnnotationImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.annotation.mongo.service.PersistentAnnotationServiceImpl;
import eu.europeana.api.commons.nosql.dao.NosqlDao;

@ExtendWith(SpringExtension.class)
@ContextConfiguration({"/annotation-mongo-test.xml"})
public class PersistentAnnotationServiceTest extends AnnotationTestDataBuilder {

	
	
	public PersistentAnnotationServiceTest() {
		super(null);
	}

	@Resource(name = "annotation_db_annotationService") 
	PersistentAnnotationServiceImpl annotation_db_annotationService;

	@Resource AnnotationConfiguration configuration;

	@Resource(name = "annotation_db_annotationDao")
	NosqlDao<PersistentAnnotation, ObjectId> annotationDao;

	/**
	 * Initialize the testing session
	 * 
	 * @throws IOException
	 */
	@BeforeEach
	public void setup() throws IOException {
//		annotationDao.getCollection().drop();
		setBaseAnnotationUrl(configuration.getAnnotationBaseUrl());	
	}

	/**
	 * Cleaning the testing session's data
	 * 
	 * @throws IOException
	 */
	@AfterEach
	public void tearDown() throws IOException {
		// annotationDao.getCollection().drop();
	}

	@Test
	public void testStoreObjectTag() throws AnnotationMongoException {

		ObjectTag persistentObject = buildObjectTag();

		PlainTagBody body = buildPlainTagBody();
		persistentObject.setBody(body);

		// persistentObject.setLabel("testTag");
		Annotation storedTag = annotation_db_annotationService.store(persistentObject); 
		checkAnnotation(persistentObject, storedTag);

		assertTrue(storedTag instanceof ObjectTag);
		
		annotation_db_annotationService.remove(storedTag.getIdentifier());		
	}
	
	@Test
	public void testStoreObjectTagWithMotivation() throws AnnotationMongoException {

		ObjectTag persistentObject = buildObjectTag();

		// set body
		PlainTagBody body = buildPlainTagBody();
		persistentObject.setBody(body);
		
		persistentObject.setMotivation(MotivationTypes.TAGGING.name());
		
		// persistentObject.setLabel("testTag");
		Annotation storedTag = annotation_db_annotationService.store(persistentObject); 
		
		assertEquals(MotivationTypes.TAGGING.name(), storedTag.getMotivation());
		assertEquals(MotivationTypes.TAGGING, storedTag.getMotivationType());
		
		annotation_db_annotationService.remove(storedTag.getIdentifier());
	}

	// TODO: Test must be reviewed
	//@Test
	public void testStoreSemanticObjectTag() throws AnnotationMongoException {

		ObjectTag persistentObject = buildObjectTag();
		
		//set body
		SemanticTagBody body = buildSemanticTagBody();
		persistentObject.setBody(body);
		
		//persistentObject.setLabel("testTag");
		Annotation storedTag = annotation_db_annotationService.store(persistentObject); 
		checkAnnotation(persistentObject, storedTag);

		assertTrue(storedTag instanceof ObjectTag);
		assertTrue(storedTag.getBody() instanceof SemanticTagBody);
		assertNotNull(((TagBody)storedTag.getBody()).getTagId());
		
		annotation_db_annotationService.remove(storedTag.getIdentifier());
	}

	// TODO: Test must be reviewed
	//@Test
	public void testUniqueSemanticTag() throws AnnotationMongoException {

		ObjectTag firstObject = buildObjectTag();

		SemanticTagBody body = buildSemanticTagBody();
		firstObject.setBody(body);

		// store first annotation
		Annotation firstAnnotation = annotation_db_annotationService.store(firstObject);

		ObjectTag secondObject = buildObjectTag();

		// set body
		SemanticTagBody bodyEn = buildSemanticTagBody();
		// TODO: change to predefined values
		bodyEn.setValue("Vlad The Impaler");
		bodyEn.setLanguage("en");
		secondObject.setBody(bodyEn);
		
		// store second annotation
		Annotation secondAnnotation = annotation_db_annotationService.store(secondObject);

		assertFalse(firstAnnotation.getIdentifier() == secondAnnotation.getIdentifier());
		
		assertFalse(firstAnnotation.getBody().equals(secondAnnotation.getBody()));
		
		//both annotations must use the same semantic tag
		String firstTagId = ((TagBody) firstAnnotation.getBody()).getTagId();
		String secondTagId = ((TagBody) secondAnnotation.getBody()).getTagId();
		
		assertEquals(firstTagId, secondTagId);
		
		annotation_db_annotationService.remove(firstAnnotation.getIdentifier());
		annotation_db_annotationService.remove(secondAnnotation.getIdentifier());
	}

	@Test
	public void testGetObjectList() throws AnnotationMongoException{
	    List<Long> annosIdentifiers = new ArrayList<Long>();
		//*** STORE OBJECTS ****
		ObjectTag firstObject = buildObjectTag();
		annosIdentifiers.add(firstObject.getIdentifier());
		SemanticTagBody body = buildSemanticTagBody();
		firstObject.setBody(body);

		// store first annotation
		annotation_db_annotationService.store(firstObject);

		ObjectTag secondObject = buildObjectTag();
		annosIdentifiers.add(secondObject.getIdentifier());

		// set body
		SemanticTagBody bodyEn = buildSemanticTagBody();
		// TODO: change to predefined values
		bodyEn.setValue("Vlad The Impaler");
		bodyEn.setLanguage("en");
		secondObject.setBody(bodyEn);
		
		// store second annotation
		annotation_db_annotationService.store(secondObject);
		
		
		//** RETRIEVE OBJECTS **
		List<? extends Annotation> results = annotation_db_annotationService.getAnnotationList(annosIdentifiers);
		
		//** CHECK OBJECTS **
		assertNotNull(results);
		assertEquals(2, results.size());
		
		annotation_db_annotationService.remove(firstObject.getIdentifier());
		annotation_db_annotationService.remove(secondObject.getIdentifier());
//		for (Annotation annotation : results) {
//			assertEquals(TEST_DRACULA_ID, annotation.getAnnotationId().getResourceId());
////			assertEquals(TEST_DRACULA_ID, annotation.getTarget().getEuropeanaId());
//		}
	}
	
	// TODO: Test must be reviewed
	//@Test
	public void testDeleteObjectTag() throws AnnotationMongoException{
				//*** STORE OBJECTS ****
				ObjectTag firstObject = buildObjectTag();

				SemanticTagBody body = buildSemanticTagBody();
				firstObject.setBody(body);

				// store first annotation
				ObjectTag storedObject = (ObjectTag) annotation_db_annotationService.store(firstObject);
				String tagId = ((TagBody)storedObject.getBody()).getTagId();
				assertNull(tagId);
				
				//delete object
				annotation_db_annotationService.remove(storedObject.getIdentifier());
				
				//check deletion
				Annotation anno = annotation_db_annotationService.find(storedObject.getIdentifier());
				assertNull(anno);				
	}
	
	@Test
	public void testStoreSimpleImageAnnotation() throws AnnotationMongoException {
		
		 ImageAnnotation annotation = createSimpleAnnotationInstance();
		 
		 ImageAnnotation storedAnnotation = (ImageAnnotation) 
				 annotation_db_annotationService.store(annotation);
		 
		 checkAnnotation(annotation, storedAnnotation);
		 
		 assertNotNull(storedAnnotation.getImageUrl());
		 assertNotNull(storedAnnotation.getTarget());
		 assertNotNull(storedAnnotation.getTarget().getSelector());
		 assertNotNull(storedAnnotation.getTarget().getState());
		 
		 annotation_db_annotationService.remove(storedAnnotation.getIdentifier());		 
		 
	}

	@SuppressWarnings("deprecation")
	private Target buildTarget() {
		Target target = new ImageTarget();
		target.setMediaType("image");
		target.setContentType("image/jpeg");
		target.setHttpUri("http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE");
//		target.setEuropeanaId(TEST_DRACULA_ID);
		
		Rectangle selector = new SvgRectangleSelector();
		selector.setX(5);
		selector.setY(5);
		selector.setHeight(100);
		selector.setWidth(200);
		
		target.setSelector((Selector)selector);
		
		State state = new BaseState();
		state.setFormat("image/jpeg");
		state.setVersionUri("http://bilddatenbank.khm.at/images/350/GG_8285.jpg");
		state.setAuthenticationRequired(false);
		target.setState(state);
		
		return target;
	}

	protected PersistentAnnotation createPersistentAnnotationInstance() {
		
		PersistentAnnotation persistentObject = new PersistentAnnotationImpl();
		persistentObject.setInternalType(AnnotationTypes.OBJECT_COMMENT.name());
		
		// set target
		Target target = buildTarget();
		persistentObject.setTarget(target);
			
		//set Body
		String comment = "Same hair style as in Dracula Untold: https://www.youtube.com/watch?v=_2aWqecTTuE";
		TextBody body = buildTextBody(comment, "en");
		persistentObject.setBody(body);
				
		// set AnnotatedBy
		Agent creator = new SoftwareAgent();
		creator.setName("unit test");
		creator.setHomepage("http://www.pro.europeana.eu/web/europeana-creative");
		persistentObject.setCreator(creator);
		
		//set serializeb by
		persistentObject.setGenerator(creator);
		
		//motivation
		persistentObject.setMotivation(MotivationTypes.COMMENTING.name());
		
		//persistentObject.setType(type)
		persistentObject.setIdentifier(annotation_db_annotationService.generateAnnotationIdentifier());
		return persistentObject;
	}
	
	 
	@Test
	public void testCreateAndUpdatePersistanceAnnotation() throws AnnotationMongoException {
		
		Annotation initialPersistentAnnotation = createPersistentAnnotationInstance();
		Annotation storedAnnotation = annotation_db_annotationService.store(initialPersistentAnnotation);
		Logger.getLogger(getClass().getName()).info(
				"testCreatePersistentAnnotation initialPersistentAnnotation: " + initialPersistentAnnotation.toString());
		Annotation updatedAnnotation = annotation_db_annotationService.updateIndexingTime(storedAnnotation, initialPersistentAnnotation.getLastUpdate());
		PersistentAnnotation persistentAnnotation = (PersistentAnnotation) updatedAnnotation;
	    
		Logger.getLogger(getClass().getName()).info(
				"testCreatePersistentAnnotation persistent annotation after update: " + persistentAnnotation.toString());

		assertNotNull(persistentAnnotation.getLastIndexed());
		annotation_db_annotationService.remove(persistentAnnotation.getIdentifier());
	}
	
	
	// @Test(expected=AnnotationValidationException.class)
	// public void testStoreValidationError(){
	// ImageAnnotation annotation = createSimpleAnnotationInstance();
	// annotation.setEuropeanaId(null);
	// //expect exception
	// annotation_db_annotationService.store(annotation);
	//
	// }
	//
	// @Test(expected=AnnotationValidationException.class)
	// public void testStoreValidationErrorObjectId(){
	// ImageAnnotation annotation = createSimpleAnnotationInstance();
	// Annotation storedAnnotation = storeAnCheck(annotation);
	// //expect exception
	// annotation_db_annotationService.store(storedAnnotation);
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
	// annotation_db_annotationService.getAnnotationList(TEST_EUROPEANA_ID);
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
	// Annotation storedAnnotation = annotation_db_annotationService.store(anno);
	// assertNotNull(storedAnnotation.getAnnotationId().getAnnotationNr());
	//
	// //retrieve annotation
	// Annotation retrievedAnnotation =
	// annotation_db_annotationService.getAnnotation(storedAnnotation.getResourceId(),
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
	

	protected List<Annotation> getTestAnnotationList(Integer numAnnotations) {
		List<Long> annoIdSequence = annotation_db_annotationService.generateAnnotationIdentifierSequence(numAnnotations);
		List<Annotation> annoList = new ArrayList<Annotation>(numAnnotations);
		Annotation anno;
		for(int i = 0; i < numAnnotations; i++) {
			anno = createPersistentAnnotationInstance();
			anno.setIdentifier(annoIdSequence.get(i));
			annoList.add(i, anno);
		}
		return annoList;
	}
	
	@Test
	public void testRollbackUpdates() throws AnnotationMongoException, AnnotationValidationException, BulkOperationException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, IOException, InterruptedException {
		
		int annotationListSize = 5;
			
		List<Annotation> annoList = getTestAnnotationList(annotationListSize);
		annotation_db_annotationService.create(annoList);
		assertNotNull(annoList);
		
		List<Long> annoIdentifiers = annoList.stream().map(Annotation::getIdentifier).collect(Collectors.toList());

		@SuppressWarnings("unchecked")
		List<PersistentAnnotation> annoListFromDb = (List<PersistentAnnotation>)annotation_db_annotationService.getAnnotationList(annoIdentifiers);
		assertNotNull(annoListFromDb);
		assertEquals(annoList.size(), annoListFromDb.size());
			
		// change object id of 4th item (at index position 3)
		PersistentAnnotation annoWithChangedObjId = (PersistentAnnotation)annoListFromDb.get(annoListFromDb.size()-2);
		// annotation[0]
		// annotation[1]
		// annotation[2]
		// annotation[3] - object id changed
		// annotation[4]
		ObjectId changedObjId = new ObjectId();
		annoWithChangedObjId.setId(changedObjId);
			
		// change body values 
		for(int i = 0; i < annoListFromDb.size(); i++) {
			annoListFromDb.get(i).getBody().setValue("updated "+i);
		}
			
	    Assertions.assertThrows(BulkOperationException.class, () -> {
	      // expected exception: BulkOperationException
	      annotation_db_annotationService.update(annoListFromDb);
	    });		
	    
        for(int i = 0; i < annoListFromDb.size(); i++) {
          annotation_db_annotationService.remove(annoListFromDb.get(i).getIdentifier());
        }
	}
	
	//@Test
	public void createBackupCopy() throws AnnotationMongoException, AnnotationValidationException, BulkOperationException, IOException, InterruptedException {
		int annotationListSize = 5;
		List<Annotation> annoList = getTestAnnotationList(annotationListSize);
		assertNotNull(annoList);
		annotation_db_annotationService.create(annoList);		
		annotation_db_annotationService.createBackupCopy(annoList);
	}
	
}

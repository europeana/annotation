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
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.ImageAnnotation;
import eu.europeana.annotation.definitions.model.ObjectTag;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.agent.impl.SoftwareAgent;
import eu.europeana.annotation.definitions.model.body.TagBody;
import eu.europeana.annotation.definitions.model.body.impl.PlainTagBody;
import eu.europeana.annotation.definitions.model.body.impl.SemanticTagBody;
import eu.europeana.annotation.definitions.model.body.impl.TextBody;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
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
import eu.europeana.annotation.mongo.model.MongoAnnotationId;
import eu.europeana.annotation.mongo.model.PersistentAnnotationImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.annotation.mongo.model.internal.PersistentTag;
import eu.europeana.annotation.mongo.service.PersistentAnnotationService;
import eu.europeana.annotation.mongo.service.PersistentTagService;
import eu.europeana.annotation.utils.AnnotationListUtils;
import eu.europeana.api.commons.nosql.dao.NosqlDao;

@ExtendWith(SpringExtension.class)
@ContextConfiguration({ "/annotation-mongo-context.xml",
		"/annotation-mongo-test.xml" })
public class PersistentAnnotationServiceTest extends AnnotationTestDataBuilder {

	
	
	public PersistentAnnotationServiceTest() {
		super(null);
	}

	@Resource PersistentAnnotationService annotationService;

	@Resource PersistentTagService tagService;

	@Resource AnnotationConfiguration configuration;

	@Resource(name = "annotation_db_annotationDao")
	NosqlDao<PersistentAnnotation, AnnotationId> annotationDao;

	/**
	 * Initialize the testing session
	 * 
	 * @throws IOException
	 */
	@BeforeEach
	public void setup() throws IOException {
		annotationDao.getCollection().drop();
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
	public void testStoreObjectTag() {

		ObjectTag persistentObject = buildObjectTag();

		PlainTagBody body = buildPlainTagBody();
		persistentObject.setBody(body);

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
		persistentObject.setBody(body);
		
		persistentObject.setMotivation(MotivationTypes.TAGGING.name());
		
		// persistentObject.setLabel("testTag");
		Annotation storedTag = annotationService.store(persistentObject); 
		
		assertEquals(MotivationTypes.TAGGING.name(), storedTag.getMotivation());
		assertEquals(MotivationTypes.TAGGING, storedTag.getMotivationType());
	}

	// TODO: Test must be reviewed
	//@Test
	public void testStoreSemanticObjectTag() {

		ObjectTag persistentObject = buildObjectTag();
		
		//set body
		SemanticTagBody body = buildSemanticTagBody();
		persistentObject.setBody(body);
		
		//persistentObject.setLabel("testTag");
		Annotation storedTag = annotationService.store(persistentObject); 
		checkAnnotation(persistentObject, storedTag);

		assertTrue(storedTag instanceof ObjectTag);
		assertTrue(storedTag.getBody() instanceof SemanticTagBody);
		assertNotNull(((TagBody)storedTag.getBody()).getTagId());
	}

	// TODO: Test must be reviewed
	//@Test
	public void testUniqueSemanticTag() {

		ObjectTag firstObject = buildObjectTag();

		SemanticTagBody body = buildSemanticTagBody();
		firstObject.setBody(body);

		// store first annotation
		Annotation firstAnnotation = annotationService.store(firstObject);

		ObjectTag secondObject = buildObjectTag();

		// set body
		SemanticTagBody bodyEn = buildSemanticTagBody();
		// TODO: change to predefined values
		bodyEn.setValue("Vlad The Impaler");
		bodyEn.setLanguage("en");
		secondObject.setBody(bodyEn);
		
		// store second annotation
		Annotation secondAnnotation = annotationService.store(secondObject);

		assertFalse(firstAnnotation.getAnnotationId().equals(secondAnnotation.getAnnotationId()));
		assertFalse(firstAnnotation.getAnnotationId().toString().equals(secondAnnotation.getAnnotationId().toString()));
		
		assertFalse(firstAnnotation.getBody().equals(secondAnnotation.getBody()));
		
		//both annotations must use the same semantic tag
		String firstTagId = ((TagBody) firstAnnotation.getBody()).getTagId();
		String secondTagId = ((TagBody) secondAnnotation.getBody()).getTagId();
		
		assertEquals(firstTagId, secondTagId);

	}

	@Test
	public void testGetObjectList(){
		//*** STORE OBJECTS ****
		ObjectTag firstObject = buildObjectTag();

		SemanticTagBody body = buildSemanticTagBody();
		firstObject.setBody(body);

		// store first annotation
		annotationService.store(firstObject);

		ObjectTag secondObject = buildObjectTag();

		// set body
		SemanticTagBody bodyEn = buildSemanticTagBody();
		// TODO: change to predefined values
		bodyEn.setValue("Vlad The Impaler");
		bodyEn.setLanguage("en");
		secondObject.setBody(bodyEn);
		
		// store second annotation
		annotationService.store(secondObject);
		
		//** RETRIEVE OBJECTS **
		List<? extends Annotation> results = annotationService.getAnnotationList(TEST_DRACULA_ID);
		
		//** CHECK OBJECTS **
		assertNotNull(results);
		assertEquals(2, results.size());
		
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
				ObjectTag storedObject = (ObjectTag) annotationService.store(firstObject);
				String tagId = ((TagBody)storedObject.getBody()).getTagId();
				
				//delete object
				annotationService.remove(storedObject.getAnnotationId());
				
				//check deletion
				Annotation anno = annotationService.find(storedObject.getAnnotationId());
				assertNull(anno);
				
				//check tag existence
				PersistentTag tag = tagService.findByID(tagId);
				assertNotNull(tag);
				
		
				
	}
	
	@Test
	public void testStoreSimpleImageAnnotation() {
		
		 ImageAnnotation annotation = createSimpleAnnotationInstance();
		 
		 ImageAnnotation storedAnnotation = (ImageAnnotation) 
				 annotationService.store(annotation);
		 
		 checkAnnotation(annotation, storedAnnotation);
		 
		 assertNotNull(storedAnnotation.getImageUrl());
		 assertNotNull(storedAnnotation.getTarget());
		 assertNotNull(storedAnnotation.getTarget().getSelector());
		 assertNotNull(storedAnnotation.getTarget().getState());
		 
		 
	}

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
		persistentObject.setAnnotationId(new BaseAnnotationId(configuration.getAnnotationBaseUrl(), "webanno", null));
		return persistentObject;
	}
	
	 
	@Test
	public void testCreateAndUpdatePersistanceAnnotation() throws AnnotationMongoException {
		
		Annotation initialPersistentAnnotation = createPersistentAnnotationInstance();
		Annotation storedAnnotation = annotationService.store(initialPersistentAnnotation);
		Logger.getLogger(getClass().getName()).info(
				"testCreatePersistentAnnotation initialPersistentAnnotation: " + initialPersistentAnnotation.toString());
		Annotation updatedAnnotation = annotationService.updateIndexingTime(storedAnnotation.getAnnotationId(), initialPersistentAnnotation.getLastUpdate());
		PersistentAnnotation persistentAnnotation = (PersistentAnnotation) updatedAnnotation;
	    
		Logger.getLogger(getClass().getName()).info(
				"testCreatePersistentAnnotation persistent annotation after update: " + persistentAnnotation.toString());

		assertNotNull(persistentAnnotation.getLastIndexed());
		annotationService.remove(persistentAnnotation.getAnnotationId());
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
	

	protected List<Annotation> getTestAnnotationList(Integer numAnnotations) {
		List<AnnotationId> annoIdSequence = annotationService.generateAnnotationIdSequence("rollbacktest", numAnnotations);
		List<Annotation> annoList = new ArrayList<Annotation>(numAnnotations);
		AnnotationId newAnnoId;
		Annotation anno;
		AnnotationId genAnnoId;
		MongoAnnotationId mongoAnnoId;
		for(int i = 0; i < numAnnotations; i++) {
			anno = createPersistentAnnotationInstance();
			genAnnoId = annoIdSequence.get(i);
			newAnnoId = new BaseAnnotationId("http://localhost:8080/annotation", "rollbacktest", genAnnoId.getIdentifier());
			mongoAnnoId = new MongoAnnotationId(newAnnoId);			
			anno.setAnnotationId(newAnnoId);
			annoList.add(i, anno);
		}
		return annoList;
	}
	
	@Test
	public void testRollbackInserts() throws AnnotationMongoException {
		
		int annotationListSize = 5;		
		
		List<Annotation> annoList = getTestAnnotationList(annotationListSize);
		
		// failed index: 3
		//
		// annotation[0]: random 					(inserted)
		// annotation[1]: random  					(inserted)
		// annotation[2]: id 999999999  			(inserted)
		// annotation[3]: id 999999999 				(not inserted, duplicate error!)
		// annotation[4]: random 					(not inserted)

		annoList.get(annotationListSize-3).getAnnotationId().setIdentifier("999999999");
		annoList.get(annotationListSize-3).getAnnotationId().setHttpUrl("http://localhost:8080/annotation/999999999");
		
		annoList.get(annotationListSize-2).getAnnotationId().setIdentifier("999999999");		
		annoList.get(annotationListSize-2).getAnnotationId().setHttpUrl("http://localhost:8080/annotation/999999999");
		
		assertNotNull(annoList);
		assertEquals(annotationListSize, annoList.size());
		
		try {
			annotationService.create(annoList);
        } catch (BulkOperationException e) {
        	assertEquals(1, e.getFailedIndices().size());
        	Integer failedIndex = e.getFailedIndices().get(0);
        	assertTrue(failedIndex == 3);
        	assertEquals("Bulk write operation failed", e.getMessage());
        }
		
	}
	

	@Test
	public void testRollbackUpdates() throws AnnotationMongoException, AnnotationValidationException, BulkOperationException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
	    Assertions.assertThrows(BulkOperationException.class, () -> {
			int annotationListSize = 5;
			
			List<Annotation> annoList = getTestAnnotationList(annotationListSize);
			annotationService.create(annoList);
			assertNotNull(annoList);
			
			List<String> httpUrls = AnnotationListUtils.getHttpUrls(annoList);
			@SuppressWarnings("unchecked")
			List<PersistentAnnotation> annoListFromDb = (List<PersistentAnnotation>)annotationService.getAnnotationList(httpUrls);
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
			
			// expected exception: BulkOperationException
			annotationService.update(annoListFromDb);
	    });				
	}
	
	@Test
	public void createBackupCopy() throws AnnotationMongoException, AnnotationValidationException, BulkOperationException {
		int annotationListSize = 5;
		List<Annotation> annoList = getTestAnnotationList(annotationListSize);
		assertNotNull(annoList);
		annotationService.create(annoList);
		
		annotationService.createBackupCopy(annoList);
	}
	
}

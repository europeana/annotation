package eu.europeana.annotation.mongo.dao;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/annotation-mongo-context.xml", "/annotation-mongo-test.xml"})
public class PersistentAnnotationDaoTest {

	@Resource(name = "annotation_db_annotationDao")
	PersistentAnnotationDao<PersistentAnnotation, AnnotationId> annotationDao;
	
	/**
	 * Initialize the testing session
	 * 
	 * @throws IOException
	 */
	@Before
	public void setup() throws IOException {
		//annotationDao.getDatastore().getCollection(GeneratedAnnotationIdImpl.class).drop();
	}

	/**
	 * Cleaning the testing session's data
	 * 
	 * @throws IOException
	 */
	@After
	public void tearDown() throws IOException {
		// annotationDao.getDatastore().getCollection(GeneratedAnnotationIdImpl.class).drop();
	}

	@Test
	public void testGenerateAnnotationId(){
		String testEuropeanaId = "/test_europeanaId/generate_annotationId";
		AnnotationId id1 = annotationDao.generateNextAnnotationId(testEuropeanaId);
		assertTrue(id1.getAnnotationNr() > 0);
		
		AnnotationId id2 = annotationDao.generateNextAnnotationId(testEuropeanaId);
		assertTrue(id1.getAnnotationNr() +1 == id2.getAnnotationNr());
		
	}

}

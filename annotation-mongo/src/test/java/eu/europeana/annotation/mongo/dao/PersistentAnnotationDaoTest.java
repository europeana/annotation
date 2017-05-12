package eu.europeana.annotation.mongo.dao;

import static org.junit.Assert.*;

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
		String testProvider = "test_provider";
		AnnotationId id1 = annotationDao.generateNextAnnotationId(testProvider);
		assertTrue(Long.parseLong(id1.getIdentifier()) > 0);
		
		AnnotationId id2 = annotationDao.generateNextAnnotationId(testProvider);
		assertTrue(Long.parseLong(id1.getIdentifier()) +1 == Long.parseLong(id2.getIdentifier()));
		
	}

}

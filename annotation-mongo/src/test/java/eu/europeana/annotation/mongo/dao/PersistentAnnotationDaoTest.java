package eu.europeana.annotation.mongo.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.util.List;
import javax.annotation.Resource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration({"/annotation-mongo-test.xml"})
public class PersistentAnnotationDaoTest {

	@Resource(name = "annotation_db_annotationDao")
	PersistentAnnotationDao annotationDao;
	
	public final Integer SEQUENCE_LENGTH = 10;
	
	/**
	 * Initialize the testing session
	 * 
	 * @throws IOException
	 */
	@BeforeEach
	public void setup() throws IOException {
		//annotationDao.getDatastore().getCollection(GeneratedAnnotationIdImpl.class).drop();
	}

	/**
	 * Cleaning the testing session's data
	 * 
	 * @throws IOException
	 */
	@AfterEach
	public void tearDown() throws IOException {
		// annotationDao.getDatastore().getCollection(GeneratedAnnotationIdImpl.class).drop();
	}

	@Test
	public void testGenerateAnnotationId(){
		Long id1 = annotationDao.generateNextAnnotationIdentifier();
		assertTrue(id1 > 0);
		Long id2 = annotationDao.generateNextAnnotationIdentifier();
		assertTrue(id1+1 == id2);	
	}
	

	@Test
	public void testGenerateAnnotationIds(){		
		List<Long> ids = annotationDao.generateNextAnnotationIdentifiers(SEQUENCE_LENGTH);
		assertEquals(SEQUENCE_LENGTH.intValue(), ids.size());
		
		Long lastAnnoInSequence = ids.get(SEQUENCE_LENGTH-1);
		Long persistedAnnoNumber = annotationDao.getLastAnnotationNr();
		
		assertTrue(persistedAnnoNumber > 0);
		assertTrue(lastAnnoInSequence > 0);
		assertEquals(persistedAnnoNumber, lastAnnoInSequence);		
	}

}

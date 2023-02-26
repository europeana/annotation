package eu.europeana.annotation.mongo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.util.Date;
import javax.annotation.Resource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.model.internal.PersistentApiWriteLock;
import eu.europeana.api.commons.definitions.exception.ApiWriteLockException;
import eu.europeana.api.commons.nosql.entity.ApiWriteLock;
import eu.europeana.api.commons.nosql.service.ApiWriteLockService;


@ExtendWith(SpringExtension.class)
@ContextConfiguration({"/annotation-mongo-test.xml"})
@Disabled("needs configuration file, to be moved to integration tests")
public class PersistentIndexingJobServiceTest {
	
    @Resource(name = "annotation_db_apilockService")
	private ApiWriteLockService apiWriteLockService;


	public void setApiWriteLockService(ApiWriteLockService apiWriteLockService) {
		this.apiWriteLockService = apiWriteLockService;
	}

	private ApiWriteLockService getApiWriteLockService() {
		return apiWriteLockService;	
	}
	
	/**
	 * Initialize the testing session
	 * 
	 * @throws IOException
	 */
	@BeforeEach
	public void setup() throws IOException {
	}

	/**
	 * Cleaning the testing session's data
	 * 
	 * @throws IOException
	 */
	@AfterEach
	public void tearDown() throws IOException {
	}
	
	@Test
	public void getJobById() throws AnnotationMongoException, ApiWriteLockException{
		
		// create job
		ApiWriteLock apiWriteLock = apiWriteLockService.lock("testaction");
		
		// id of the job
		String id = apiWriteLock.getId().toString();
		
		// get indexing job just created by its id
		ApiWriteLock newJobRetrieved = getApiWriteLockService().getLockById(id);
		
		// check if the retrieved object exists and if the id is correct
		assertNotNull(newJobRetrieved);
		assertEquals(id, newJobRetrieved.getId().toString());
		
		// remove test job object
		//TODO: might want to really delete the lock from the database 
		getApiWriteLockService().unlock(apiWriteLock);  
		
	}
	
	

	@Test
	public void jobLockedAndInStatusRunning() throws AnnotationMongoException, ApiWriteLockException{
		
		// lock job indicating the action 
		ApiWriteLock newJob = getApiWriteLockService().lock("testaction");
		assertNotNull(newJob);

		// get last indexing job and check if start date is correct and end date does not exist
		ApiWriteLock newRunningJob = getApiWriteLockService().getLastActiveLock("testaction");
		assertTrue(newRunningJob.getStarted() instanceof Date);
		assertNull(newRunningJob.getEnded());
		
		// unlock the job (which sets the end date) and verify that the end date is set correctly
		getApiWriteLockService().unlock(newRunningJob);
		assertTrue(newRunningJob.getEnded() instanceof Date);	
		
		// remove test job object
		getApiWriteLockService().unlock(newJob); 
		
	}
	
}

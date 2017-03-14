package eu.europeana.annotation.mongo.service;

import java.io.IOException;
import java.util.Date;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.IndexingJobServiceException;
import eu.europeana.annotation.mongo.model.internal.PersistentIndexingJob;
import eu.europeana.annotation.mongo.service.PersistentIndexingJobService;
import eu.europeana.annotation.mongo.service.PersistentIndexingJobServiceImpl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/annotation-mongo-context.xml"
	,"/annotation-mongo-test.xml" 
	})
public class PersistentIndexingJobServiceTest {
	
	@Resource(name = "annotation_db_jobService")
	private PersistentIndexingJobService indexingJobService;


	public void setIndexingJobService(PersistentIndexingJobService indexingJobService) {
		this.indexingJobService = indexingJobService;
	}

	private PersistentIndexingJobServiceImpl getIndexingJobService() {
		return (PersistentIndexingJobServiceImpl) indexingJobService;	
	}
	
	/**
	 * Initialize the testing session
	 * 
	 * @throws IOException
	 */
	@Before
	public void setup() throws IOException {
	}

	/**
	 * Cleaning the testing session's data
	 * 
	 * @throws IOException
	 */
	@After
	public void tearDown() throws IOException {
	}
	
	@Test
	public void getJobById() throws AnnotationMongoException, IndexingJobServiceException{
		
		// create job
		PersistentIndexingJob newJob = indexingJobService.lock("testaction");
		
		// id of the job
		String id = newJob.getId().toString();
		
		// get indexing job just created by its id
		PersistentIndexingJob newJobRetrieved = getIndexingJobService().getJobById(id);
		
		// check if the retrieved object exists and if the id is correct
		assertNotNull(newJobRetrieved);
		assertEquals(id, newJobRetrieved.getId().toString());
		
		// remove test job object
		getIndexingJobService().deleteJob(newJob); 
		
	}
	
	

	@Test
	public void jobLockedAndInStatusRunning() throws AnnotationMongoException, IndexingJobServiceException{
		
		// lock job indicating the action 
		PersistentIndexingJob newJob = indexingJobService.lock("testaction");
		assertNotNull(newJob);

		// get last indexing job and check if start date is correct and end date does not exist
		PersistentIndexingJob newRunningJob = getIndexingJobService().getLastRunningJob();
		assertTrue(newRunningJob.getStarted() instanceof Date);
		assertNull(newRunningJob.getEnded());
		
		// unlock the job (which sets the end date) and verify that the end date is set correctly
		getIndexingJobService().unlock(newRunningJob);
		assertTrue(newRunningJob.getEnded() instanceof Date);	
		
		// remove test job object
		getIndexingJobService().deleteJob(newJob); 
		
	}
	
}

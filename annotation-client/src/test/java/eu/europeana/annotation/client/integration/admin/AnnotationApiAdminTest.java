package eu.europeana.annotation.client.integration.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.stanbol.commons.exception.JsonParseException;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.client.AnnotationSearchApiImpl;
import eu.europeana.annotation.client.admin.WebAnnotationAdminApiImpl;
import eu.europeana.annotation.client.integration.webanno.BaseWebAnnotationProtocolTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.search.Query;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;

/**
 * Annotation API Admin Test class
 * 
 * @author Sven Schlarb
 */
public class AnnotationApiAdminTest extends BaseWebAnnotationProtocolTest {

	public static final String TAG_INDEXING = "/tag/tag_indexing.json";
	final String VALUE_INDEXING_TESTSET = "generator_uri: \"http://test.europeana.org/519b30bc-6980-47b0-a0ad-50eb5933ae02\"";
	
	// settings for blockConcurrentBatchIndexingJobs test
	public static final String TAG_INDEXING_LOCK = "/tag/tag_indexing_lock.json";
	public static final int ANNOTATION_TESTSET_LOCK_SIZE = 20;
	public static final int CONCURRENT_REQUESTS = 2;


	/**
	 * Test creating and deleting an annotation.
	 * 
	 * @throws JsonParseException
	 * @throws IOException
	 */
	@Test
	public void createAndDeleteAnnotation() throws JsonParseException, IOException {

		// create
		Annotation annotation = this.createTestAnnotation();

		// read
		assertNotNull(annotation);

		Integer numericId = getNumericAnnotationId(annotation);
		assertTrue(getNumericAnnotationId(annotation) > 0);
		log.debug("Created annotation: " + numericId);

		// delete
		this.deleteAnnotation(numericId);
	}

	/**
	 * Test the "index outdated" admin function. First, one annotation is
	 * created without indexing it. Then the index outdated admin function is
	 * executed. And finally it is checked if the annotation was indexed
	 * correctly by verifying if it is returned in the query result.
	 * 
	 * @throws JsonParseException
	 * @throws IOException
	 */
	@Test
	public void indexOneOutdated() throws JsonParseException, IOException {
		assertIndexOutdated(1);
	}

	/**
	 * Test the "index outdated" admin function. First, two annotations are
	 * created without indexing them. Then the index outdated admin function is
	 * executed. And finally it is checked if the annotations were indexed
	 * correctly by verifying if they are returned in the query result.
	 * 
	 * @throws JsonParseException
	 * @throws IOException
	 */
	@Test
	public void indexTwoOutdated() throws JsonParseException, IOException {
		assertIndexOutdated(2);
	}

	/**
	 * Helper function to support testing the "index outdated" admin function
	 * using a given number of annotations. First, the annotations are created
	 * without indexing them. Then the index outdated admin function is
	 * executed. And finally it is checked if the annotations were indexed
	 * correctly by verifying if they are returned in the query result.
	 * 
	 * @throws JsonParseException
	 * @throws IOException
	 */
	private void assertIndexOutdated(int numAnnotations) throws JsonParseException, IOException {

			List<Annotation> annotations = new ArrayList<Annotation>();
			
		try {
			// create test annotations without indexing it
			for (int i = 0; i < numAnnotations; i++)
				annotations.add(this.createTestAnnotation(TAG_INDEXING, false));
			assertNotNull(annotations);
			assertEquals(numAnnotations, annotations.size());

			AnnotationSearchApiImpl annSearchApi = new AnnotationSearchApiImpl();

			// search for the annotation just created using the generator_id
			// value defined by VALUE_INDEXING_TESTSET
			AnnotationPage annPgBefore = annSearchApi.searchAnnotations(VALUE_INDEXING_TESTSET, null, null, null, null,
					SearchProfiles.MINIMAL);
			assertNotNull("AnnotationPage must not be null", annPgBefore);
			// annotations were not indexed, therefore it must not show up in
			// the search result
			// assertEquals(0, annPgBefore.getTotalInCollection());
			// but failing tests might leave data behind
			long outdatedAnnotationsBefore = annPgBefore.getTotalInCollection();
			if (outdatedAnnotationsBefore > 0)
				log.warn("expected 0 outdated annotations before test, but found: " + outdatedAnnotationsBefore);

			// index outdated annotations including the test annotation which
			// was intentionally not indexed
			WebAnnotationAdminApiImpl adminApi = new WebAnnotationAdminApiImpl();
			adminApi.reindexOutdated();

			// search for the annotation just created using the generator_id
			// value defined by VALUE_INDEXING_TESTSET
			AnnotationPage annPgAfter = annSearchApi.searchAnnotations(VALUE_INDEXING_TESTSET,
					Integer.toString(Query.DEFAULT_PAGE), Integer.toString(Query.DEFAULT_PAGE_SIZE), null, null,
					SearchProfiles.MINIMAL);
			assertNotNull("AnnotationPage must not be null", annPgAfter);
			// Now that the annotation was indexed, it must show up in the
			// search result
			assertEquals(outdatedAnnotationsBefore + numAnnotations, annPgAfter.getTotalInCollection());
		} finally {
			// delete test annotations
			deleteAnnotations(annotations);
//TODO: chech and remove
/*		
      
      for (Annotation anno : annotations) {
				Integer numericId = getNumericAnnotationId(anno);
				assertTrue(getNumericAnnotationId(anno) > 0);
				log.debug("Created annotation: " + numericId);
				this.deleteAnnotation(numericId);
			}
		// create test annotations without indexing it
		List<Annotation> annotations = createAnnotationsTestSet(numAnnotations, TAG_INDEXING, false);
		
		assertNotNull(annotations);
		assertEquals(numAnnotations, annotations.size());
		
		AnnotationSearchApiImpl annSearchApi = new AnnotationSearchApiImpl();
		
		// search for the annotation just created using the generator_id value defined by VALUE_INDEXING_TESTSET
		AnnotationPage annPgBefore = annSearchApi.searchAnnotations(VALUE_INDEXING_TESTSET,
				Integer.toString(Query.DEFAULT_PAGE), Integer.toString(Query.DEFAULT_PAGE_SIZE), null, null, SearchProfiles.MINIMAL);
		
		
		
		assertNotNull("AnnotationPage must not be null", annPgBefore);
		// annotations were not indexed, therefore it must not show up in the search result
		assertEquals(0, annPgBefore.getTotalInCollection());
		
		// index outdated annotations including the test annotation which was intentionally not indexed
		WebAnnotationAdminApiImpl adminApi = new WebAnnotationAdminApiImpl();
		adminApi.reindexOutdated();
		
		// search for the annotation just created using the generator_id value defined by VALUE_INDEXING_TESTSET
		AnnotationPage annPgAfter = annSearchApi.searchAnnotations(VALUE_INDEXING_TESTSET,
				Integer.toString(Query.DEFAULT_PAGE), Integer.toString(Query.DEFAULT_PAGE_SIZE), null, null, SearchProfiles.MINIMAL);
		assertNotNull("AnnotationPage must not be null", annPgAfter);
		// Now that the annotation was indexed, it must show up in the search result
		assertEquals(numAnnotations, annPgAfter.getTotalInCollection());
		
		deleteAnnotations(annotations);
    */
	}
  }
	

	/**
	 * Block Concurrent Batch Indexing Jobs
	 * 
	 * @throws JsonParseException
	 * @throws IOException
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	@Test
	public void blockConcurrentBatchIndexingJobs() throws JsonParseException, IOException, InterruptedException, ExecutionException {
		
		// create test annotations without indexing it
		List<Annotation> annotations = createAnnotationsTestSet(ANNOTATION_TESTSET_LOCK_SIZE, TAG_INDEXING_LOCK, false);
		assertNotNull(annotations);
		assertEquals(ANNOTATION_TESTSET_LOCK_SIZE, annotations.size());		
		
			
		ApiInvoker call1 = new ApiInvoker();
		Thread th1 = new Thread(call1);
		
		ApiInvoker call2 = new ApiInvoker();
		Thread th2 = new Thread(call2);
		
		th1.start();
		
		//wait 200 ms
		{
		long now = System.currentTimeMillis();
		
		System.out.println(now);
		while(System.currentTimeMillis() < (now + 200));
		System.out.println(System.currentTimeMillis());
		}
		
		th2.start();
		
		//wait for execution of started threads
		while(th1.isAlive() || th2.isAlive());
		
		assertTrue(call1.getResult().getStatusCode() != call2.getResult().getStatusCode());
		assertTrue(call1.getResult().getStatusCode() == HttpStatus.OK || call2.getResult().getStatusCode() == HttpStatus.OK);
		System.out.println(call1.getResult());
		System.out.println(call2.getResult());
		
		// delete annotation test set
		deleteAnnotations(annotations);
	}
	
	
	class ApiInvoker implements Runnable {

		ResponseEntity<String> result;
		
		@Override
		public void run() {
			WebAnnotationAdminApiImpl adminApi = new WebAnnotationAdminApiImpl();
			result = adminApi.reindexOutdated();
		}
		
		public ResponseEntity<String> getResult() {
			return result;
		}
	}
	
	private void deleteAnnotations(List<Annotation> annotations) {
		// delete test annotations
		for(Annotation anno: annotations)  {
			Integer numericId = getNumericAnnotationId(anno);
			assertTrue(getNumericAnnotationId(anno) > 0);
			log.debug("Created annotation: " + numericId);
			this.deleteAnnotation(numericId);
		}
	}
	
	private List<Annotation> createAnnotationsTestSet(int numAnnotations, String testAnnotationTag, boolean index) throws JsonParseException, IOException {
		List<Annotation> annotations = new ArrayList<Annotation>();
		for(int i = 0; i < numAnnotations; i++) 
			annotations.add(this.createTestAnnotation(testAnnotationTag, index));
		return annotations;
	}
	
}

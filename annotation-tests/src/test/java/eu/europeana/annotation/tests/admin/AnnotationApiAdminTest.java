package eu.europeana.annotation.tests.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.search.Query;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
import eu.europeana.annotation.tests.AbstractIntegrationTest;

/**
 * Annotation API Admin Test class
 * 
 * @author Sven Schlarb
 */
public class AnnotationApiAdminTest extends AbstractIntegrationTest {

	public static final String TAG_INDEXING = "/tag/tag_indexing.json";
	
	// settings for blockConcurrentBatchIndexingJobs test
	public static final String TAG_INDEXING_LOCK = "/tag/tag_indexing_lock.json";
	public static final int ANNOTATION_TESTSET_LOCK_SIZE = 20;
	public static final int CONCURRENT_REQUESTS = 2;


	/**
	 * Test creating and deleting an annotation.
	 * @throws Exception 
	 */
	@Test
	public void createAndDeleteAnnotation() throws Exception {
	  
		// create
		Annotation annotation = createTestAnnotation(TAG_STANDARD, true, null);
		createdAnnotations.add(annotation.getIdentifier());
		// read
		assertNotNull(annotation);
		assertNotNull(annotation.getIdentifier());
		log.debug("Created annotation: " + annotation.getIdentifier());
	}

	/**
	 * Test the "index outdated" admin function. First, one annotation is
	 * created without indexing it. Then the index outdated admin function is
	 * executed. And finally it is checked if the annotation was indexed
	 * correctly by verifying if it is returned in the query result.
	 * @throws Exception 
	 */
	@Test
	public void indexOneOutdated() throws Exception {
		assertIndexOutdated(1);
	}

	/**
	 * Test the "index outdated" admin function. First, two annotations are
	 * created without indexing them. Then the index outdated admin function is
	 * executed. And finally it is checked if the annotations were indexed
	 * correctly by verifying if they are returned in the query result.
	 * @throws Exception 
	 */
	@Test
	public void indexTwoOutdated() throws Exception {
		assertIndexOutdated(2);
	}

	/**
	 * Helper function to support testing the "index outdated" admin function
	 * using a given number of annotations. First, the annotations are created
	 * without indexing them. Then the index outdated admin function is
	 * executed. And finally it is checked if the annotations were indexed
	 * correctly by verifying if they are returned in the query result.
	 * @throws Exception 
	 */
	private void assertIndexOutdated(int numAnnotations) throws Exception {

			List<Annotation> annotations = new ArrayList<Annotation>();
			
			// create test annotations without indexing it
			for (int i = 0; i < numAnnotations; i++) {
				annotations.add(createTestAnnotation(TAG_INDEXING, false, null));
				createdAnnotations.add(annotations.get(i).getIdentifier());
			}
			assertNotNull(annotations);
			assertEquals(numAnnotations, annotations.size());
			
			String QUERY_BY_CREATOR = "creator_uri:\""+ annotations.get(0).getCreator().getHttpUrl() + "\"";
			
			// search for the annotation just created using the generator_id
			// value defined by VALUE_INDEXING_TESTSET
			AnnotationPage annPgBefore = searchAnnotationsAddQueryField(QUERY_BY_CREATOR, null, null, null, null,
					SearchProfiles.MINIMAL, null);
			assertNotNull(annPgBefore, "AnnotationPage must not be null");
			// annotations were not indexed, therefore it must not show up in
			// the search result
			// assertEquals(0, annPgBefore.getTotalInCollection());
			// but failing tests might leave data behind
			long outdatedAnnotationsBefore = annPgBefore.getTotalInCollection();
			if (outdatedAnnotationsBefore > 0)
				log.warn("expected 0 outdated annotations before test, but found: " + outdatedAnnotationsBefore);

			// index outdated annotations including the test annotation which
			// was intentionally not indexed
			ResponseEntity<String> result = reindexOutdated();
			assertEquals(HttpStatus.OK, result.getStatusCode());
//			String indexed = JsonUtils.extractValueFromJsonString(valueName, result.getBody())
			
			// search for the annotation just created using the generator_id
			// value defined by VALUE_INDEXING_TESTSET
			AnnotationPage annPgAfter = searchAnnotationsAddQueryField(QUERY_BY_CREATOR,
					Integer.toString(Query.DEFAULT_PAGE), Integer.toString(Query.DEFAULT_PAGE_SIZE), null, null,
					SearchProfiles.MINIMAL, null);
			assertNotNull(annPgAfter, "AnnotationPage must not be null");
			// Now that the annotation was indexed, it must show up in the
			// search result
			//normally it should be equal, but there might be annotations what were not indexed created by other users
			assertTrue(annPgAfter.getTotalInCollection() >= outdatedAnnotationsBefore + numAnnotations);
		
  }
	

	/**
	 * Block Concurrent Batch Indexing Jobs
	 * @throws Exception 
	 */
	@Test
	public void blockConcurrentBatchIndexingJobs() throws Exception {
		
		// create test annotations without indexing it
		List<Annotation> annotations = createAnnotationsTestSet(ANNOTATION_TESTSET_LOCK_SIZE, TAG_INDEXING_LOCK, false);
		assertNotNull(annotations);
		assertEquals(ANNOTATION_TESTSET_LOCK_SIZE, annotations.size());		

        for (Annotation anno : annotations) {
          createdAnnotations.add(anno.getIdentifier());
        }		
			
		ApiInvoker call1 = new ApiInvoker();
		Thread th1 = new Thread(call1);
		
		ApiInvoker call2 = new ApiInvoker();
		Thread th2 = new Thread(call2);
		
		th1.start();
		
		//wait 200 ms
		{
		long now = System.currentTimeMillis();
		
		log.debug(now);
		while(System.currentTimeMillis() < (now + 200));
		log.debug(System.currentTimeMillis());
		}
		
		th2.start();
		
		//wait for execution of started threads
		while(th1.isAlive() || th2.isAlive());
		
		assertTrue(call1.getResult().getStatusCode() != call2.getResult().getStatusCode());
		assertTrue(call1.getResult().getStatusCode() == HttpStatus.OK || call2.getResult().getStatusCode() == HttpStatus.OK);
		log.debug(call1.getResult());
		log.debug(call2.getResult());
		
	}
	
	
	class ApiInvoker implements Runnable {

		ResponseEntity<String> result;
		
		@Override
		public void run() {
		try {
          result = reindexOutdated();
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
		}
		
		public ResponseEntity<String> getResult() {
			return result;
		}
	}
	
	private List<Annotation> createAnnotationsTestSet(int numAnnotations, String testAnnotationTag, boolean index) throws Exception {
		List<Annotation> annotations = new ArrayList<Annotation>();
		for(int i = 0; i < numAnnotations; i++) 
			annotations.add(createTestAnnotation(testAnnotationTag, index, null));
		return annotations;
	}
	
}

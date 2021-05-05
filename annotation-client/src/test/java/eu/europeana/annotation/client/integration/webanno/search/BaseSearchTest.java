package eu.europeana.annotation.client.integration.webanno.search;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.europeana.annotation.client.AnnotationSearchApiImpl;
import eu.europeana.annotation.client.integration.webanno.tag.BaseTaggingTest;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.vocabulary.fields.WebAnnotationModelFields;

//TODO: should extend BaseWebAnnotationProtocolTest instead
public class BaseSearchTest extends BaseTaggingTest{

    static final int TOTAL_BY_ID_FOUND = 1;
	
	protected Logger log = LogManager.getLogger(getClass());
	
	private AnnotationSearchApiImpl annotationSearchApi;
	
	
	/**
	 * Create annotations data set for search tests execution
	 */
	public AnnotationSearchApiImpl getAnnotationSearchApi() {
		// lazy initialization
		if (annotationSearchApi == null)
			annotationSearchApi = new AnnotationSearchApiImpl();
		return annotationSearchApi;
	}
	
	/**
	 * Search annotations by textual body value for different body types
	 * @param bodyValue
	 * @param foundAnnotationsNumber
	 */
	protected void searchByBodyValue(String bodyValue, int foundAnnotationsNumber) {
	    searchByBodyValue(bodyValue, SearchProfiles.MINIMAL, null, foundAnnotationsNumber);
	}
	
	protected AnnotationPage searchByBodyValue(String bodyValue, SearchProfiles profile, String limit, int foundAnnotationsNumber) {
		AnnotationPage annPg = getAnnotationSearchApi().searchAnnotations(bodyValue, null, 
			WebAnnotationFields.CREATED,
			"desc", "0", limit, profile, null);
			
		assertNotNull(annPg, "AnnotationPage must not be null");
		assertTrue(foundAnnotationsNumber <= annPg.getTotalInCollection());
		return annPg;
	}
}

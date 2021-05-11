package eu.europeana.annotation.client.integration.webanno.search;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.europeana.annotation.client.AnnotationSearchApiImpl;
import eu.europeana.annotation.client.integration.webanno.tag.BaseTaggingTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;

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
	 * @param query
	 * @param foundAnnotationsNumber
	 */
	protected Annotation searchLastCreated(String query) {
	    AnnotationPage annPg = search(query, SearchProfiles.STANDARD, "1");
	    assertNotNull(annPg);
	    assert(annPg.getTotalInPage() > 0);
	    assert(annPg.getTotalInCollection() > 0);
	    assertNotNull(annPg.getAnnotations());
	    Annotation anno = annPg.getAnnotations().get(0);
	    assertNotNull(anno);
//	    assertTrue(foundAnnotationsNumber <= annPg.getTotalInCollection());
	    return anno; 
	}
	
	protected AnnotationPage search(String bodyValue, SearchProfiles profile, String limit) {
		AnnotationPage annPg = getAnnotationSearchApi().searchAnnotations(bodyValue, null, 
			WebAnnotationFields.CREATED,
			"desc", "0", limit, profile, null);
			
		assertNotNull(annPg, "AnnotationPage must not be null");
		return annPg;
	}
}

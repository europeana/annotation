package eu.europeana.annotation.tests.webanno.search;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.tests.webanno.tag.BaseTaggingTest;

//TODO: should extend BaseWebAnnotationProtocolTest instead
@SpringBootTest
@AutoConfigureMockMvc
public class BaseSearchTest extends BaseTaggingTest{

    static final int TOTAL_BY_ID_FOUND = 1;
	
	protected Logger log = LogManager.getLogger(getClass());
	
	/**
	 * Search annotations by textual body value for different body types
	 * @param query
	 * @param foundAnnotationsNumber
	 * @throws Exception 
	 */
	protected Annotation searchLastCreated(String query) throws Exception {
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
	
	protected AnnotationPage search(String bodyValue, SearchProfiles profile, String limit) throws Exception {
		AnnotationPage annPg = searchAnnotations(bodyValue, null, 
			WebAnnotationFields.CREATED,
			"desc", "0", limit, profile, null);
			
		assertNotNull(annPg, "AnnotationPage must not be null");
		return annPg;
	}
}

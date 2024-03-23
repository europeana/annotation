package eu.europeana.annotation.client.integration.webanno.link;

import org.apache.stanbol.commons.exception.JsonParseException;
import eu.europeana.annotation.client.integration.webanno.BaseWebAnnotationTest;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class BaseLinkingTest extends BaseWebAnnotationTest {

	public static final String LINK_MINIMAL = "/link/minimal.json";
	public static final String LINK_STANDARD = "/link/standard.json";
	public static final String LINK_EDM_IS_SIMMILAR_TO = "/link/edmIsSimilarTo.json";
	public static final String LINK_EDM_IS_SIMMILAR_TO_MINIMAL = "/link/edmIsSimilarTo_minimal.json";
	
	
	protected Annotation parseLink(String jsonString) throws JsonParseException {
		MotivationTypes motivationType = MotivationTypes.LINKING;
		return parseAnnotation(jsonString, motivationType);		
	}

}

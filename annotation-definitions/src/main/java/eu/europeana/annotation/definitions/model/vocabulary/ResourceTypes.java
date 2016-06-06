package eu.europeana.annotation.definitions.model.vocabulary;

import eu.europeana.annotation.definitions.model.vocabulary.fields.WebAnnotationModelKeywords;

public enum ResourceTypes implements JsonKeyword{
	TEXTUAL_BODY(WebAnnotationModelKeywords.CLASS_TEXTUAL_BODY), 
	EXTERNAL_DATASET(WebAnnotationModelKeywords.CLASS_DATASET), 
	EXTERNAL_IMAGE(WebAnnotationModelKeywords.CLASS_IMAGE), 
	EXTERNAL_VIDEO(WebAnnotationModelKeywords.CLASS_VIDEO),
	EXTERNAL_SOUND(WebAnnotationModelKeywords.CLASS_SOUND) , 
	EXTERNAL_TEXT(WebAnnotationModelKeywords.CLASS_TEXT), 
	SPECIFIC (WebAnnotationModelKeywords.CLASS_SPECIFIC_RESOURCE), 
	GRAPH (WebAnnotationModelKeywords.CLASS_GRAPH), 
	PLACE (WebAnnotationModelKeywords.CLASS_EDM_PLACE);
	
	
	private String jsonValue;

	ResourceTypes(String jsonValue){
		this.jsonValue = jsonValue; 
	}
	
		
//	public static boolean contains(String test) {
//
//	    for (ResourceTypes c : ResourceTypes.values()) {
//	        if (c.name().equals(test)) {
//	            return true;
//	        }
//	    }
//
//	    return false;
//	}

	@Override
	public String getJsonValue() {
		return jsonValue;
	}	

	
}

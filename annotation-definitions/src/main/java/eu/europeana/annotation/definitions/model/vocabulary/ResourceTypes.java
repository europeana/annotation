package eu.europeana.annotation.definitions.model.vocabulary;

import eu.europeana.annotation.definitions.model.vocabulary.fields.WebAnnotationModelKeywords;

public enum ResourceTypes implements JsonKeyword{
	TEXTUAL_BODY(WebAnnotationModelKeywords.CLASS_TEXTUAL_BODY), 
	EXTERNAL_DATASET(WebAnnotationModelKeywords.CLASS_DATASET), 
	EXTERNAL_IMAGE(WebAnnotationModelKeywords.CLASS_IMAGE), 
	EXTERNAL_VIDEO(WebAnnotationModelKeywords.CLASS_VIDEO),
	EXTERNAL_SOUND(WebAnnotationModelKeywords.CLASS_SOUND) , 
	EXTERNAL_TEXT(WebAnnotationModelKeywords.CLASS_TEXT), 
	SPECIFIC_RESOURCE (WebAnnotationModelKeywords.CLASS_SPECIFIC_RESOURCE), 
	FULL_TEXT_RESOURCE (WebAnnotationModelKeywords.CLASS_FULL_TEXT_RESOURCE), 
	GRAPH (WebAnnotationModelKeywords.CLASS_GRAPH), 
	PLACE (WebAnnotationModelKeywords.CLASS_EDM_PLACE),
	AGENT (WebAnnotationModelKeywords.CLASS_EDM_ENTITY);
	
	
	private String jsonValue;

	ResourceTypes(String jsonValue){
		this.jsonValue = jsonValue; 
	}

	@Override
	public String getJsonValue() {
		return jsonValue;
	}	

	public boolean hasName(String name) {
		return this.name().equalsIgnoreCase(name);
	}	
	
	public boolean hasJsonValue(String jsonValue) {
		return this.getJsonValue().equalsIgnoreCase(jsonValue);
	}
}

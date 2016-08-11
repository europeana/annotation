package eu.europeana.annotation.definitions.model.vocabulary;

import eu.europeana.annotation.definitions.exception.AnnotationAttributeInstantiationException;
import eu.europeana.annotation.definitions.model.vocabulary.fields.WebAnnotationModelKeywords;

public enum ContextTypes implements JsonKeyword{
	ANNO(WebAnnotationModelKeywords.WA_CONTEXT), 
	EDM(WebAnnotationModelKeywords.EDM_CONTEXT),
	ENTITY(WebAnnotationModelKeywords.ENTITY_CONTEXT);
	
	private String jsonValue;

	ContextTypes(String jsonValue){
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
	
	public static ContextTypes  valueOfJsonValue(String jsonValue) {

//		if(jsonValue == null)
//			return null;
		
	    for (ContextTypes ct : ContextTypes.values()) {
	        if (ct.getJsonValue().equalsIgnoreCase(jsonValue)) {
	            return ct;
	        }
	    }

	    throw new AnnotationAttributeInstantiationException(
	    		AnnotationAttributeInstantiationException.MESSAGE_UNKNOWN_KEYWORD, jsonValue);
	}	
	
}

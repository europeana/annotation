package eu.europeana.annotation.definitions.model.vocabulary;

public enum AnnotationScenarioTypes {

	TRANSCRIPTIONS("transcriptions"), 
	SUBTITLES("subtitles"), 
	SEMANTIC_TAGS("semantic_tags"), 
	SIMPLE_TAGS("simple_tags"), 
	GEO_TAGS("geo_tags"), 
	OBJECT_LINKS("object_links");
	
	private String scenarioType;
	
	AnnotationScenarioTypes(String scenarioType){
		this.scenarioType = scenarioType;
	}

	public String getScenarioType() {
		return scenarioType;
	}
}

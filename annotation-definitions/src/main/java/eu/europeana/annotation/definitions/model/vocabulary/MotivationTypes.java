package eu.europeana.annotation.definitions.model.vocabulary;

public enum MotivationTypes {

	  BOOKMARKING("oa:bookmarking")
	, CLASSIFYING("oa:classifying")
	, COMMENTING("oa:commenting")
	, DESCRIBING("oa:describing")
	, EDITING("oa:editing")
	, HIGHLIGHTING("oa:highlighting")
	, IDENTIFYING("oa:identifying")
	, LINKING("oa:linking")
	, MODERATING("oa:moderating")
	, QUESTIONING("oa:questioning")
	, REPLYING("oa:replying")
	, TAGGING("oa:tagging"),
	UNKNOWN("oa:unknown"); 

	
	private String oaType;
	
	MotivationTypes(String oaType){
		this.oaType = oaType;
	}
	
	public String getOaType(){
		return oaType;
	}
	
	public static MotivationTypes getType(String oaType){
		String[] values = oaType.split(":", 2);
		//last token
		String value = values[values.length -1];
		return valueOf(value.toUpperCase());
	}
	
}

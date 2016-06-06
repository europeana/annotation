package eu.europeana.annotation.definitions.model.vocabulary;

public enum MotivationTypes implements JsonKeyword{

	  BOOKMARKING("oa:bookmarking", null)
	, CLASSIFYING("oa:classifying", null)
	, COMMENTING("oa:commenting", null)
	, DESCRIBING("oa:describing", null)
	, EDITING("oa:editing", null)
	, HIGHLIGHTING("oa:highlighting", null)
	, IDENTIFYING("oa:identifying", null)
	, LINKING("oa:linking", "link")
	, MODERATING("oa:moderating", null)
	, QUESTIONING("oa:questioning", null)
	, REPLYING("oa:replying", null)
	, TAGGING("oa:tagging", "tag"),
	UNKNOWN("oa:unknown", "unknown"); 

	
	private String oaType;
	private String annoType;
	
	MotivationTypes(String oaType, String annoType){
		this.oaType = oaType;
		this.annoType = annoType;
	}
	
	public String getOaType(){
		return oaType;
	}
	
	public static MotivationTypes getType(String oaType){
		String[] values = oaType.split(":", 2);
		//last token
		String value = values[values.length -1];
		try{
			return valueOf(value.toUpperCase());
		}catch(Throwable th){
			return MotivationTypes.UNKNOWN;
		}	
	}

	public String getAnnoType() {
		return annoType;
	}

	public static MotivationTypes getTypeForAnnoType(String annoType){
		
		if(annoType == null)
			return null;
		
		for (int i = 0; i < MotivationTypes.values().length; i++) {
			if(annoType.equals(MotivationTypes.values()[i].getAnnoType()))
				return MotivationTypes.values()[i];
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return super.toString() + ":" + getOaType() + ":" + getAnnoType();
	}

	@Override
	public String getJsonValue() {
		return getOaType();
	}
	
}

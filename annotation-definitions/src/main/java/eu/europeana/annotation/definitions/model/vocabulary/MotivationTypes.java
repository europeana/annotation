package eu.europeana.annotation.definitions.model.vocabulary;

public enum MotivationTypes implements JsonKeyword{

	  BOOKMARKING("bookmarking", null)
	, CLASSIFYING("classifying", null)
	, COMMENTING("commenting", null)
	, DESCRIBING("describing", null)
	, EDITING("editing", null)
	, HIGHLIGHTING("highlighting", null)
	, IDENTIFYING("identifying", null)
	, LINKING("linking", "link")
	, MODERATING("moderating", null)
	, QUESTIONING("questioning", null)
	, REPLYING("replying", null)
	, TRANSCRIBING("transcribing", null)
	, TRANSLATING("translating", null)
	, SUBTITLING("subtitling", null)
	, CAPTIONING("captioning", null)
	, TAGGING("tagging", "tag")
	, LINKFORCONTRIBUTING("linkForContributing", null),
	UNKNOWN("unknown", "unknown"); 

	
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

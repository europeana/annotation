package eu.europeana.annotation.definitions.model.vocabulary.fields;


public enum WAPropEnum implements WebAnnotationProperty{

	CONTEXT(WebAnnotationModelFields.AT_CONTEXT, true),
	ID(WebAnnotationModelFields.ID, true),
	TYPE(WebAnnotationModelFields.TYPE, true);
	
	private static final String AT = "@";
	private static final String COLON = ":";
	private final boolean jsonLdKeyword;
	private String jsonPropertyName;
	private String namespace;

	
	WAPropEnum(String jsonPropertyName){
		this(jsonPropertyName, false, null);
	}
	
	WAPropEnum(String jsonPropertyName, boolean jsonLdKeyword){
		this(jsonPropertyName, jsonLdKeyword, null);
	}

	WAPropEnum(String jsonPropertyName, boolean jsonLdKeyword, String namespace){
		this.jsonPropertyName = jsonPropertyName;
		this.namespace = namespace;
		this.jsonLdKeyword = jsonLdKeyword;
	}
	
	/**
	 * This is a commodity method to determine the appropriate wa property basing of different variations of json property names
	 * (i.e. with or without namespace, with @)  
	 * @param propertyName
	 * @return
	 */
	public static WAPropEnum getByJsonProperty(String jsonPropName){
		String[] values = jsonPropName.split(COLON, 2);
		//last token
		String value = values[values.length -1];
		//remove "@"
		if(value.startsWith(AT));
			value = value.substring(1);
		try{
			return valueOf(value.toUpperCase());
		}catch(NullPointerException e){
			//if not found return null
			return null;
		}
	}
	
	@Override
	public String getJsonPropertyName() {
		return jsonPropertyName;
	}
	
	public String getDefaultJsonLdPropertyName(){
		if(isJsonLdKeyword() && !getJsonPropertyName().startsWith(AT))
			return AT + getJsonPropertyName();
		
		return getJsonPropertyName();
	}
	
	
	@Override
	public String toString() {
		return getJsonPropertyName();
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public String getCanonicalName() {
		String ret = getJsonPropertyName();
		if(getNamespace() != null)
			ret = getNamespace() + COLON + getJsonPropertyName();
		
		return ret;
	}

	public boolean isJsonLdKeyword() {
		return jsonLdKeyword;
	}
	
	
	
}

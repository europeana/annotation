package eu.europeana.annotation.definitions.model.vocabulary.fields;


public enum WAPropEnum implements WebAnnotationProperty{

	CONTEXT(WebAnnotaionModelFields.AT_CONTEXT, null);
	
	private static final String AT = "@";
	private static final String COLON = ":";
	private String jsonPropertyName;
	private String namespace;
	
	WAPropEnum(String jsonPropertyName, String namespace){
		this.jsonPropertyName = jsonPropertyName;
		this.namespace = namespace;
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
	
	
	
}

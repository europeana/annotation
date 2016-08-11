package eu.europeana.annotation.definitions.model.vocabulary.fields;

public interface WebAnnotationProperty {

	/**
	 * 
	 * @return the jsonPropertyName as defined in (Web) Annotation LD Context
	 */
	public String getJsonPropertyName();
	public String getNamespace();
	public String getCanonicalName();
	public boolean isJsonLdKeyword();
	/**
	 * This method returns <code>@<code> prefix + {@link #getJsonPropertyName()} if the property is a JSONLD Keyword. 
	 * Otherwise it returnds the same as {@link #getJsonPropertyName()}
	 * @return the default property name in JsonLD (and not the one wired in the Web Annotation context) 
	 *    
	 */
	public String getDefaultJsonLdPropertyName();
	
}

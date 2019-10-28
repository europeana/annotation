package eu.europeana.annotation.definitions.model.vocabulary.fields;

public class WebAnnotationModelKeywords {

	//Hard-coded value to avoid model dependency to java libraries 
	public static final String MIME_TYPE_TEXT_PLAIN = "text/plain";   
	
	//Resource classes
	public static final String CLASS_TEXTUAL_BODY = "TextualBody";
	public static final String CLASS_DATASET = "Dataset";
	public static final String CLASS_IMAGE = "Image";
	public static final String CLASS_VIDEO = "Video";
	public static final String CLASS_SOUND = "Sound";
	public static final String CLASS_TEXT = "Text";
	public static final String CLASS_SPECIFIC_RESOURCE = "SpecificResource";
	public static final String CLASS_FULL_TEXT_RESOURCE = "FullTextResource";
	public static final String CLASS_VCARD_ADDRESS = "vcard:Address";
	
	//EUROPEANA EXTENSIONS CLASSES
	public static final String CLASS_GRAPH = "Graph";
	public static final String CLASS_EDM_PLACE = "Place";
	
	
	//Contexts
	public static final String WA_CONTEXT = "http://www.w3.org/ns/anno.jsonld";
	public static final String EDM_CONTEXT = "http://www.europeana.eu/schemas/context/edm.jsonld";
	public static final String ENTITY_CONTEXT = "http://www.europeana.eu/schemas/context/entity.jsonld";
	
}

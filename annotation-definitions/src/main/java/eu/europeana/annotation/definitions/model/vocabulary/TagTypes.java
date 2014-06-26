package eu.europeana.annotation.definitions.model.vocabulary;

public enum TagTypes {
	SIMPLE_TAG, SEMANTIC_TAG;
	
	public static boolean isSimpleTag(String type){
		return SIMPLE_TAG.name().equals(type);
	}
	
	public static boolean isSemanticTag(String type){
		return SEMANTIC_TAG.name().equals(type);
	}
}

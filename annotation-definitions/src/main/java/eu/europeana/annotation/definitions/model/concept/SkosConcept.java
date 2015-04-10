package eu.europeana.annotation.definitions.model.concept;

import java.util.Map;


/**
 * This interface defines method for all SKOS concepts.
 */
public interface SkosConcept extends Concept {

	public abstract Map<String, String> getInScheme();

	public abstract void setInScheme(Map<String, String> inScheme);
	
	public abstract void addInSchemeInMapping(String id, String label);
	
	public abstract void setTopConceptOf(Map<String, String> topConceptOf);
	
	public abstract void addTopConceptOfInMapping(String id, String label);
	
	public abstract Map<String, String> getTopConceptOf();
	
	public abstract String getSkos();

	public abstract void setSkos(String skos);
	
	public String getSkosType();

	public void setSkosType(String skosType);

	public String getHttpUri();

	public void setHttpUri(String httpUri);	
		
}

package eu.europeana.annotation.definitions.model.impl;

import eu.europeana.annotation.definitions.model.Provider;
import eu.europeana.annotation.definitions.model.vocabulary.IdGenerationTypes;


public abstract class AbstractProvider implements Provider {

	private String name;
	private String uri;
	private IdGenerationTypes idGeneration;

	
	public AbstractProvider(){
		super();
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getIdGeneration() {
		return idGeneration.getIdType();
	}

	public void setIdGeneration(IdGenerationTypes idGeneration) {
		this.idGeneration = idGeneration;
	}

	@Override
	public boolean equals(Object other) {
	    if (!(other instanceof Provider)) {
	        return false;
	    }

	    Provider that = (Provider) other;

	    boolean res = true;
	    
	    /**
	     * equality check for all relevant fields.
	     */
	    if ((this.getName() != null) && (that.getName() != null) &&
	    		(!this.getName().equals(that.getName()))) {
	    	System.out.println("Provider objects have different 'name' fields.");
	    	res = false;
	    }
	    
	    if ((this.getUri() != null) && (that.getUri() != null) &&
	    		(!this.getUri().equals(that.getUri()))) {
	    	System.out.println("Provider objects have different 'uri' fields.");
	    	res = false;
	    }
	    
	    if ((this.getIdGeneration() != null) && (that.getIdGeneration() != null) &&
	    		(!this.getIdGeneration().equals(that.getIdGeneration()))) {
	    	System.out.println("Provider objects have different 'IdGeneration' fields.");
	    	res = false;
	    }
	    
	    return res;
	}
	
	@Override
	public String toString() {
		String res = "### Provider ###\n";
		if (name != null) 
			res = res + "\t" + "name:" + name + "\n";
		if (uri != null) 
			res = res + "\t" + "uri:" + uri + "\n";
		if (idGeneration != null) 
			res = res + "\t" + "idGeneration:" + idGeneration + "\n";
		return res;
	}	
	
}

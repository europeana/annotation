package eu.europeana.annotation.definitions.model.target.impl;

import eu.europeana.annotation.definitions.model.resource.impl.BaseSpecificResource;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.vocabulary.TargetTypes;

public class BaseTarget extends BaseSpecificResource implements Target {
 
	@Override
	public void setTypeEnum(TargetTypes targetType) {
		setInternalType(targetType.name());
	}
	
	public BaseTarget(){}
	
	public BaseTarget(String internalType){
		super();
		setInternalType(internalType);
	}

	public BaseTarget(TargetTypes targetType){
		super();
		setTypeEnum(targetType);
	}
	
	/**
	 * TODO: refactor - buggy implementation it doesn't verify this.a=null vs. that.a=not_null 
	 */
	@Override
	public boolean equals(Object other) {
	    if (!(other instanceof Target)) {
	        return false;
	    }

	    Target that = (Target) other;

	    boolean res = true;
	    
	    /**
	     * equality check for all relevant fields.
	     */
	    if ((this.getContentType() != null) && (that.getContentType() != null) &&
	    		(!this.getContentType().equals(that.getContentType()))) {
	    	res = false;
	    }
	    		
//	    if ((this.getEuropeanaId() != null) && (that.getEuropeanaId() != null) &&
//	    		(!this.getEuropeanaId().equals(that.getEuropeanaId()))) {
//	    	res = false;
//	    }

	    if ((this.getHttpUri() != null) && (that.getHttpUri() != null) &&
	    		(!this.getHttpUri().equals(that.getHttpUri()))) {
	    	res = false;
	    }

	    if ((this.getLanguage() != null) && (that.getLanguage() != null) &&
	    		(!this.getLanguage().equals(that.getLanguage()))) {
	    	res = false;
	    }
	   
	    if ((this.getSelector() != null) && (that.getSelector() != null) &&
	    		(!this.getSelector().equals(that.getSelector()))) {
//	    		(this.getSelector().getSelectorType() != null) && (that.getSelector().getSelectorType() != null) &&
//	    		(!this.getSelector().getSelectorType().equals(that.getSelector().getSelectorType()))) {
	    	res = false;
	    }

	    if ((this.getScope() != null) && (that.getScope() != null) && !this.getScope().equals(that.getScope())) {
    		res = false;
	    }
	    	
	    if ((this.getSource() != null) && (that.getSource() != null) && !this.getSource().equals(that.getSource())) {
    		res = false;
	    }
	    	
	    
	    if ((this.getSourceResource() != null) && (that.getSourceResource() != null)) {
		    if ((this.getSourceResource().getContentType() != null) && (that.getSourceResource().getContentType() != null) &&
		    		(!this.getSourceResource().getContentType().equals(that.getSourceResource().getContentType()))) {
		    	res = false;
		    }
	
		    if ((this.getSourceResource().getHttpUri() != null) && (that.getSourceResource().getHttpUri() != null) &&
		    		(!this.getSourceResource().getHttpUri().equals(that.getSourceResource().getHttpUri()))) {
		    	res = false;
		    }
	
		    if ((this.getSourceResource().getLanguage() != null) && (that.getSourceResource().getLanguage() != null) &&
		    		(!this.getSourceResource().getLanguage().equals(that.getSourceResource().getLanguage()))) {
		    	res = false;
		    }
		   
	
		    if ((this.getSourceResource().getValue() != null) && (that.getSourceResource().getValue() != null) &&
		    		(!this.getSourceResource().getValue().equals(that.getSourceResource().getValue()))) {
		    	res = false;
		    }
	    }
	    
	    if ((this.getType() != null) && (that.getType() != null) &&
	    		(!this.getType().equals(that.getType()))) {
	    	res = false;
	    }

	    if ((this.getValue() != null) && (that.getValue() != null) &&
	    		(!this.getValue().equals(that.getValue()))) {
	    	res = false;
	    }
	    
	    return res;
	}
		
	public boolean equalsContent(Object other) {
		return equals(other);
	}
	
	@Override
	public String toString() {
		String res = "\t### Target ###\n";
		
		if (getType() != null) 
			res = res + "\t\t" + "type:" + getType() + "\n";
		if (getContentType() != null) 
			res = res + "\t\t" + "contentType:" + getContentType().toString() + "\n";
		if (getHttpUri() != null) 
			res = res + "\t\t" + "httpUri:" + getHttpUri() + "\n";
		if (getLanguage() != null) 
			res = res + "\t\t" + "language:" + getLanguage().toString() + "\n";
		if (getValue() != null) 
			res = res + "\t\t" + "value:" + getValue().toString() + "\n";
		if (getSelector() != null) 
			res = res + "\t\t" + "Selector:" + getSelector().toString() + "\n";
		if (getSource() != null) 
			res = res + "\t\t" + "Source:" + getSource().toString() + "\n";
		if (getScope() != null) 
			res = res + "\t\t" + "Scope:" + getScope().toString() + "\n";
		return res;
	}	
}


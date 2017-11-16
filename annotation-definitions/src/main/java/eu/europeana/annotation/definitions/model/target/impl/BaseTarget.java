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
	    	System.out.println("Target objects have different content types.");
	    	res = false;
	    }
	    		
//	    if ((this.getEuropeanaId() != null) && (that.getEuropeanaId() != null) &&
//	    		(!this.getEuropeanaId().equals(that.getEuropeanaId()))) {
//	    	System.out.println("Target objects have different EuropeanaIds.");
//	    	res = false;
//	    }

	    if ((this.getHttpUri() != null) && (that.getHttpUri() != null) &&
	    		(!this.getHttpUri().equals(that.getHttpUri()))) {
	    	System.out.println("Target objects have different httpUris.");
	    	res = false;
	    }

	    if ((this.getLanguage() != null) && (that.getLanguage() != null) &&
	    		(!this.getLanguage().equals(that.getLanguage()))) {
	    	System.out.println("Target objects have different language values.");
	    	res = false;
	    }
	   
	    if ((this.getSelector() != null) && (that.getSelector() != null) &&
	    		(!this.getSelector().equals(that.getSelector()))) {
//	    		(this.getSelector().getSelectorType() != null) && (that.getSelector().getSelectorType() != null) &&
//	    		(!this.getSelector().getSelectorType().equals(that.getSelector().getSelectorType()))) {
	    	System.out.println("Target objects have different selectors.");
	    	res = false;
	    }

	    if ((this.getScope() != null) && (that.getScope() != null) && !this.getScope().equals(that.getScope())) {
	    	System.out.println("Target objects have different scope values.");
    		res = false;
	    }
	    	
	    if ((this.getSource() != null) && (that.getSource() != null) && !this.getSource().equals(that.getSource())) {
	    	System.out.println("Target objects have different source values.");
    		res = false;
	    }
	    	
	    
	    if ((this.getSourceResource() != null) && (that.getSourceResource() != null)) {
		    if ((this.getSourceResource().getContentType() != null) && (that.getSourceResource().getContentType() != null) &&
		    		(!this.getSourceResource().getContentType().equals(that.getSourceResource().getContentType()))) {
		    	System.out.println("Target objects have different content types.");
		    	res = false;
		    }
	
		    if ((this.getSourceResource().getHttpUri() != null) && (that.getSourceResource().getHttpUri() != null) &&
		    		(!this.getSourceResource().getHttpUri().equals(that.getSourceResource().getHttpUri()))) {
		    	System.out.println("Target objects have different source httpUris.");
		    	res = false;
		    }
	
		    if ((this.getSourceResource().getLanguage() != null) && (that.getSourceResource().getLanguage() != null) &&
		    		(!this.getSourceResource().getLanguage().equals(that.getSourceResource().getLanguage()))) {
		    	System.out.println("Target objects have different source languages.");
		    	res = false;
		    }
		   
	
		    if ((this.getSourceResource().getValue() != null) && (that.getSourceResource().getValue() != null) &&
		    		(!this.getSourceResource().getValue().equals(that.getSourceResource().getValue()))) {
		    	System.out.println("Target objects have different source values.");
		    	res = false;
		    }
	    }
	    
	    if ((this.getType() != null) && (that.getType() != null) &&
	    		(!this.getType().equals(that.getType()))) {
	    	System.out.println("Target objects have different target type values.");
	    	res = false;
	    }

	    if ((this.getValue() != null) && (that.getValue() != null) &&
	    		(!this.getValue().equals(that.getValue()))) {
	    	System.out.println("Target objects have different 'value' fields.");
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


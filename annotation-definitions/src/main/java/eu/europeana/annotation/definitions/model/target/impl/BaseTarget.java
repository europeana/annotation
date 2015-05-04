package eu.europeana.annotation.definitions.model.target.impl;

import eu.europeana.annotation.definitions.model.resource.impl.OaSpecificResource;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.vocabulary.TargetTypes;

public class BaseTarget extends OaSpecificResource implements Target {
 
	private String targetType;
	private String inputString;
	private String internalType;

	@Override
	public String getType() {
		return targetType;
	}

	@Override
	public void setType(String targetType) {
		this.targetType = targetType;
	}
	
	@Override
	public void setTypeEnum(TargetTypes targetType) {
		this.targetType = targetType.name();
	}
	
	public BaseTarget(){}
	
	public BaseTarget(String targetType){
		super();
		setType(targetType);
	}

	public BaseTarget(TargetTypes targetType){
		super();
		setTypeEnum(targetType);
	}

	@Override
	public String getInternalType() {
		return internalType;
	}
	@Override
	public void setInternalType(String internalType) {
		this.internalType = internalType;
	}
	
	@Override
	public String getInputString() {
		return inputString;
	}
	@Override
	public void setInputString(String inputString) {
		this.inputString = inputString;
	}
	
//	@Override
//	public String getEuropeanaId() {
//		return europeanaId;
//	}
//
//	@Override
//	public void setEuropeanaId(String europeanaId) {
//		this.europeanaId = europeanaId;
//	};
	
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

	    if ((this.getMediaType() != null) && (that.getMediaType() != null) &&
	    		(!this.getMediaType().equals(that.getMediaType()))) {
	    	System.out.println("Target objects have different media types.");
	    	res = false;
	    }

	    if ((this.getSelector() != null) && (that.getSelector() != null) &&
	    		(!this.getSelector().equals(that.getSelector()))) {
//	    		(this.getSelector().getSelectorType() != null) && (that.getSelector().getSelectorType() != null) &&
//	    		(!this.getSelector().getSelectorType().equals(that.getSelector().getSelectorType()))) {
	    	System.out.println("Target objects have different selectors.");
	    	res = false;
	    }

	    if ((this.getSource() != null) && (that.getSource() != null)) {
		    if ((this.getSource().getContentType() != null) && (that.getSource().getContentType() != null) &&
		    		(!this.getSource().getContentType().equals(that.getSource().getContentType()))) {
		    	System.out.println("Target objects have different content types.");
		    	res = false;
		    }
	
		    if ((this.getSource().getHttpUri() != null) && (that.getSource().getHttpUri() != null) &&
		    		(!this.getSource().getHttpUri().equals(that.getSource().getHttpUri()))) {
		    	System.out.println("Target objects have different source httpUris.");
		    	res = false;
		    }
	
		    if ((this.getSource().getLanguage() != null) && (that.getSource().getLanguage() != null) &&
		    		(!this.getSource().getLanguage().equals(that.getSource().getLanguage()))) {
		    	System.out.println("Target objects have different source languages.");
		    	res = false;
		    }
	
		    if ((this.getSource().getMediaType() != null) && (that.getSource().getMediaType() != null) &&
		    		(!this.getSource().getMediaType().equals(that.getSource().getMediaType()))) {
		    	System.out.println("Target objects have different source media types.");
		    	res = false;
		    }
	
		    if ((this.getSource().getValue() != null) && (that.getSource().getValue() != null) &&
		    		(!this.getSource().getValue().equals(that.getSource().getValue()))) {
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
		
	@Override
	public String toString() {
		String res = "\t### Target ###\n";
		
		if (getType() != null) 
			res = res + "\t\t" + "targetType:" + getType().toString() + "\n";
		if (getContentType() != null) 
			res = res + "\t\t" + "contentType:" + getContentType().toString() + "\n";
		if (getMediaType() != null) 
			res = res + "\t\t" + "mediaType:" + getMediaType() + "\n";
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
		return res;
	}	
}


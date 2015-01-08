package eu.europeana.annotation.definitions.model.body.impl;

import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.resource.impl.BaseInternetResource;
import eu.europeana.annotation.definitions.model.vocabulary.BodyTypes;

public abstract class BaseBody extends BaseInternetResource implements Body {
	private String bodyType;
	
	@Override
	public String getBodyType() {
		return bodyType;
	}
	protected void setBodyTypeEnum(BodyTypes bodyType) {
		this.bodyType = bodyType.name();
	}
	@Override
	public void setBodyType(String bodyTypeStr) {
		this.bodyType = bodyTypeStr;
	}
	protected BaseBody(){} 
	
	@Override
	public boolean equals(Object other) {
	    if (!(other instanceof Body)) {
	        return false;
	    }

	    Body that = (Body) other;

	    boolean res = true;
	    
	    /**
	     * equality check for all relevant fields.
	     */
	    if ((this.getBodyType() != null) && (that.getBodyType() != null) &&
	    		(!this.getBodyType().equals(that.getBodyType()))) {
	    	System.out.println("Body objects have different body types.");
	    	res = false;
	    }
	    
	    if ((this.getContentType() != null) && (that.getContentType() != null) &&
	    		(!this.getContentType().equals(that.getContentType()))) {
	    	System.out.println("Body objects have different content types.");
	    	res = false;
	    }
	    
	    if ((this.getHttpUri() != null) && (that.getHttpUri() != null) &&
	    		(!this.getHttpUri().equals(that.getHttpUri()))) {
	    	System.out.println("Body objects have different httpUris.");
	    	res = false;
	    }
	    
	    if ((this.getLanguage() != null) && (that.getLanguage() != null) &&
	    		(!this.getLanguage().equals(that.getLanguage()))) {
	    	System.out.println("Body objects have different languages.");
	    	res = false;
	    }
		    
	    if ((this.getMediaType() != null) && (that.getMediaType() != null) &&
	    		(!this.getMediaType().equals(that.getMediaType()))) {
	    	System.out.println("Body objects have different media types.");
	    	res = false;
	    }
		    
	    if ((this.getValue() != null) && (that.getValue() != null) &&
	    		(!this.getValue().equals(that.getValue()))) {
	    	System.out.println("Body objects have different values.");
	    	res = false;
	    }
	    
	    return res;
	}
		
	@Override
	public String toString() {
		String res = "\t### Body ###\n";
		
		if (getBodyType() != null) 
			res = res + "\t\t" + "bodyType:" + getBodyType().toString() + "\n";
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
		return res;
	}	
}

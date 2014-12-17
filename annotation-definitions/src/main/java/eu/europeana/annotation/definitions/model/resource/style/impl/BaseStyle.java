package eu.europeana.annotation.definitions.model.resource.style.impl;

import eu.europeana.annotation.definitions.model.resource.impl.BaseInternetResource;
import eu.europeana.annotation.definitions.model.resource.style.Style;

public class BaseStyle extends BaseInternetResource implements Style {

	public boolean isEmbedded() {
		return false;
	}

	public void setAnnotationClass(String annotationClass) {
	}

	public String getAnnotationClass() {
		return null;
	}

	@Override
	public boolean equals(Object other) {
	    if (!(other instanceof Style)) {
	        return false;
	    }

	    Style that = (Style) other;

	    boolean res = true;
	    
	    /**
	     * equality check for all relevant fields.
	     */
	    if ((this.getContentType() != null) && (that.getContentType() != null) &&
	    		(!this.getContentType().equals(that.getContentType()))) {
	    	System.out.println("Style objects have different 'contentType' fields.");
	    	res = false;
	    }
	    
	    if ((this.getMediaType() != null) && (that.getMediaType() != null) &&
	    		(!this.getMediaType().equals(that.getMediaType()))) {
	    	System.out.println("Style objects have different 'mediaType' fields.");
	    	res = false;
	    }
	    
	    if ((this.getHttpUri() != null) && (that.getHttpUri() != null) &&
	    		(!this.getHttpUri().equals(that.getHttpUri()))) {
	    	System.out.println("Style objects have different 'httpUri' fields.");
	    	res = false;
	    }
	    
	    if ((this.getLanguage() != null) && (that.getLanguage() != null) &&
	    		(!this.getLanguage().equals(that.getLanguage()))) {
	    	System.out.println("Style objects have different 'language' fields.");
	    	res = false;
	    }
	    
	    if ((this.getValue() != null) && (that.getValue() != null) &&
	    		(!this.getValue().equals(that.getValue()))) {
	    	System.out.println("Style objects have different 'value' fields.");
	    	res = false;
	    }
	    
	    return res;
	}
		
	@Override
	public String toString() {
		String res = "\t### Style ###\n";
		
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

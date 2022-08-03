package eu.europeana.annotation.definitions.model.body.impl;

import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.resource.impl.BaseSpecificResource;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;

public abstract class BaseBody extends BaseSpecificResource implements Body {
	
	protected Map<String, String> multilingual;
	    
	public void setTypeEnum(BodyInternalTypes bodyType) {
//		this.bodyType = bodyType.name();
		setInternalType(bodyType.name());
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
	    if ((this.getType() != null) && (that.getType() != null) &&
	    		(!this.getType().equals(that.getType()))) {
	    	res = false;
	    }
	    
	    if ((this.getContentType() != null) && (that.getContentType() != null) &&
	    		(!this.getContentType().equals(that.getContentType()))) {
	    	res = false;
	    }
	    
	    if ((this.getHttpUri() != null) && (that.getHttpUri() != null) &&
	    		(!this.getHttpUri().equals(that.getHttpUri()))) {
	    	res = false;
	    }
	    
	    if ((this.getLanguage() != null) && (that.getLanguage() != null) &&
	    		(!this.getLanguage().equals(that.getLanguage()))) {
	    	res = false;
	    }
		    
	    if ((this.getValue() != null) && (that.getValue() != null) &&
	    		(!this.getValue().equals(that.getValue()))) {
	    	res = false;
	    }
	    
	    if ((this.getSource() != null) && (that.getSource() != null) &&
	    		(!this.getSource().equals(that.getSource()))) {
	    	res = false;
	    }
	    
	    if ((this.getPurpose() != null) && (that.getPurpose() != null) &&
	    		(!this.getPurpose().equals(that.getPurpose()))) {
	    	res = false;
	    }
	    
	    return res;
	}
		
	public boolean equalsContent(Object other) {
		return equals(other);
	}
	
	@Override
	public String toString() {
		String res = "\t### Body ###\n";
		
		if (getType() != null) 
			res = res + "\t\t" + "bodyType:" + getType() + "\n";
		if (getContext() != null) 
			res = res + "\t\t" + "@context:" + getContext() + "\n";
		if (getType() != null) 
			res = res + "\t\t" + "internalType:" + getInternalType() + "\n";
		if (getContentType() != null) 
			res = res + "\t\t" + "contentType:" + getContentType() + "\n";
		if (getHttpUri() != null) 
			res = res + "\t\t" + "httpUri:" + getHttpUri() + "\n";
		if (getLanguage() != null) 
			res = res + "\t\t" + "language:" + getLanguage() + "\n";
		if (getValue() != null) 
			res = res + "\t\t" + "value:" + getValue() + "\n";
		if (getSource() != null) 
			res = res + "\t\t" + "bodySource:" + getSource() + "\n";
		if (getPurpose() != null) 
			res = res + "\t\t" + "bodyRole:" + getPurpose() + "\n";
		return res;
	}

	
}

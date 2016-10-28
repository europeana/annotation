package eu.europeana.annotation.definitions.model.resource.impl;

import java.util.ArrayList;
import java.util.List;

import eu.europeana.annotation.definitions.model.resource.InternetResource;
import eu.europeana.annotation.definitions.model.resource.style.Style;

public class BaseInternetResource extends AbstractResource implements InternetResource{

	private String mediaType;
	private List<String> values = new ArrayList<String>(2);

	public void addValue(String value) {
		if (!values.contains(value)) {
			values.add(value);
		}
	}
	
	public List<String> getValues() {
		return values;
	}
	
	public void setValues(List<String> values) {
		this.values = values;
	}	
	
	private String resourceId;
	

	@Override
	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	
	private List<String> resourceIds = new ArrayList<String>(2);
	
	public void addResourceId(String resourceId) {
		if (!resourceIds.contains(resourceId)) {
			resourceIds.add(resourceId);
		}
	}
	
	public List<String> getResourceIds() {
		return resourceIds;
	}
	
	public void setResourceIds(List<String> resourceIds) {
		this.resourceIds = resourceIds;
	}	
	
	@Override
	public String getMediaType() {
		return mediaType;
	}

	@Override
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	@Override
	public void copyInto(InternetResource destination){
		destination.setContentType(this.getContentType());
		destination.setHttpUri(this.getHttpUri());
		destination.setLanguage(this.getLanguage());
		destination.setInternalType(this.getInternalType());
		destination.setValue(this.getValue());
		destination.setValues(this.getValues());
		destination.setContext(this.getContext());
		destination.setResourceId(this.getResourceId());
		destination.setResourceIds(this.getResourceIds());
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
		
	public boolean equalsContent(Object other) {
		return equals(other);
	}
		
	@Override
	public String toString() {
		String res = "\t### BaseInternetResource ###\n";
		
		if (getContentType() != null) 
			res = res + "\t\t" + "contentType:" + getContentType().toString() + "\n";
		if (getHttpUri() != null) 
			res = res + "\t\t" + "httpUri:" + getHttpUri() + "\n";
		if (getLanguage() != null) 
			res = res + "\t\t" + "language:" + getLanguage().toString() + "\n";
		if (getValue() != null) 
			res = res + "\t\t" + "value:" + getValue().toString() + "\n";
		return res;
	}
	
//	@Override
//	public String toString() {
//		String res = "\t### Source ###\n";
//		
//		if (getContentType() != null) 
//			res = res + "\t\t" + "\t\t" + "contentType:" + getContentType().toString() + "\n";
//		if (getHttpUri() != null) 
//			res = res + "\t\t" + "\t\t" + "httpUri:" + getHttpUri().toString() + "\n";
//		if (getMediaType() != null) 
//			res = res + "\t\t" + "\t\t" + "mediaType:" + getMediaType().toString() + "\n";
//		return res;
//	}		
	
}

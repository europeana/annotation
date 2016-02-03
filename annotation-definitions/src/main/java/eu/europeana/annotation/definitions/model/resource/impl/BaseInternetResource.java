package eu.europeana.annotation.definitions.model.resource.impl;

import java.util.ArrayList;
import java.util.List;

import eu.europeana.annotation.definitions.model.resource.InternetResource;
import eu.europeana.annotation.definitions.model.resource.style.Style;

public class BaseInternetResource implements InternetResource{

	private String contentType;
	private String mediaType;
	private String httpUri;
	private String language;
	private String value;
	
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
	public String getContentType() {
		return contentType;
	}

	@Override
	public void setContentType(String contentType) {
		this.contentType = contentType;
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
	public String getHttpUri() {
		return httpUri;
	}

	@Override
	public void setHttpUri(String httpUri) {
		this.httpUri = httpUri;
	}

	@Override
	public String getLanguage() {
		return language;
	}

	@Override
	public void setLanguage(String language) {
		this.language = language;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public void copyInto(InternetResource destination){
		destination.setContentType(this.getContentType());
		destination.setHttpUri(this.getHttpUri());
		destination.setLanguage(this.getLanguage());
		destination.setMediaType(this.getMediaType());
		destination.setValue(this.getValue());
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
		
	public boolean equalsContent(Object other) {
		return equals(other);
	}
		
	@Override
	public String toString() {
		String res = "\t### BaseInternetResource ###\n";
		
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

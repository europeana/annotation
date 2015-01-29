package eu.europeana.annotation.solr.model.internal;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.beans.Field;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.agent.impl.Person;
import eu.europeana.annotation.definitions.model.impl.AbstractAnnotation;

public class SolrAnnotationImpl extends AbstractAnnotation implements SolrAnnotation {

	private String annotationId_string;
	private String resourceId;
	private String label;
	private String annotation_type;
	private String http_uri;
	private String language;

	@Field("*_multilingual")
	protected Map<String, String> multiLingual;

	public Map<String, String> getMultiLingual() {
		return multiLingual;
	}

	public void setMultiLingual(Map<String, String> multiLingual) {
		this.multiLingual = multiLingual;
	}
	
	/**
	 * This method adds a new language/label association to the 
	 * multilingual map.
	 * @param language
	 * @param label
	 */
	public void addLabelInMapping(String language, String label) {
	    if(this.multiLingual == null) {
	        this.multiLingual = new HashMap<String, String>();
	    }
	    this.multiLingual.put(language + "_multilingual", label);
	}

	/* (non-Javadoc)
	 * @see eu.europeana.annotation.solr.model.internal.SolrAnnotation#setAnnotationIdString(java.lang.String)
	 */
	@Override
	@Field("annotationId_string")
	public void setAnnotationIdString(String annotationId){
		int pos = annotationId.lastIndexOf("/");
		AnnotationId annoId = calculateAnnotationIdByString(annotationId);
		setAnnotationId(annoId);
		this.annotationId_string = annotationId;
		this.resourceId = annotationId.substring(1, pos);
	}
	
	@Override
	public String getAnnotationIdString() {
		return annotationId_string;
	}
	
	@Override
	@Field("annotatedBy_string")
	public void setAnnotatedByString(String annotatedBy) {
		Agent creator = new Person();
		creator.setName(annotatedBy);
		
		super.setAnnotatedBy(creator);
	}
	
	@Override
	public String getAnnotatedByString() {
		String res = "";
		if (getAnnotatedBy() != null) {
			res = getAnnotatedBy().getName();
		}
		return res;
	}
	
	@Override
	@Field("resourceId")
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	
	@Override
	public String getResourceId() {
		return resourceId;
	}
	
	@Override
	@Field("label")
	public void setLabel(String label) {
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return label;
	}
	
	@Override
	@Field("annotation_type")
//	public void setType(String type) {
//		this.setType(type);
//	}
	public void setAnnotationType(String annotation_type) {
		this.annotation_type = annotation_type;
	}
	
	@Override
//	public String getType() {
//		return this.getType();
//	}
	public String getAnnotationType() {
		return annotation_type;
	}
	
	@Override
	@Field("http_uri")
	public void setHttpUri(String http_uri) {
		this.http_uri = http_uri;
	}
	
	@Override
	public String getHttpUri() {
		return http_uri;
	}
	
	@Override
	@Field("language")
	public void setLanguage(String language) {
		this.language = language;
	}
	
	@Override
	public String getLanguage() {
		return language;
	}
	
	@Override
	@Field("annotatedAt")
	public void setAnnotatedAt(Date annotatedAt) {
	    super.setAnnotatedAt(annotatedAt);
	}

	@Override
	public Date getAnnotatedAt() {
	    return super.getAnnotatedAt();
	}
	
	public String toString() {
		return "SolrAnnotation [solrAnnotationId_string:" + getAnnotationId() + ", annotatedAt:" + getAnnotatedAt() + 
				", resourceId:" + getResourceId() + ", language:" + getLanguage() + ", label:" + getLabel() + "]";
	}
}

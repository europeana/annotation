package eu.europeana.annotation.solr.model.internal;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.agent.impl.Person;
import eu.europeana.annotation.definitions.model.impl.AbstractAnnotation;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;

public class SolrAnnotationImpl extends AbstractAnnotation implements SolrAnnotation {

	private String annotationId_string;
	private String resourceId;
	private String label;
	private String tag_type;
	private String http_uri;
	private String language;


	/* (non-Javadoc)
	 * @see eu.europeana.annotation.solr.model.internal.SolrAnnotation#setAnnotationIdString(java.lang.String)
	 */
	@Override
	@Field("annotationId_string")
	public void setAnnotationIdString(String annotationId){
		int pos = annotationId.lastIndexOf("/");
//		System.out.println("annotationIdString() annotationId: " + annotationId + ", pos: " + pos);
		AnnotationId annoId = new BaseAnnotationId();
//		System.out.println("annotationIdString() annotationId.substring(0, pos): " + annotationId.substring(0, pos));
		annoId.setResourceId(annotationId.substring(0, pos));
//		System.out.println("annotationIdString() annotationId.substring(pos + 1): " + annotationId.substring(pos + 1));
		String annoNr = annotationId.substring(pos + 1);
		annoId.setAnnotationNr(Integer.parseInt(annoNr));
		
		setAnnotationId(annoId);
		this.annotationId_string = annotationId;
		this.resourceId = annotationId.substring(1, pos);
	}
	
	@Override
	public String getAnnotationIdString() {
		return annotationId_string;
//		return getAnnotationId().toString();
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
	@Field("tag_type")
	public void setTagType(String tag_type) {
		this.tag_type = tag_type;
	}
	
	@Override
	public String getTagType() {
		return tag_type;
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

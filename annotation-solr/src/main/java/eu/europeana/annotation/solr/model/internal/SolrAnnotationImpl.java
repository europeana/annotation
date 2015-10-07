package eu.europeana.annotation.solr.model.internal;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.beans.Field;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.body.impl.PlainTagBody;
import eu.europeana.annotation.definitions.model.factory.impl.AgentObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.BodyObjectFactory;
import eu.europeana.annotation.definitions.model.impl.AbstractAnnotation;
import eu.europeana.annotation.definitions.model.vocabulary.AgentTypes;
import eu.europeana.annotation.definitions.model.vocabulary.BodyTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public class SolrAnnotationImpl extends AbstractAnnotation implements SolrAnnotation {

	private String annotationId_string;
	private String resourceId;
	private String label;
	private String annotation_type;
	private String http_uri;
	private String language;
	
	@Override
	@Field("tag_id")
	public void setTagId(String id) {
		if (super.getBody() == null) {
			Body body = BodyObjectFactory.getInstance().createModelObjectInstance(
					BodyTypes.SEMANTIC_TAG.name());
			((PlainTagBody) body).setTagId(id);
			super.setBody(body);
		} else {
			((PlainTagBody) super.getBody()).setTagId(id);
		}
//		super.setId(id);
	}

	public String getTagId() {
		String res = "";
		if (getBody() != null && ((PlainTagBody) super.getBody()).getTagId() != null) 
			res = ((PlainTagBody) super.getBody()).getTagId();
		return res;
	}

	public List<String> getBodyType() {
//		public String getBodyType() {
//		String res = "";
		
//		if (getBody() != null && getBody().getType() != null) 
//			res = getBody().getType();
//		return res;
		return getBody().getType();
	}

	@Field("body_internaltype")
	public void setBodyInternalType(String bodyInternalType) {
		
		if (super.getBody() == null) {
			Body body = BodyObjectFactory.getInstance().createModelObjectInstance(
					bodyInternalType);
			super.setBody(body);
		}
		
		super.getBody().setInternalType(bodyInternalType);
	}

	public String getBodyInternalType() {	
		return super.getBody().getInternalType();
	}

	@Field("body_type")
	public void setBodyType(List<String> bodyType) {
		if (super.getBody() != null)
			super.getBody().setType(bodyType);
		//this.bodyType = bodyType;
	}
	
	public String getBodyValue() {
		String res = "";
		if (getBody() != null && getBody().getValue() != null) 
			res = getBody().getValue();
		return res;
//		return super.getBody().getValue();
	}

	@Field("body_value")
	public void setBodyValue(String bodyValue) {
//		if (super.getBody() == null) {
//			Body body = BodyObjectFactory.getInstance().createModelObjectInstance(
//					BodyTypes.SEMANTIC_TAG.name());
//			body.setValue(bodyValue);
//			super.setBody(body);
//		} else {
		if (super.getBody() != null)
			super.getBody().setValue(bodyValue);
		//}
//		this.bodyValue = bodyValue;
	}

//	@Field("*_multilingual")
//	protected Map<String, String> multiLingual;

	public Map<String, String> getMultilingual() {
		Map<String, String> res = new HashMap<String, String>();
		if (getBody() != null && getBody().getMultilingual() != null) 
			res = getBody().getMultilingual();
		return res;
//		return multiLingual;
	}

	@Field("*_multilingual")
	public void setMultilingual(Map<String, String> multilingual) {
//		if (super.getBody() == null) {
//			Body body = BodyObjectFactory.getInstance().createModelObjectInstance(
//					BodyTypes.SEMANTIC_TAG.name());
//			body.setMultilingual(multilingual);
//			super.setBody(body);
//		} else {
		if (super.getBody() != null) 
			super.getBody().setMultilingual(multilingual);
//		}
//		this.multiLingual = multiLingual;
	}
	
	/**
	 * This method adds a new language/label association to the 
	 * multilingual map.
	 * @param language
	 * @param label
	 */
	public void addLabelInMapping(String language, String label) {
		getMultilingual().put(language + "_" + WebAnnotationFields.MULTILINGUAL, label);
//	    if(this.multiLingual == null) {
//	        this.multiLingual = new HashMap<String, String>();
//	    }
//	    this.multiLingual.put(language + "_multilingual", label);
	}

	/* (non-Javadoc)
	 * @see eu.europeana.annotation.solr.model.internal.SolrAnnotation#setAnnotationIdString(java.lang.String)
	 */
	@Override
	@Field("annotationId_string")
	public void setAnnotationIdString(String annotationId){
		int pos = annotationId.lastIndexOf("/");
		AnnotationId annoId = parse(annotationId);
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
		Agent creator = AgentObjectFactory.getInstance().createModelObjectInstance(
				AgentTypes.SOFTWARE.name());
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
	public void setAnnotationType(String annotation_type) {
		this.annotation_type = annotation_type;
	}
	
	@Override
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
	
	@Override
	public void setDefaultMotivation() {
		//setMotivation(MotivationTypes.TAGGING.getOaType());
		throw new RuntimeException("method not supported yet");
	}

}

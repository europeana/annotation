package eu.europeana.annotation.definitions.model.impl;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.resource.style.Style;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public abstract class AbstractAnnotation implements Annotation {

	protected AnnotationId annotationId = null;
	private String type;
	private String internalType;
	private Date annotatedAt;
	private Agent annotatedBy;
	private Body body;
	private Target target;
	private String motivation;
	private Date serializedAt;
	private Agent serializedBy;
	private Style styledBy;	
	protected MotivationTypes motivationType;	
	private boolean disabled;	
	private String sameAs;
	private String equivalentTo;
	
	
	public AbstractAnnotation(){
		super();
	}
	
	@Override
	public boolean equals(Object other) {
	    if (!(other instanceof Annotation)) {
	        return false;
	    }

	    Annotation that = (Annotation) other;

	    boolean res = true;
	    
	    /**
	     * equality check for all relevant fields.
	     */
	    if ((this.getAnnotationId() != null) && (that.getAnnotationId() != null) &&
	    		(this.getAnnotationId().getAnnotationNr() != null) && (that.getAnnotationId().getAnnotationNr() != null) &&
//	    		(this.getAnnotationId().getResourceId() != null) && (that.getAnnotationId().getResourceId() != null) &&
	    		(this.getAnnotationId().getProvider() != null) && (that.getAnnotationId().getProvider() != null) &&
	    		(!this.getAnnotationId().getAnnotationNr().equals(that.getAnnotationId().getAnnotationNr())
//	    		|| !this.getAnnotationId().getResourceId().equals(that.getAnnotationId().getResourceId())
	    		)) {
	    	System.out.println("Annotation objects have different 'annotationId' objects.");
	    	res = false;
	    }
	    
	    if ((this.getType() != null) && (that.getType() != null) &&
	    		(!this.getType().equals(that.getType()))) {
	    	System.out.println("Annotation objects have different 'type' fields.");
	    	res = false;
	    }
	    
	    if ((this.getAnnotatedAt() != null) && (that.getAnnotatedAt() != null) &&
	    		(!this.getAnnotatedAt().equals(that.getAnnotatedAt()))) {
	    	System.out.println("Annotation objects have different 'annotatedAt' fields.");
	    	res = false;
	    }
	    
	    if ((this.getAnnotatedBy() != null) && (that.getAnnotatedBy() != null) &&
	    		(!this.getAnnotatedBy().equals(that.getAnnotatedBy()))) {
	    	System.out.println("Annotation objects have different 'annotatedBy' objects.");
	    	res = false;
	    }
	    
	    if ((this.getBody() != null) && (that.getBody() != null) &&
	    		(!this.getBody().equals(that.getBody()))) {
	    	System.out.println("Annotation objects have different Body objects.");
	    	res = false;
	    }
	    
	    if ((this.getTarget() != null) && (that.getTarget() != null) &&
	    		(!this.getTarget().equals(that.getTarget()))) {
	    	System.out.println("Annotation objects have different Target objects.");
	    	res = false;
	    }
	    
	    if ((this.getMotivation() != null) && (that.getMotivation() != null) &&
	    		(!this.getMotivation().equals(that.getMotivation()))) {
	    	System.out.println("Annotation objects have different 'motivation' fields.");
	    	res = false;
	    }
	    
	    if ((this.getSerializedAt() != null) && (that.getSerializedAt() != null) &&
	    		(!this.getSerializedAt().equals(that.getSerializedAt()))) {
	    	System.out.println("Annotation objects have different 'serializedAt' fields.");
	    	res = false;
	    }
	    
	    if ((this.getSerializedBy() != null) && (that.getSerializedBy() != null) &&
	    		(!this.getSerializedBy().equals(that.getSerializedBy()))) {
	    	System.out.println("Annotation objects have different 'serializedBy' objects.");
	    	res = false;
	    }
	    
	    if ((this.getStyledBy() != null) && (that.getStyledBy() != null) &&
	    		(!this.getStyledBy().equals(that.getStyledBy()))) {
	    	System.out.println("Annotation objects have different 'styledBy' objects.");
	    	res = false;
	    }
	    
	    return res;
	}
	
	public boolean equalsContent(Object other) {
	    if (!(other instanceof Annotation)) {
	        return false;
	    }

	    Annotation that = (Annotation) other;

	    boolean res = true;
	    
	    /**
	     * equality check for all relevant fields.
	     */
	    if ((this.getType() != null) && (that.getType() != null) &&
	    		(!this.getType().equals(that.getType()))) {
	    	System.out.println("Annotation objects have different 'type' fields.");
	    	res = false;
	    }
	    
	    if ((this.getAnnotatedAt() != null) && (that.getAnnotatedAt() != null) &&
	    		(!this.getAnnotatedAt().equals(that.getAnnotatedAt()))) {
	    	System.out.println("Annotation objects have different 'annotatedAt' fields.");
	    	res = false;
	    }
	    
	    if ((this.getAnnotatedBy() != null) && (that.getAnnotatedBy() != null) &&
	    		(!this.getAnnotatedBy().equalsContent(that.getAnnotatedBy()))) {
	    	System.out.println("Annotation objects have different 'annotatedBy' objects.");
	    	res = false;
	    }
	    
	    if ((this.getBody() != null) && (that.getBody() != null) &&
	    		(!this.getBody().equalsContent(that.getBody()))) {
	    	System.out.println("Annotation objects have different Body objects.");
	    	res = false;
	    }
	    
	    if ((this.getTarget() != null) && (that.getTarget() != null) &&
	    		(!this.getTarget().equalsContent(that.getTarget()))) {
	    	System.out.println("Annotation objects have different Target objects.");
	    	res = false;
	    }
	    
	    if ((this.getMotivation() != null) && (that.getMotivation() != null) &&
	    		(!this.getMotivation().equals(that.getMotivation()))) {
	    	System.out.println("Annotation objects have different 'motivation' fields.");
	    	res = false;
	    }
	    
	    if ((this.getSerializedAt() != null) && (that.getSerializedAt() != null) &&
	    		(!this.getSerializedAt().equals(that.getSerializedAt()))) {
	    	System.out.println("Annotation objects have different 'serializedAt' fields.");
	    	res = false;
	    }
	    
	    if ((this.getSerializedBy() != null) && (that.getSerializedBy() != null) &&
	    		(!this.getSerializedBy().equalsContent(that.getSerializedBy()))) {
	    	System.out.println("Annotation objects have different 'serializedBy' objects.");
	    	res = false;
	    }
	    
	    if ((this.getStyledBy() != null) && (that.getStyledBy() != null) &&
	    		(!this.getStyledBy().equalsContent(that.getStyledBy()))) {
	    	System.out.println("Annotation objects have different 'styledBy' objects.");
	    	res = false;
	    }
	    
	    return res;
	}
	
	/**
	 * This method converts annotationId_string to AnnotaionId object.
	 * @param annotationId The annotationId_string
	 * @return AnnotationId object
	 */
	public AnnotationId parse(String annotationId){
//		int pos = annotationId.lastIndexOf("/");

//		System.out.println("annotationIdString() annotationId: " + annotationId + ", pos: " + pos);
		AnnotationId annoId = new BaseAnnotationId();
		if (StringUtils.isNotEmpty(annotationId)) {
	        String[] arrValue = annotationId.split(WebAnnotationFields.SLASH);
	        if (arrValue.length >= WebAnnotationFields.MIN_ANNOTATION_ID_COMPONENT_COUNT) {
	//		System.out.println("annotationIdString() annotationId.substring(0, pos): " + annotationId.substring(0, pos));
//			annoId.setResourceId(annotationId.substring(0, pos));
	        	//computed from the end of the url
	        	//int collectionPosition = arrValue.length - 4;
	        	//computed from the end of the url
	        	//int objectPosition = arrValue.length - 3;
	        	//computed from the end of the url
	        	int providerPosition = arrValue.length - 2;
	        	//computed from the end of the url
	        	int annotationNrPosition = arrValue.length - 1;
					        	
				//String collection = arrValue[collectionPosition];
	        	//String object     = arrValue[objectPosition];
//				if (StringUtils.isNotEmpty(collection) && StringUtils.isNotEmpty(object))
//					annoId.setResourceId((new AnnotationIdHelper()).createResourceId(collection, object));
	//		System.out.println("annotationIdString() annotationId.substring(pos + 1): " + annotationId.substring(pos + 1));
//				String annoNr = annotationId.substring(pos + 1);
//				annoId.setAnnotationNr(Integer.parseInt(annoNr));
				String provider = arrValue[providerPosition];
				if (StringUtils.isNotEmpty(provider))
					annoId.setProvider(provider);
				String annotationNrStr = arrValue[annotationNrPosition];
				if (StringUtils.isNotEmpty(annotationNrStr))
					annoId.setAnnotationNr(Long.parseLong(annotationNrStr));
	        }
		}
		return annoId;
	}

	@Override
	public AnnotationId getAnnotationId() {
		return annotationId;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
		
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
	public Date getAnnotatedAt() {
		return annotatedAt;
	}

	@Override
	public Agent getAnnotatedBy() {
		return annotatedBy;
	}

	@Override
	public void setAnnotatedBy(Agent annotatedBy) {
		this.annotatedBy = annotatedBy;
	}

	@Override
	public Body getBody() {
		return body;
	}

	@Override
	public void setBody(Body body) {
		this.body = body;
	}

	@Override
	public Target getTarget() {
		return target;
	}

	@Override
	public void setTarget(Target target) {
		this.target = target;
	}

	@Override
	public String getMotivation() {
		return motivation;
	}

	@Override
	public MotivationTypes getMotivationType() {
		motivationType = MotivationTypes.UNKNOWN;
		if(getMotivation() == null)
			return null;
		for(MotivationTypes element : MotivationTypes.values()){
			if(element.getOaType().equals(getMotivation()))
				return element;
		}
		
		return  MotivationTypes.UNKNOWN;		
	}
	
	@Override
	public void setMotivation(String motivation) {
		this.motivation = motivation;
	}

	@Override
	public Date getSerializedAt() {
		return serializedAt;
	}

	
	@Override
	public Style getStyledBy() {
		return styledBy;
	}

	@Override
	public void setStyledBy(Style styledBy) {
		this.styledBy = styledBy;
	}

	@Override
	public Agent getSerializedBy() {
		return serializedBy;
	}

	@Override
	public void setSerializedBy(Agent serializedBy) {
		this.serializedBy = serializedBy;
	}

	@Override
	public void setAnnotatedAt(Date annotatedAt) {
		this.annotatedAt = annotatedAt;
	}
	
	@Override
	public void setSerializedAt(Date serializedAt) {
		this.serializedAt = serializedAt;
	}
	
	public void setAnnotationId(AnnotationId annotationId) {
		this.annotationId = annotationId;
	}
	
	@Override
	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
		
	public String getSameAs() {
		return sameAs;
	}

	public void setSameAs(String sameAs) {
		this.sameAs = sameAs;
	}

	public String getEquivalentTo() {
		return equivalentTo;
	}

	public void setEquivalentTo(String equivalentTo) {
		this.equivalentTo = equivalentTo;
	}

	@Override
	public String toString() {
		String res = "### Annotation ###\n";
		if (annotationId != null) 
			res = res + "\t" + "annotationId:" + annotationId.toString() + "\n";
		if (type != null) 
			res = res + "\t" + "type:" + type + "\n";
		if (annotatedAt != null) 
			res = res + "\t" + "annotatedAt:" + annotatedAt + "\n";
		if (annotatedBy != null) 
			res = res + "\t" + "annotatedBy:" + annotatedBy.toString() + "\n";
		if (serializedBy != null) 
			res = res + "\t" + "serializedBy:" + serializedBy.toString() + "\n";
		if (styledBy != null) 
			res = res + "\t" + "styledBy:" + styledBy.toString() + "\n";
		if (motivation != null) 
			res = res + "\t" + "motivation:" + motivation.toString() + "\n";
		if (target != null) 
			res = res + "\t" + "target:" + target.toString() + "\n";
		if (body != null) 
			res = res + "\t" + "body:" + body.toString() + "\n";
		if (StringUtils.isNotEmpty(sameAs)) 
			res = res + "\t" + "sameAs:" + sameAs + "\n";
		if (StringUtils.isNotEmpty(equivalentTo)) 
			res = res + "\t" + "equivalentTo:" + equivalentTo + "\n";
		return res;
	}	
	
}

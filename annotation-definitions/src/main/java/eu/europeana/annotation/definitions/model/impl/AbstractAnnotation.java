package eu.europeana.annotation.definitions.model.impl;

import java.util.Date;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.resource.style.Style;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;

public abstract class AbstractAnnotation implements Annotation {

	protected AnnotationId annotationId = null;
	private String type;
	//private Long annotatedAtTs;
	private Date annotatedAt;
	private Agent annotatedBy;
	private Body body;
	private Target target;
	private String motivatedBy;
	//private Long serializedAtTs;
	private Date serializedAt;
	
	private Agent serializedBy;
	private Style styledBy;
	
	
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
	    		(this.getAnnotationId().getResourceId() != null) && (that.getAnnotationId().getResourceId() != null) &&
	    		(!this.getAnnotationId().getAnnotationNr().equals(that.getAnnotationId().getAnnotationNr())
	    		|| !this.getAnnotationId().getResourceId().equals(that.getAnnotationId().getResourceId()))) {
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
	    
	    if ((this.getMotivatedBy() != null) && (that.getMotivatedBy() != null) &&
	    		(!this.getMotivatedBy().equals(that.getMotivatedBy()))) {
	    	System.out.println("Annotation objects have different 'motivatedBy' fields.");
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
	public String getMotivatedBy() {
		return motivatedBy;
	}

	@Override
	public MotivationTypes getMotivationType() {
		if(getMotivatedBy() == null)
			return null;
		
		return MotivationTypes.valueOf(getMotivatedBy());		
	}
	
	@Override
	public void setMotivatedBy(String motivatedBy) {
		this.motivatedBy = motivatedBy;
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

//	@Override
//	public Long getAnnotatedAtTs() {
//		return annotatedAt.getTime();
//	}
//
//	@Override
//	public void setAnnotatedAtTs(Long annotatedAtTs) {
//		this.annotatedAt = new Date(annotatedAtTs);
//	}

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
		if (motivatedBy != null) 
			res = res + "\t" + "motivatedBy:" + motivatedBy.toString() + "\n";
		if (target != null) 
			res = res + "\t" + "target:" + target.toString() + "\n";
		if (body != null) 
			res = res + "\t" + "body:" + body.toString() + "\n";
		return res;
	}	
	
}

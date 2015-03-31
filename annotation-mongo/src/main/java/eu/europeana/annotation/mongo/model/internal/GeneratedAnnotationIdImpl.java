package eu.europeana.annotation.mongo.model.internal;

import org.apache.commons.lang.StringUtils;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;

/**
 * This class is used to generate the Annotation IDs in form of europeanaId/annotationNr, where the 
 * annotation numbers are generated using sequencies 
 * 
 * @author Sergiu Gordea 
 *
 */
@Entity(value="annotationNrGenerator", noClassnameStored=true)
public class GeneratedAnnotationIdImpl implements AnnotationId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4554805997975526594L;
	@Id
	private String resourceId;
	private Integer annotationNr;
	
	public static final String SEQUENCE_COLUMN_NAME = "annotationNr";
	
	/**
	 * This constructor must be use only by morphia 
	 */
	protected GeneratedAnnotationIdImpl(){
		
	}
	
	public GeneratedAnnotationIdImpl(String europeanaId){
		this.resourceId = europeanaId;
	}
	
	public GeneratedAnnotationIdImpl(String europeanaId, Integer annotationNr){
		this.resourceId = europeanaId;
		this.annotationNr = annotationNr;
	}
	
	
	@Override
	public Integer getAnnotationNr() {
		return annotationNr;
	}

	@Override
	public void setAnnotationNr(Integer nr) {
		this.annotationNr = nr;
	}

	@Override
	public String getResourceId() {
		return resourceId;
	}

	@Override
	public boolean equals(Object obj) {
		//europeana id and annotation number must not be null
		if(obj instanceof AnnotationId 
				&& this.getResourceId() != null && StringUtils.isNotEmpty(this.getProvider()) && this.getAnnotationNr() != null
				&& this.getResourceId().equals(((AnnotationId)obj).getResourceId())
				&& this.getProvider().equals(((AnnotationId)obj).getProvider())
				&& this.getAnnotationNr().equals(((AnnotationId)obj).getAnnotationNr()))
			return true;
				
		return false;
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public String toString() {
		return getResourceId() + WebAnnotationFields.SLASH + getProvider() 
				+ WebAnnotationFields.SLASH + getAnnotationNr();
	}
	
	@Override
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
		
	}

	@Override
	public String getProvider() {
		return null;
	}

	@Override
	/**
	 * not used as ids are generated only for own annotations (webanno as provider)  
	 */
	public void setProvider(String provider) {
		//this.provider = provider;		
	} 
}

package eu.europeana.annotation.mongo.model.internal;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

import eu.europeana.annotation.definitions.model.AnnotationId;

/**
 * This class is used to generate the Annotation IDs in form of europeanaId/annotationNr, where the 
 * annotation numbers are generated using sequencies 
 * 
 * @author Sergiu Gordea 
 *
 */
@Entity(value="annotationNrGenerator", noClassnameStored=true)
public class GeneratedAnnotationIdImpl implements AnnotationId{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4554805997975526594L;
	@Id
	private String europeanaId;
	private Integer annotationNr;
	
	public static final String SEQUENCE_COLUMN_NAME = "annotationNr";
	
	/**
	 * This constructor must be use only by morphia 
	 */
	protected GeneratedAnnotationIdImpl(){
		
	}
	
	public GeneratedAnnotationIdImpl(String europeanaId){
		this.europeanaId = europeanaId;
	}
	
	public GeneratedAnnotationIdImpl(String europeanaId, Integer annotationNr){
		this.europeanaId = europeanaId;
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
	public String getEuropeanaId() {
		return europeanaId;
	}

	@Override
	public boolean equals(Object obj) {
		//europeana id and annotation number must not be null
		if(obj instanceof AnnotationId 
				&& this.getEuropeanaId() != null && this.getAnnotationNr() != null
				&& this.getEuropeanaId().equals(((AnnotationId)obj).getEuropeanaId())
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
		return getEuropeanaId()+"/"+getAnnotationNr();
	}
}

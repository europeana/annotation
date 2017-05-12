package eu.europeana.annotation.mongo.model.internal;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;

/**
 * This class is used to generate the Annotation IDs in form of europeanaId/annotationNr, where the 
 * annotation numbers are generated using sequencies 
 * 
 * @author Sergiu Gordea 
 *
 */
//TODO: check if possible to extend the BaseAnnotationId class
@Entity(value="annotationNrGenerator", noClassnameStored=true)
public class GeneratedAnnotationIdImpl extends BaseAnnotationId implements AnnotationId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4554805997975526594L;
	
//	private String resourceId;
//	private String identifier;
	@Id
	private String id;
	private Long annotationNr;
	//not used, just marked as transient for morphia, the field in the upper class will be used to store information
	
	public Long getAnnotationNr() {
		return annotationNr;
	}

	public void setAnnotationNr(Long annotationNr) {
		this.annotationNr = annotationNr;
	}

	public static final String SEQUENCE_COLUMN_NAME = "annotationNr";
	
	/**
	 * This constructor must be use only by morphia 
	 */
	protected GeneratedAnnotationIdImpl(){
		
	}
	
	/**
	 * 
	 * @param baseUrl
	 * @param provider
	 * @param identifier - must be a long number
	 */
	public GeneratedAnnotationIdImpl(String baseUrl, String provider, String identifier){
		this(baseUrl, provider, Long.parseLong(identifier));
	}
	
	public GeneratedAnnotationIdImpl(String baseUrl, String provider, Long annotationNr){
		super(null, provider, null);
		this.id=provider;
		this.annotationNr = annotationNr;
		this.setHttpUrl(toHttpUrl());
	}
	
	@Override
	public String getIdentifier() {
		return ""+getAnnotationNr();
	}

	@Override
	public void setIdentifier(String identifier) {
		setAnnotationNr(Long.parseLong(identifier));
	}
	
}

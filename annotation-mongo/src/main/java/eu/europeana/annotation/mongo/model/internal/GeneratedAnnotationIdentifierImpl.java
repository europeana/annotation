package eu.europeana.annotation.mongo.model.internal;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;

/**
 * This class is used to generate the Annotation IDs in form of europeanaId/annotationNr, where the 
 * annotation numbers are generated using sequencies 
 * 
 * @author Sergiu Gordea 
 *
 */
@Entity(value="annotationNrGenerator", noClassnameStored=true)
public class GeneratedAnnotationIdentifierImpl {
	
	@Id
	private String id;
	private long annotationNr;
	
	public long getAnnotationNr() {
		return annotationNr;
	}

	public void setAnnotationNr(long annotationNr) {
		this.annotationNr = annotationNr;
	}

	public static final String SEQUENCE_COLUMN_NAME = "annotationNr";
	
	/**
	 * This constructor must be use only by morphia 
	 */
	protected GeneratedAnnotationIdentifierImpl(){
		
	}
	
	public GeneratedAnnotationIdentifierImpl(long annotationNr){
	    this(WebAnnotationFields.DEFAULT_PROVIDER, annotationNr);
	}

	public GeneratedAnnotationIdentifierImpl(String id, long annotationNr){
		super();
		this.id = id;
		this.annotationNr = annotationNr;
	}
	
}

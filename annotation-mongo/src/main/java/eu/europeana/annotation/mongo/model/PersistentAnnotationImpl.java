package eu.europeana.annotation.mongo.model;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Index;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Indexes;
import com.google.code.morphia.annotations.Polymorphic;
import com.google.code.morphia.annotations.Transient;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.AnnotationTypes;
import eu.europeana.annotation.mongo.model.internal.GeneratedAnnotationIdImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.corelib.db.entity.nosql.abstracts.NoSqlEntity;

@Entity("annotation")
@Polymorphic
@Indexes( @Index("europeanaId, annotationNr"))
public class PersistentAnnotationImpl implements PersistentAnnotation, NoSqlEntity {

	@Id
	private ObjectId id;
	@Transient
	private AnnotationId annotationId = null;
	@Indexed(unique = false)
	private String europeanaId;
	private Integer annotationNr;
	
	@Indexed(unique = false)
	private String creator;
	
	private Long creationTimestamp;
	private Long lastUpdateTimestamp;
	private String[] visibility;
	private String type;
	

	public PersistentAnnotationImpl() {
		super();
	}

	public PersistentAnnotationImpl(AnnotationId annotationId) {
		this();
		this.annotationId = annotationId;
	}

	public AnnotationId getAnnotationId() {
		//ensure synchronization between europeanaId and annotationId
		//annotationId == null || europeanaId != annotationid.europeanaId 
		if(getEuropeanaId() != null && annotationId == null
				|| getEuropeanaId() != null && !getEuropeanaId().equals(annotationId.getEuropeanaId()))
			resetAnnotationId(europeanaId);
		
		
		return annotationId;
	}

	public void setAnnotationId(AnnotationId annotationId) {
		this.annotationId = annotationId;
		this.europeanaId = annotationId.getEuropeanaId();
		this.annotationNr = annotationId.getAnnotationNr();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.europeana.annotation.model.PersistantTag#getType()
	 */
	@Override
	public String getType() {
		return type;
	}

	/**
	 * @see AnnotationTypes
	 * @param type 
	 */
	public void setType(String type) {
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.europeana.annotation.model.PersistantTag#getVisibility()
	 */
	@Override
	public String[] getVisibility() {
		return visibility;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.annotation.model.PersistantTag#setVisibility(java.lang.String
	 * [])
	 */
	@Override
	public void setVisibility(String[] visibility) {
		this.visibility = visibility;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.europeana.annotation.model.PersistantTag#getCreator()
	 */
	@Override
	public String getCreator() {
		return creator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.annotation.model.PersistantTag#setCreator(java.lang.String)
	 */
	@Override
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.europeana.annotation.model.PersistantTag#getCreationTimpestamp()
	 */
	@Override
	public Long getCreationTimestamp() {
		return creationTimestamp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.annotation.model.PersistantTag#setCreationTimestamp(java
	 * .lang.Long)
	 */
	@Override
	public void setCreationTimestamp(Long creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.annotation.model.PersistantTag#getLastUpdateTimestamp()
	 */
	@Override
	public Long getLastUpdateTimestamp() {
		return lastUpdateTimestamp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.annotation.model.PersistantTag#setLastUpdateTimestamp(java
	 * .lang.Long)
	 */
	@Override
	public void setLastUpdateTimestamp(Long lastUpdateTimestamp) {
		this.lastUpdateTimestamp = lastUpdateTimestamp;
	}

	public String getEuropeanaId() {
		return europeanaId;
	}

	
	public void setEuropeanaId(String europeanaId) {
		this.europeanaId = europeanaId;
		resetAnnotationId(europeanaId);		
	}

	private void resetAnnotationId(String europeanaId) {
		//the generated annotation id is immutable
		setAnnotationId(new GeneratedAnnotationIdImpl(europeanaId, this.annotationNr));
	}

	public Integer getAnnotationNr() {
		return annotationNr;
	}

	public void setAnnotationNr(Integer annotationNr) {
		this.annotationNr = annotationNr;
		//in the case that setEuropeanaId is called previous to setAnnotationNr
		if(getAnnotationId() != null)
			getAnnotationId().setAnnotationNr(annotationNr);
	}

	@Override
	public ObjectId getId() {
		return id;
	}

}
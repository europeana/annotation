package eu.europeana.annotation.mongo.service;

import java.util.List;

import eu.europeana.annotation.definitions.exception.AnnotationValidationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.ImageAnnotation;
import eu.europeana.annotation.definitions.model.SemanticTag;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.corelib.db.service.abstracts.AbstractNoSqlService;

public interface PersistentAnnotationService extends AbstractNoSqlService<PersistentAnnotation, String>{

	public abstract ImageAnnotation store(ImageAnnotation object) throws AnnotationValidationException;

	public abstract SemanticTag store(SemanticTag object) throws AnnotationValidationException;

	public abstract Annotation store(Annotation object) throws AnnotationValidationException;

	public List<? extends Annotation> getAnnotationList(String europeanaId);

	public PersistentAnnotation getAnnotation(String europeanaId, Integer annotationNr);
}

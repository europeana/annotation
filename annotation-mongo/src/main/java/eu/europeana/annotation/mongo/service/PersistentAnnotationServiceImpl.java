package eu.europeana.annotation.mongo.service;

import java.util.List;

import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryResults;

import eu.europeana.annotation.definitions.exception.AnnotationValidationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.ImageAnnotation;
import eu.europeana.annotation.definitions.model.SemanticTag;
import eu.europeana.annotation.mongo.dao.PersistentAnnotationDao;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.corelib.db.service.abstracts.AbstractNoSqlServiceImpl;

public class PersistentAnnotationServiceImpl extends AbstractNoSqlServiceImpl<PersistentAnnotation, String> implements PersistentAnnotationService {

	/**
	 * This method shouldn't be public but protected. Anyway, it is forced to be public by the supper implementation
	 * @throws AnnotationValidationException - see AnnotationValidationException.ERROR_NULL_EUROPEANA_ID
	 */
	@Override
	public PersistentAnnotation store(PersistentAnnotation object) {
		if(object.getCreationTimestamp() == null){
			object.setCreationTimestamp(System.currentTimeMillis());
			object.setLastUpdateTimestamp(object.getCreationTimestamp());
		}
		
		if(object.getEuropeanaId() == null)
			throw new AnnotationValidationException(AnnotationValidationException.ERROR_NULL_EUROPEANA_ID);
		
		if(object.getId() != null)
			throw new AnnotationValidationException(AnnotationValidationException.ERROR_NULL_EUROPEANA_ID);
			
		AnnotationId annoId = getAnnotationDao().generateNextAnnotationId(object.getEuropeanaId());
		object.setAnnotationId(annoId);
		
		return super.store(object);
	}

	@Override
	public Annotation store(Annotation object) {
		return this.store((PersistentAnnotation)object);
	}
	
	@Override
	public SemanticTag store(SemanticTag object) {
		return (SemanticTag) this.store((PersistentAnnotation)object);
	}
	
	@Override
	public ImageAnnotation store(ImageAnnotation object) {
		return (ImageAnnotation) this.store((PersistentAnnotation) object);
	}
	
	protected PersistentAnnotationDao<PersistentAnnotation, String> getAnnotationDao(){
		return (PersistentAnnotationDao<PersistentAnnotation, String>) getDao();
	}
	
	public List<? extends Annotation> getAnnotationList(String europeanaId) {
		Query<PersistentAnnotation> query = getAnnotationDao().createQuery();
		query.filter(PersistentAnnotation.FIELD_EUROPEANA_ID, europeanaId);
		QueryResults<? extends PersistentAnnotation> results = getAnnotationDao().find(query);
		return results.asList();		
	}

	@Override
	public PersistentAnnotation getAnnotation(String europeanaId, Integer annotationNr) {
		Query<PersistentAnnotation> query = getAnnotationDao().createQuery();
		query.filter(PersistentAnnotation.FIELD_EUROPEANA_ID, europeanaId);
		query.filter(PersistentAnnotation.FIELD_ANNOTATION_NR, annotationNr);
		
		return getAnnotationDao().findOne(query);
	}
}

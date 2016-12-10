package eu.europeana.annotation.mongo.service;

import java.util.List;

import org.bson.types.ObjectId;

import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryResults;
import com.mongodb.WriteResult;

import eu.europeana.annotation.definitions.exception.ConceptValidationException;
import eu.europeana.annotation.definitions.model.entity.Concept;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.AnnotationMongoRuntimeException;
import eu.europeana.annotation.mongo.exception.InvalidConceptException;
import eu.europeana.annotation.mongo.model.PersistentConceptImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentConcept;
import eu.europeana.corelib.db.service.abstracts.AbstractNoSqlServiceImpl;
import org.springframework.stereotype.Component;


@Component
public class PersistentConceptServiceImpl extends
		AbstractNoSqlServiceImpl<PersistentConcept, String> implements
		PersistentConceptService {

	@Override
	public PersistentConcept find(PersistentConcept concept) {
		Query<PersistentConcept> query = createQuery(concept);

		return getDao().findOne(query);
	}
	
	@Override
	public List<PersistentConcept> findAll(PersistentConcept concept)
			throws AnnotationMongoException {
		
		Query<PersistentConcept> query = createQuery(concept);
		return getDao().find(query).asList();

	}
	
	@Override
	public PersistentConcept findByID(String id) {
		return  getDao().findOne("_id", new ObjectId(id));
	}

	protected Query<PersistentConcept> createQuery(PersistentConcept concept) {
		Query<PersistentConcept> query = getDao().createQuery();
//		if(concept.getConceptType() != null)
//			query.filter(PersistentConcept.FIELD_CONCEPT_TYPE, concept.getConceptType());
		
		return query;
	}

	@Override
	public void remove(String id) {
		try{
			PersistentConcept concept = findByID(id);
			getDao().delete(concept);
			//make one of the following to work
			//getDao().deleteById(id);
			//super.remove(id);
		}catch(Exception e){
			throw new AnnotationMongoRuntimeException(e);
		}
	}
	
	@Override
	public void remove(PersistentConcept queryConcept) throws AnnotationMongoException {
		Query<PersistentConcept> createQuery = createQuery(queryConcept);
		WriteResult res = getDao().deleteByQuery(createQuery);
		validateDeleteResult(res);
	}

	@SuppressWarnings("deprecation")
	protected void validateDeleteResult(WriteResult res)
			throws AnnotationMongoException {
		int affected = res.getN();
		if(affected != 1 )
			throw new AnnotationMongoException("Delete operation Failed!" + res.getError(), res.getLastError().getException());
	}

	@Override
	public PersistentConcept update(PersistentConcept concept, String agent) throws InvalidConceptException {
		if (concept.getId() == null)
			throw new InvalidConceptException(InvalidConceptException.MESSAGE_NULL_ATTRIBUTE + "id");
		
//		concept.setLastUpdateTimestamp(System.currentTimeMillis());
		
//		validateConcept(concept);
		return store(concept);
	}

//	public boolean isSemanticConcept(PersistentConcept concept){
//		return ConceptTypes.isSemanticConcept(concept.getConceptType());
//	}
//	
//	public boolean isSimpleConcept(PersistentConcept concept){
//		return ConceptTypes.isSimpleConcept(concept.getConceptType());
//	}
	
	
	@Override
	public PersistentConcept create(PersistentConcept concept)
			throws AnnotationMongoException {
		
//		if (concept.getLastUpdatedBy() == null)
//			concept.setLastUpdatedBy(concept.getCreator());
//		
//		if (concept.getCreationTimestamp() == null)
//			concept.setCreationTimestamp(System.currentTimeMillis());
//		if (concept.getLastUpdateTimestamp() == null)
//			concept.setLastUpdateTimestamp(concept.getCreationTimestamp());
//		
//		if (concept.getConceptType() == null)
//			concept.setConceptTypeEnum(ConceptTypes.SIMPLE_TAG);
//
//		validateConcept(concept);

		return store(concept);
	}

	void validateConcept(PersistentConcept concept) throws InvalidConceptException {
//		if (concept.getCreator() == null)
//			throw new InvalidConceptException(
//					InvalidConceptException.MESSAGE_NULL_ATTRIBUTE + "creator");
//		if (concept.getLastUpdatedBy() == null)
//			throw new InvalidConceptException(
//					InvalidConceptException.MESSAGE_NULL_ATTRIBUTE + "lastUpdatedBy");
//		
//		if (concept.getCreationTimestamp() == null)
//			throw new InvalidConceptException(
//					InvalidConceptException.MESSAGE_NULL_ATTRIBUTE + "creationTimestamp");
//		
//		if (concept.getLastUpdateTimestamp() == null)
//			throw new InvalidConceptException(
//					InvalidConceptException.MESSAGE_NULL_ATTRIBUTE + "lastUpdatedTimeStamp");
//		
//		if (ConceptTypes.SEMANTIC_TAG.name().equals(concept.getConceptType())
//				&& concept.getHttpUri() == null)
//			throw new InvalidConceptException(
//					InvalidConceptException.MESSAGE_NULL_ATTRIBUTE + "httpUri");
	}

//	@Override
//	public PersistentConcept store(PersistentConcept concept) {
//		
//		try {
//			validateConcept(concept);
//			return super.store(concept);
//		} catch (InvalidConceptException e) {
//			throw new AnnotationMongoRuntimeException(e);
//		}
//		
//	}

	@Override
	public Concept store(Concept object) {
		Concept res = null;
		if(object instanceof PersistentConcept)
			res = this.store((PersistentConcept) object);
		else{
			PersistentConcept persistentObject = copyIntoPersistentConcept(object);
			return this.store(persistentObject); 
		}
		return res;
	}

	public PersistentConcept copyIntoPersistentConcept(Concept concept) {

		PersistentConceptImpl persistentConcept = new PersistentConceptImpl();
		persistentConcept.setPrefLabel(concept.getPrefLabel());
		persistentConcept.setAltLabel(concept.getAltLabel());
		persistentConcept.setHiddenLabel(concept.getHiddenLabel());
		persistentConcept.setBroader(concept.getBroader());
		persistentConcept.setNarrower(concept.getNarrower());
		persistentConcept.setNotation(concept.getNotation());
		persistentConcept.setType(concept.getType());
		persistentConcept.setRelated(concept.getRelated());
		persistentConcept.setUri(concept.getUri());
		return persistentConcept;
	}				
	
	@Override
	public Concept update(Concept object) {

		Concept res = null;

		PersistentConcept persistentConcept = (PersistentConcept) object;

		if (persistentConcept != null 
				&& persistentConcept.getId() != null 
				) {
			remove(persistentConcept.getId().toString());
			persistentConcept.setId(null);
			res = store(persistentConcept);
		} else {
			throw new ConceptValidationException(
					ConceptValidationException.ERROR_MISSING_ID);
		}

		return res;
	}

	@Override
	public PersistentConcept findByUrl(String url) {
		Query<PersistentConcept> query = getDao().createQuery();
		query.filter(PersistentConcept.FIELD_URI, url);

//		return getDao().findOne(query);
		QueryResults<? extends PersistentConcept> results = getDao()
				.find(query);
		List<? extends PersistentConcept> conceptList = results.asList();
		return conceptList.get(conceptList.size() - 1);
	}
	
}

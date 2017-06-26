package eu.europeana.annotation.mongo.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;

import com.mongodb.WriteResult;

import eu.europeana.annotation.definitions.model.vocabulary.TagTypes;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.AnnotationMongoRuntimeException;
import eu.europeana.annotation.mongo.exception.InvalidTagException;
import eu.europeana.annotation.mongo.model.internal.PersistentTag;
import eu.europeana.api.commons.nosql.service.impl.AbstractNoSqlServiceImpl;

public class PersistentTagServiceImpl extends
		AbstractNoSqlServiceImpl<PersistentTag, String> implements
		PersistentTagService {

//	@SuppressWarnings("unchecked")
//	protected PersistentTagDao<PersistentTag, String> getTagDao() {
//
//		return (PersistentTagDao<PersistentTag, String>) getDao();
//	}

	@Override
	public PersistentTag find(PersistentTag tag) {
		Query<PersistentTag> query = createQuery(tag);

		return getDao().findOne(query);
	}
	
	@Override
	public List<PersistentTag> findAll(PersistentTag tag)
			throws AnnotationMongoException {
		
		Query<PersistentTag> query = createQuery(tag);
		return getDao().find(query).asList();

	}
	
	@Override
	public PersistentTag findByID(String id) {
		return  getDao(). findOne("_id", new ObjectId(id));
	}

	protected Query<PersistentTag> createQuery(PersistentTag tag) {
		Query<PersistentTag> query = getDao().createQuery();
		if(tag.getTagType() != null)
			query.filter(PersistentTag.FIELD_TAG_TYPE, tag.getTagType());
		if(isSimpleTag(tag))
			query.filter(PersistentTag.FIELD_LABEL, tag.getLabel());
		if(isSemanticTag(tag))
			query.filter(PersistentTag.FIELD_HTTPURI, tag.getHttpUri());
		
		return query;
	}

	@Override
	public void remove(String id) {
		try{
			PersistentTag tag = findByID(id);
			getDao().delete(tag);
			//make one of the following to work
			//getDao().deleteById(id);
			//super.remove(id);
		}catch(Exception e){
			throw new AnnotationMongoRuntimeException(e);
		}
	}
	
	@Override
	public void remove(PersistentTag queryTag) throws AnnotationMongoException {
		Query<PersistentTag> createQuery = createQuery(queryTag);
		WriteResult res = getDao().deleteByQuery(createQuery);
		validateDeleteResult(res);
	}

	@SuppressWarnings("deprecation")
	protected void validateDeleteResult(WriteResult res)
			throws AnnotationMongoException {
		int affected = res.getN();
		if(affected != 1 )
			throw new AnnotationMongoException("Delete operation Failed!" + res);
	}

	@Override
	public PersistentTag update(PersistentTag tag, String agent) throws InvalidTagException {
		if (tag.getId() == null)
			throw new InvalidTagException(InvalidTagException.MESSAGE_NULL_ATTRIBUTE + "id");
		
		tag.setLastUpdatedBy(agent);
		tag.setLastUpdateTimestamp(System.currentTimeMillis());
		
		validateTag(tag);
		return store(tag);
	}

	public boolean isSemanticTag(PersistentTag tag){
		return TagTypes.isSemanticTag(tag.getTagType());
	}
	
	public boolean isSimpleTag(PersistentTag tag){
		return TagTypes.isSimpleTag(tag.getTagType());
	}
	
	
	@Override
	public PersistentTag create(PersistentTag tag)
			throws AnnotationMongoException {
		
		if (tag.getLastUpdatedBy() == null)
			tag.setLastUpdatedBy(tag.getCreator());
		
		if (tag.getCreationTimestamp() == null)
			tag.setCreationTimestamp(System.currentTimeMillis());
		if (tag.getLastUpdateTimestamp() == null)
			tag.setLastUpdateTimestamp(tag.getCreationTimestamp());
		
		if (tag.getTagType() == null)
			tag.setTagTypeEnum(TagTypes.SIMPLE_TAG);

		validateTag(tag);

		return store(tag);
	}

	private void validateTag(PersistentTag tag) throws InvalidTagException {
		if (tag.getCreator() == null)
			throw new InvalidTagException(
					InvalidTagException.MESSAGE_NULL_ATTRIBUTE + "creator");
		if (tag.getLastUpdatedBy() == null)
			throw new InvalidTagException(
					InvalidTagException.MESSAGE_NULL_ATTRIBUTE + "lastUpdatedBy");
		
		if (tag.getCreationTimestamp() == null)
			throw new InvalidTagException(
					InvalidTagException.MESSAGE_NULL_ATTRIBUTE + "creationTimestamp");
		
		if (tag.getLastUpdateTimestamp() == null)
			throw new InvalidTagException(
					InvalidTagException.MESSAGE_NULL_ATTRIBUTE + "lastUpdatedTimeStamp");
		
		if (TagTypes.SEMANTIC_TAG.name().equals(tag.getTagType())
				&& tag.getHttpUri() == null)
			throw new InvalidTagException(
					InvalidTagException.MESSAGE_NULL_ATTRIBUTE + "httpUri");
//		else if(!TagTypes.SEMANTIC_TAG.name().equals(tag.getTagType())
//				&& tag.getHttpUri() != null)
//			throw new InvalidTagException(
//					InvalidTagException.MESSAGE_WRONG_VALUE + " httpUri: " + tag.getHttpUri());
		
	}

	@Override
	public PersistentTag store(PersistentTag tag) {
		
		try {
			validateTag(tag);
			return super.store(tag);
		} catch (InvalidTagException e) {
			throw new AnnotationMongoRuntimeException(e);
		}
		
	}

	
	
}

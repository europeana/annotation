package eu.europeana.annotation.mongo.service;

import java.util.List;

import org.bson.types.ObjectId;

import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryResults;
import com.mongodb.WriteResult;

import eu.europeana.annotation.definitions.exception.WhitelistValidationException;
import eu.europeana.annotation.definitions.model.whitelist.Whitelist;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.AnnotationMongoRuntimeException;
import eu.europeana.annotation.mongo.exception.InvalidWhitelistException;
import eu.europeana.annotation.mongo.model.PersistentWhitelistImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentWhitelist;
import eu.europeana.corelib.db.service.abstracts.AbstractNoSqlServiceImpl;

public class PersistentWhitelistServiceImpl extends
		AbstractNoSqlServiceImpl<PersistentWhitelist, String> implements
		PersistentWhitelistService {

	@Override
	public PersistentWhitelist find(PersistentWhitelist whitelist) {
		Query<PersistentWhitelist> query = createQuery(whitelist);

		return getDao().findOne(query);
	}
	
	@Override
	public List<PersistentWhitelist> findAll(PersistentWhitelist whitelist)
			throws AnnotationMongoException {
		
		Query<PersistentWhitelist> query = createQuery(whitelist);
		return getDao().find(query).asList();

	}
	
	@Override
	public PersistentWhitelist findByID(String id) {
		return  getDao().findOne("_id", new ObjectId(id));
	}

	protected Query<PersistentWhitelist> createQuery(PersistentWhitelist whitelist) {
		Query<PersistentWhitelist> query = getDao().createQuery();
//		if(whitelist.getWhitelistType() != null)
//			query.filter(PersistentWhitelist.FIELD_whitelist_TYPE, whitelist.getWhitelistType());
		
		return query;
	}

	@Override
	public void remove(String id) {
		try{
			PersistentWhitelist whitelist = findByID(id);
			getDao().delete(whitelist);
			//make one of the following to work
			//getDao().deleteById(id);
			//super.remove(id);
		}catch(Exception e){
			throw new AnnotationMongoRuntimeException(e);
		}
	}
	
	@Override
	public void removeByUrl(String url) {
		Query<PersistentWhitelist> query = getDao().createQuery();
		query.filter(PersistentWhitelist.FIELD_HTTP_URL, url);

		WriteResult results = getDao()
				.deleteByQuery(query);
	}
	
	@Override
	public void removeAll() {
		try{
			getDao().deleteAll();
		}catch(Exception e){
			throw new AnnotationMongoRuntimeException(e);
		}
	}
	
	@Override
	public void remove(PersistentWhitelist queryWhitelist) throws AnnotationMongoException {
		Query<PersistentWhitelist> createQuery = createQuery(queryWhitelist);
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

	/*
	@Override
	public PersistentWhitelist update(PersistentWhitelist whitelist, String agent) throws InvalidWhitelistException {
		if (whitelist.getId() == null)
			throw new InvalidWhitelistException(InvalidWhitelistException.MESSAGE_NULL_ATTRIBUTE + "id");
		
//		whitelist.setLastUpdateTimestamp(System.currentTimeMillis());
		
//		validateWhitelist(whitelist);
		return store(whitelist);
	}
	*/

//	public boolean isSemanticWhitelist(PersistentWhitelist whitelist){
//		return WhitelistTypes.isSemanticWhitelist(whitelist.getWhitelistType());
//	}
//	
//	public boolean isSimpleWhitelist(PersistentWhitelist whitelist){
//		return WhitelistTypes.isSimpleWhitelist(whitelist.getWhitelistType());
//	}
	
	
	@Override
	public PersistentWhitelist create(PersistentWhitelist whitelist)
			throws AnnotationMongoException {
		
//		if (whitelist.getLastUpdatedBy() == null)
//			whitelist.setLastUpdatedBy(whitelist.getCreator());
//		
//		if (whitelist.getCreationTimestamp() == null)
//			whitelist.setCreationTimestamp(System.currentTimeMillis());
//		if (whitelist.getLastUpdateTimestamp() == null)
//			whitelist.setLastUpdateTimestamp(whitelist.getCreationTimestamp());
//		
//		if (whitelist.getWhitelistType() == null)
//			whitelist.setWhitelistTypeEnum(WhitelistTypes.SIMPLE_TAG);
//
//		validateWhitelist(whitelist);

		return store(whitelist);
	}

	void validateWhitelist(PersistentWhitelist whitelist) throws InvalidWhitelistException {
//		if (whitelist.getCreator() == null)
//			throw new InvalidWhitelistException(
//					InvalidWhitelistException.MESSAGE_NULL_ATTRIBUTE + "creator");
//		if (whitelist.getLastUpdatedBy() == null)
//			throw new InvalidWhitelistException(
//					InvalidWhitelistException.MESSAGE_NULL_ATTRIBUTE + "lastUpdatedBy");
//		
//		if (whitelist.getCreationTimestamp() == null)
//			throw new InvalidWhitelistException(
//					InvalidWhitelistException.MESSAGE_NULL_ATTRIBUTE + "creationTimestamp");
//		
//		if (whitelist.getLastUpdateTimestamp() == null)
//			throw new InvalidWhitelistException(
//					InvalidWhitelistException.MESSAGE_NULL_ATTRIBUTE + "lastUpdatedTimeStamp");
//		
//		if (WhitelistTypes.SEMANTIC_TAG.name().equals(whitelist.getWhitelistType())
//				&& whitelist.getHttpUri() == null)
//			throw new InvalidWhitelistException(
//					InvalidWhitelistException.MESSAGE_NULL_ATTRIBUTE + "httpUri");
	}

//	@Override
//	public PersistentWhitelist store(PersistentWhitelist whitelist) {
//		
//		try {
//			validateWhitelist(whitelist);
//			return super.store(whitelist);
//		} catch (InvalidWhitelistException e) {
//			throw new AnnotationMongoRuntimeException(e);
//		}
//		
//	}

	@Override
	public Whitelist store(Whitelist object) {
		Whitelist res = null;
		if(object instanceof PersistentWhitelist)
			res = this.store((PersistentWhitelist) object);
		else{
			PersistentWhitelist persistentObject = copyIntoPersistentWhitelist(object);
			return this.store(persistentObject); 
		}
		return res;
	}

	public PersistentWhitelist copyIntoPersistentWhitelist(Whitelist whitelist) {

		PersistentWhitelistImpl persistentWhitelist = new PersistentWhitelistImpl();
		persistentWhitelist.setHttpUrl(whitelist.getHttpUrl());
		persistentWhitelist.setName(whitelist.getName());
		persistentWhitelist.setStatus(whitelist.getStatus());
		persistentWhitelist.setCreationDate(whitelist.getCreationDate());
		persistentWhitelist.setLastUpdate(whitelist.getLastUpdate());
		persistentWhitelist.setEnableFrom(whitelist.getEnableFrom());
		persistentWhitelist.setDisableTo(whitelist.getDisableTo());
		return persistentWhitelist;
	}				
	
	@Override
	public Whitelist update(Whitelist object) {

		Whitelist res = null;

		PersistentWhitelist persistentWhitelist = (PersistentWhitelist) object;

		if (persistentWhitelist != null 
				&& persistentWhitelist.getId() != null 
				) {
			remove(persistentWhitelist.getId().toString());
			persistentWhitelist.setId(null);
			res = store(persistentWhitelist);
		} else {
			throw new WhitelistValidationException(
					WhitelistValidationException.ERROR_MISSING_ID);
		}

		return res;
	}

	@Override
	public PersistentWhitelist findByUrl(String url) {
		Query<PersistentWhitelist> query = getDao().createQuery();
		query.filter(PersistentWhitelist.FIELD_HTTP_URL, url);
//		query.filter(PersistentWhitelist.FIELD_URI, url);
//		query.filter(PersistentWhitelist.FIELD_HTTP_URL, url);

//		return getDao().findOne(query);
		QueryResults<? extends PersistentWhitelist> results = getDao()
				.find(query);
		List<? extends PersistentWhitelist> whitelistList = results.asList();
		return whitelistList.get(whitelistList.size() - 1);
	}

//	public List<? extends PersistentWhitelist> findAll() {
//	    
//	}
	
//	public List<? extends PersistentWhitelist> findAll() {
//		Query<PersistentWhitelist> query = getDao().createQuery();
//		QueryResults<? extends PersistentWhitelist> results = getDao()
//				.find(query);
//		List<? extends PersistentWhitelist> whitelistList = results.asList();
//		return whitelistList;
//	}	
}

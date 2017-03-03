package eu.europeana.annotation.mongo.service;

import java.util.List;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.google.code.morphia.query.Query;

import eu.europeana.annotation.definitions.model.authentication.Client;
import eu.europeana.annotation.mongo.dao.PersistentClientDao;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.model.internal.PersistentClient;
import eu.europeana.corelib.db.service.abstracts.AbstractNoSqlServiceImpl;

@Configuration
@EnableCaching
@Component
public class PersistentClientServiceImpl extends
		AbstractNoSqlServiceImpl<PersistentClient, String> implements
		PersistentClientService {

		
//	@Override
//	public PersistentWhitelistEntry findByID(String id) {
//		return  getDao().findOne("_id", new ObjectId(id));
//	}

	protected Query<PersistentClient> createQuery(PersistentClient client) {
		Query<PersistentClient> query = getDao().createQuery();
		return query;
	}

	
	@CachePut("client")
	@Override
	public List<? extends PersistentClient> getAll() {
		return (List<? extends PersistentClient>) findAll();
	}


	@Override
	public PersistentClient create(PersistentClient client) throws AnnotationMongoException {
		return super.store(client);
	}
	
	@SuppressWarnings("unchecked")
	protected PersistentClientDao<PersistentClient, String> getClientDao() {
		return (PersistentClientDao<PersistentClient, String>) getDao();
	}


	@Override
	public Client findByApiKey(String apiKey) {
		return getDao().findOne(PersistentClient.FIELD_CLIENT_ID, apiKey);
	}
	
//	@Override
//	public PersistentWhitelistEntry findByName(String name) {
//		Query<PersistentWhitelistEntry> query = getDao().createQuery();
//		query.filter(PersistentWhitelistEntry.FIELD_NAME, name);
//		QueryResults<? extends PersistentWhitelistEntry> results = getDao()
//				.find(query);
//		List<? extends PersistentWhitelistEntry> whitelistList = results.asList();
//		if (whitelistList.size() == 0)
//			return null;
//		return whitelistList.get(whitelistList.size() - 1);
//	}
}

package eu.europeana.corelib.db.service.mock.impl;

import java.io.Serializable;
import java.util.List;

import eu.europeana.corelib.db.entity.relational.ApiKeyImpl;
import eu.europeana.corelib.db.exception.DatabaseException;
import eu.europeana.corelib.db.exception.LimitReachedException;
import eu.europeana.corelib.db.service.ApiKeyService;
import eu.europeana.corelib.definitions.db.entity.relational.ApiKey;

public class MockApiKeyServiceImpl implements ApiKeyService {

	@Override
	public ApiKey store(ApiKey object) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(ApiKey object) throws DatabaseException {
		// TODO Auto-generated method stub

	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<ApiKey> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiKey findByID(Serializable id) throws DatabaseException {
		if("apidemo".equals(id)){
			ApiKey res =  new ApiKeyImpl();
			res.setApiKey("apidemo");
			res.setPrivateKey("apidemo");
			res.setApplicationName("mock");
			
			return res;
		}			
		return null;
	}

	@Override
	public List<ApiKey> findAllSortByDate(boolean asc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ApiKey> findAllSortByDate(boolean asc, int offset, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long checkReachedLimit(ApiKey apiKey) throws DatabaseException, LimitReachedException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void removeApiKey(Long userId, String apiKey) throws DatabaseException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateApplicationName(Long userId, String apiKey, String applicationName) throws DatabaseException {
		// TODO Auto-generated method stub

	}

	@Override
	public ApiKey createApiKey(String token, String email, String apiKey, String privateKey, Long limit, String appName,
			String username, String company, String country, String firstName, String lastName, String website,
			String address, String phone, String fieldOfWork) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

}

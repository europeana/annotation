package eu.europeana.annotation.mongo.dao;

import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import eu.europeana.annotation.mongo.model.PersistentApiWriteLockImpl;
import eu.europeana.api.commons.nosql.dao.impl.NosqlDaoImpl;

@Component
public class PersistentApiWriteLockDaoImpl extends NosqlDaoImpl<PersistentApiWriteLockImpl, String> implements PersistentApiWriteLockDao{

    @Autowired
	public PersistentApiWriteLockDaoImpl(Datastore datastore) {
		super(datastore, PersistentApiWriteLockImpl.class);
	}

}

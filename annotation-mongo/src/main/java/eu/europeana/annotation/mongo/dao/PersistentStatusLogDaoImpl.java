package eu.europeana.annotation.mongo.dao;

import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import eu.europeana.annotation.mongo.model.PersistentStatusLogImpl;
import eu.europeana.api.commons.nosql.dao.impl.NosqlDaoImpl;

@Component
public class PersistentStatusLogDaoImpl
extends NosqlDaoImpl<PersistentStatusLogImpl, String> implements PersistentStatusLogDao{

    @Autowired
	public PersistentStatusLogDaoImpl(Datastore datastore) {
		super(datastore, PersistentStatusLogImpl.class);
	}

}

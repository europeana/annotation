package eu.europeana.annotation.mongo.dao;

import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import eu.europeana.annotation.mongo.model.PersistentWhitelistImpl;
import eu.europeana.api.commons.nosql.dao.impl.NosqlDaoImpl;

@Component
public class PersistentWhitelistDaoImpl 
extends NosqlDaoImpl<PersistentWhitelistImpl, String> implements PersistentWhitelistDao{

    @Autowired
	public PersistentWhitelistDaoImpl(Datastore datastore) {
		super(datastore, PersistentWhitelistImpl.class);
	}

}

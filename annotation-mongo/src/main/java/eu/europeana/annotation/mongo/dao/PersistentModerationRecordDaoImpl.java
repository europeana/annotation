package eu.europeana.annotation.mongo.dao;

import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import eu.europeana.annotation.mongo.model.PersistentModerationRecordImpl;
import eu.europeana.api.commons.nosql.dao.impl.NosqlDaoImpl;

@Component
public class PersistentModerationRecordDaoImpl
extends NosqlDaoImpl<PersistentModerationRecordImpl, String> implements PersistentModerationRecordDao{

    @Autowired
	public PersistentModerationRecordDaoImpl(Datastore datastore) {
		super(datastore, PersistentModerationRecordImpl.class);
	}

}

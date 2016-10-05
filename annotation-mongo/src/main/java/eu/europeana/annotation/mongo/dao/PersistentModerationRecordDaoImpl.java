package eu.europeana.annotation.mongo.dao;

import java.io.Serializable;

import com.google.code.morphia.Datastore;

import eu.europeana.annotation.mongo.model.internal.PersistentModerationRecord;
import eu.europeana.corelib.db.dao.impl.NosqlDaoImpl;

public class PersistentModerationRecordDaoImpl <E extends PersistentModerationRecord, T extends Serializable>
extends NosqlDaoImpl<E, T> implements PersistentModerationRecordDao<PersistentModerationRecord, String>{

	public PersistentModerationRecordDaoImpl(Class<E> clazz, Datastore datastore) {
		super(datastore, clazz);
	}

}

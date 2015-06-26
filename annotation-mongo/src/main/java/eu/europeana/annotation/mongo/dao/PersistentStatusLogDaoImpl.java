package eu.europeana.annotation.mongo.dao;

import java.io.Serializable;

import com.google.code.morphia.Datastore;

import eu.europeana.annotation.mongo.model.internal.PersistentStatusLog;
import eu.europeana.corelib.db.dao.impl.NosqlDaoImpl;

public class PersistentStatusLogDaoImpl <E extends PersistentStatusLog, T extends Serializable>
extends NosqlDaoImpl<E, T> implements PersistentStatusLogDao<PersistentStatusLog, String>{

	public PersistentStatusLogDaoImpl(Class<E> clazz, Datastore datastore) {
		super(datastore, clazz);
	}

}

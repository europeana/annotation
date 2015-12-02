package eu.europeana.annotation.mongo.dao;

import java.io.Serializable;

import com.google.code.morphia.Datastore;

import eu.europeana.annotation.mongo.model.internal.PersistentWhitelistEntry;
import eu.europeana.corelib.db.dao.impl.NosqlDaoImpl;

public class PersistentWhitelistDaoImpl <E extends PersistentWhitelistEntry, T extends Serializable>
extends NosqlDaoImpl<E, T> implements PersistentWhitelistDao<PersistentWhitelistEntry, String>{

	public PersistentWhitelistDaoImpl(Class<E> clazz, Datastore datastore) {
		super(datastore, clazz);
	}

}

package eu.europeana.annotation.mongo.dao;

import java.io.Serializable;

import com.google.code.morphia.Datastore;

import eu.europeana.annotation.mongo.model.internal.PersistentWhitelist;
import eu.europeana.corelib.db.dao.impl.NosqlDaoImpl;

public class PersistentWhitelistDaoImpl <E extends PersistentWhitelist, T extends Serializable>
extends NosqlDaoImpl<E, T> implements PersistentWhitelistDao<PersistentWhitelist, String>{

	public PersistentWhitelistDaoImpl(Class<E> clazz, Datastore datastore) {
		super(datastore, clazz);
	}

}

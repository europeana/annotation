package eu.europeana.annotation.mongo.dao;

import java.io.Serializable;

import com.google.code.morphia.Datastore;

import eu.europeana.annotation.mongo.model.internal.PersistentProvider;
import eu.europeana.corelib.db.dao.impl.NosqlDaoImpl;

public class PersistentProviderDaoImpl <E extends PersistentProvider, T extends Serializable>
extends NosqlDaoImpl<E, T> implements PersistentProviderDao<PersistentProvider, String>{

	public PersistentProviderDaoImpl(Class<E> clazz, Datastore datastore) {
		super(datastore, clazz);
	}

}

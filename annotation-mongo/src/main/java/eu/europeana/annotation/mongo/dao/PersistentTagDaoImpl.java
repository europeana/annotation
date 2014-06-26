package eu.europeana.annotation.mongo.dao;

import java.io.Serializable;

import com.google.code.morphia.Datastore;

import eu.europeana.annotation.mongo.model.internal.PersistentTag;
import eu.europeana.corelib.db.dao.impl.NosqlDaoImpl;

public class PersistentTagDaoImpl <E extends PersistentTag, T extends Serializable>
extends NosqlDaoImpl<E, T> implements PersistentTagDao<PersistentTag, String>{

	public PersistentTagDaoImpl(Class<E> clazz, Datastore datastore) {
		super(clazz, datastore);
	}

}

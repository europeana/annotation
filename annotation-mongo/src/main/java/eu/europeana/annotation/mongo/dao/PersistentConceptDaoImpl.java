package eu.europeana.annotation.mongo.dao;

import java.io.Serializable;

import com.google.code.morphia.Datastore;

import eu.europeana.annotation.mongo.model.internal.PersistentConcept;
import eu.europeana.corelib.db.dao.impl.NosqlDaoImpl;

public class PersistentConceptDaoImpl <E extends PersistentConcept, T extends Serializable>
extends NosqlDaoImpl<E, T> implements PersistentConceptDao<PersistentConcept, String>{

	public PersistentConceptDaoImpl(Class<E> clazz, Datastore datastore) {
		super(datastore, clazz);
	}

}

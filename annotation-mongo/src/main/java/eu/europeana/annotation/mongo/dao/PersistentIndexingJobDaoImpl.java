package eu.europeana.annotation.mongo.dao;

import java.io.Serializable;

import com.google.code.morphia.Datastore;

import eu.europeana.annotation.mongo.model.internal.PersistentIndexingJob;
import eu.europeana.corelib.db.dao.impl.NosqlDaoImpl;

public class PersistentIndexingJobDaoImpl <E extends PersistentIndexingJob, T extends Serializable>
extends NosqlDaoImpl<E, T> implements PersistentIndexingJobDao<PersistentIndexingJob, String>{

	public PersistentIndexingJobDaoImpl(Class<E> clazz, Datastore datastore) {
		super(datastore, clazz);
	}

}

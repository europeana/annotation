package eu.europeana.annotation.mongo.dao;

import java.io.Serializable;
import dev.morphia.Datastore;
import eu.europeana.annotation.mongo.model.internal.PersistentApiWriteLock;
import eu.europeana.api.commons.nosql.dao.impl.NosqlDaoImpl;

public class PersistentApiWriteLockDaoImpl <E extends PersistentApiWriteLock, T extends Serializable>
extends NosqlDaoImpl<E, T> implements PersistentApiWriteLockDao<E, T>{

    public PersistentApiWriteLockDaoImpl(Class<E> clazz, Datastore datastore) {
        super(datastore, clazz);
    }

}

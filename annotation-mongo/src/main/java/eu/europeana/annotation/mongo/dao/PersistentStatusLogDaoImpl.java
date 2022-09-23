package eu.europeana.annotation.mongo.dao;

import java.io.Serializable;
import org.mongodb.morphia.Datastore;
import eu.europeana.annotation.mongo.model.internal.PersistentStatusLog;
import eu.europeana.api.commons.nosql.dao.impl.NosqlDaoImpl;

public class PersistentStatusLogDaoImpl <E extends PersistentStatusLog, T extends Serializable>
extends NosqlDaoImpl<E, T> implements PersistentStatusLogDao<E, T>{

    public PersistentStatusLogDaoImpl(Class<E> clazz, Datastore datastore) {
        super(datastore, clazz);
    }

}

package eu.europeana.annotation.mongo.dao;

import java.io.Serializable;
import org.mongodb.morphia.Datastore;
import eu.europeana.annotation.mongo.model.internal.PersistentModerationRecord;
import eu.europeana.api.commons.nosql.dao.impl.NosqlDaoImpl;

public class PersistentModerationRecordDaoImpl <E extends PersistentModerationRecord, T extends Serializable>
extends NosqlDaoImpl<E, T> implements PersistentModerationRecordDao<E, T>{

    public PersistentModerationRecordDaoImpl(Class<E> clazz, Datastore datastore) {
        super(datastore, clazz);
    }

}

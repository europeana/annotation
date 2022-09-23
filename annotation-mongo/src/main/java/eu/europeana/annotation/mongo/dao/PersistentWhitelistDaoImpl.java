package eu.europeana.annotation.mongo.dao;

import java.io.Serializable;
import org.mongodb.morphia.Datastore;
import eu.europeana.annotation.mongo.model.internal.PersistentWhitelistEntry;
import eu.europeana.api.commons.nosql.dao.impl.NosqlDaoImpl;

public class PersistentWhitelistDaoImpl <E extends PersistentWhitelistEntry, T extends Serializable>
extends NosqlDaoImpl<E, T> implements PersistentWhitelistDao<E, T>{

    public PersistentWhitelistDaoImpl(Class<E> clazz, Datastore datastore) {
        super(datastore, clazz);
    }

}

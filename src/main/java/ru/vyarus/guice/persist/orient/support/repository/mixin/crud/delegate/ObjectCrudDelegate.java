package ru.vyarus.guice.persist.orient.support.repository.mixin.crud.delegate;

import com.google.inject.ProvidedBy;
import com.google.inject.internal.DynamicSingletonProvider;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import ru.vyarus.guice.persist.orient.support.repository.mixin.crud.ObjectCrud;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Crud mixin object implementation.
 *
 * @param <T> object type
 * @author Vyacheslav Rusakov
 * @since 15.10.2014
 */
@ProvidedBy(DynamicSingletonProvider.class)
public abstract class ObjectCrudDelegate<T> implements ObjectCrud<T> {

    private final Provider<OObjectDatabaseTx> dbProvider;

    @Inject
    public ObjectCrudDelegate(final Provider<OObjectDatabaseTx> dbProvider) {
        this.dbProvider = dbProvider;
    }

    @Override
    public void delete(final T entity) {
        dbProvider.get().delete(entity);
    }

    @Override
    public void delete(final String id) {
        dbProvider.get().delete(new ORecordId(id));
    }

    @Override
    public void delete(final ORID id) {
        dbProvider.get().delete(id);
    }
}

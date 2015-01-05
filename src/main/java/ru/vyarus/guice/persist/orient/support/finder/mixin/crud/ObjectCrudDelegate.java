package ru.vyarus.guice.persist.orient.support.finder.mixin.crud;

import com.google.inject.ProvidedBy;
import com.google.inject.internal.DynamicSingletonProvider;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import ru.vyarus.guice.persist.orient.finder.delegate.mixin.FinderGeneric;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Iterator;

/**
 * Crud mixin object implementation.
 *
 * @param <T> object type
 * @author Vyacheslav Rusakov
 * @since 15.10.2014
 */
@ProvidedBy(DynamicSingletonProvider.class)
public abstract class ObjectCrudDelegate<T> implements ObjectCrudMixin<T> {

    private final Provider<OObjectDatabaseTx> dbProvider;

    @Inject
    public ObjectCrudDelegate(final Provider<OObjectDatabaseTx> dbProvider) {
        this.dbProvider = dbProvider;
    }

    @Override
    public T get(final String id) {
        return dbProvider.get().load(new ORecordId(id));
    }

    @Override
    public T get(final ORID id) {
        return dbProvider.get().load(id);
    }

    @Override
    public T save(final T entity) {
        return dbProvider.get().save(entity);
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


    @Override
    public void attach(final T entity) {
        dbProvider.get().attach(entity);
    }

    @Override
    public T detach(final T entity) {
        return dbProvider.get().detach(entity, true);
    }

    public T create(@FinderGeneric("T") final Class<T> type) {
        return dbProvider.get().newInstance(type);
    }

    public Iterator<T> getAll(@FinderGeneric("T") final Class<T> type) {
        return dbProvider.get().browseClass(type);
    }
}

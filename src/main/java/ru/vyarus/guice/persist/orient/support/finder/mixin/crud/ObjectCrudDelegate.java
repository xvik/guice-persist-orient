package ru.vyarus.guice.persist.orient.support.finder.mixin.crud;

import com.google.common.collect.Lists;
import com.google.inject.ProvidedBy;
import com.google.inject.internal.DynamicSingletonProvider;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.object.enhancement.OObjectEntitySerializer;
import ru.vyarus.guice.persist.orient.finder.delegate.mixin.FinderGeneric;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Iterator;
import java.util.List;

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
    public T attach(final T entity) {
        // com.orientechnologies.orient.object.db.OObjectDatabaseTx.attach() did not return object,
        // which is not always correct (new proxy could be created), so do attach directly
        return OObjectEntitySerializer.attach(entity, dbProvider.get());
    }

    @Override
    public T detach(final T entity) {
        T res = null;
        if (entity != null) {
            res = dbProvider.get().detachAll(entity, true);
        }
        return res;
    }

    @Override
    public List<T> detachAll(final Iterator<T> entities) {
        return detachAll(Lists.newArrayList(entities));
    }

    @Override
    public List<T> detachAll(final Iterable<T> entities) {
        return detachAll(Lists.newArrayList(entities));
    }

    @Override
    public List<T> detachAll(final T... entities) {
        return detachAll(Lists.newArrayList(entities));
    }

    public T create(@FinderGeneric("T") final Class<T> type) {
        return dbProvider.get().newInstance(type);
    }

    public Iterator<T> getAll(@FinderGeneric("T") final Class<T> type) {
        return dbProvider.get().browseClass(type);
    }

    private List<T> detachAll(final List<T> entities) {
        final List<T> res = Lists.newArrayList();
        for (T entity : entities) {
            res.add(detach(entity));
        }
        return res;
    }
}

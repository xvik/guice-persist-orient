package ru.vyarus.guice.persist.orient.support.repository.mixin.crud.delegate;

import com.google.common.collect.Lists;
import com.google.inject.ProvidedBy;
import com.google.inject.internal.DynamicSingletonProvider;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.object.enhancement.OObjectEntitySerializer;
import javassist.util.proxy.Proxy;
import ru.vyarus.guice.persist.orient.db.util.RidUtils;
import ru.vyarus.guice.persist.orient.repository.delegate.ext.generic.Generic;
import ru.vyarus.guice.persist.orient.support.repository.mixin.crud.BaseObjectCrud;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Iterator;
import java.util.List;

/**
 * Base object crud mixins implementation.
 *
 * @param <T> object type
 * @author Vyacheslav Rusakov
 * @since 21.06.2015
 */
@ProvidedBy(DynamicSingletonProvider.class)
public abstract class BaseObjectCrudDelegate<T> implements BaseObjectCrud<T> {

    private final Provider<OObjectDatabaseTx> dbProvider;

    @Inject
    public BaseObjectCrudDelegate(final Provider<OObjectDatabaseTx> dbProvider) {
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
            if (entity instanceof Proxy) {
                // when entity detached under transaction it gets temporal id
                // this logic will catch real id after commit and set to object
                RidUtils.trackIdChange((Proxy) entity, res);
            }
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

    public T create(@Generic("T") final Class<T> type) {
        return dbProvider.get().newInstance(type);
    }

    public Iterator<T> getAll(@Generic("T") final Class<T> type) {
        return dbProvider.get().browseClass(type);
    }

    // delegate extension will found method by name and default converter will convert iterator to list
    public Iterator<T> getAllAsList(@Generic("T") final Class<T> type) {
        return getAll(type);
    }

    private List<T> detachAll(final List<T> entities) {
        final List<T> res = Lists.newArrayList();
        for (T entity : entities) {
            res.add(detach(entity));
        }
        return res;
    }
}

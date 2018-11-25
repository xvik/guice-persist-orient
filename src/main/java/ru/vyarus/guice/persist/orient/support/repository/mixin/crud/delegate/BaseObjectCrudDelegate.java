package ru.vyarus.guice.persist.orient.support.repository.mixin.crud.delegate;

import com.google.common.collect.Lists;
import com.google.inject.ProvidedBy;
import com.google.inject.internal.DynamicSingletonProvider;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
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

    private final Provider<ODatabaseObject> objectDb;

    @Inject
    public BaseObjectCrudDelegate(final Provider<ODatabaseObject> objectDb) {
        this.objectDb = objectDb;
    }

    @Override
    public T get(final String id) {
        return objectDb.get().load(new ORecordId(id));
    }

    @Override
    public T get(final ORID id) {
        return objectDb.get().load(id);
    }

    @Override
    public T save(final T entity) {
        return objectDb.get().save(entity);
    }

    @Override
    public T attach(final T entity) {
        // com.orientechnologies.orient.object.db.OObjectDatabaseTx.attach() did not return object,
        // which is not always correct (new proxy could be created), so do attach directly
        return OObjectEntitySerializer.attach(entity, objectDb.get());
    }

    @Override
    public T detach(final T entity) {
        T res = null;
        if (entity != null) {
            res = objectDb.get().detachAll(entity, true);
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
        return detachAllInternal(Lists.newArrayList(entities));
    }

    @Override
    public List<T> detachAll(final Iterable<T> entities) {
        return detachAllInternal(Lists.newArrayList(entities));
    }

    @Override
    public List<T> detachAll(final T... entities) {
        return detachAllInternal(Lists.newArrayList(entities));
    }

    public T create(@Generic("T") final Class<T> type) {
        return objectDb.get().newInstance(type);
    }

    public Iterator<T> getAll(@Generic("T") final Class<T> type) {
        return objectDb.get().browseClass(type);
    }

    // delegate extension will found method by name and default converter will convert iterator to list
    public Iterator<T> getAllAsList(@Generic("T") final Class<T> type) {
        return getAll(type);
    }

    @Override
    public ODocument objectToDocument(final Object object) {
        return (ODocument) objectDb.get().getRecordByUserObject(object, true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T documentToObject(final ODocument document) {
        return (T) objectDb.get().getUserObjectByRecord(document, null);
    }

    private List<T> detachAllInternal(final List<T> entities) {
        final List<T> res = Lists.newArrayList();
        for (T entity : entities) {
            res.add(detach(entity));
        }
        return res;
    }
}

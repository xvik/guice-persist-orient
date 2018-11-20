package ru.vyarus.guice.persist.orient.support.repository.mixin.crud.delegate;

import com.google.inject.ProvidedBy;
import com.google.inject.internal.DynamicSingletonProvider;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import ru.vyarus.guice.persist.orient.repository.delegate.ext.generic.Generic;
import ru.vyarus.guice.persist.orient.support.repository.mixin.crud.DocumentCrud;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Iterator;

/**
 * Crud mixin implementation for document database.
 *
 * @author Vyacheslav Rusakov
 * @since 15.10.2014
 */
@ProvidedBy(DynamicSingletonProvider.class)
public abstract class DocumentCrudDelegate implements DocumentCrud {

    private final Provider<ODatabaseDocument> dbProvider;

    @Inject
    public DocumentCrudDelegate(final Provider<ODatabaseDocument> dbProvider) {
        this.dbProvider = dbProvider;
    }

    @Override
    public ODocument get(final String id) {
        return dbProvider.get().load(new ORecordId(id));
    }

    @Override
    public ODocument get(final ORID id) {
        return dbProvider.get().load(id);
    }

    @Override
    public ODocument save(final ODocument entity) {
        return dbProvider.get().save(entity);
    }

    @Override
    public void delete(final ODocument entity) {
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

    public Iterator<ODocument> getAll(@Generic("T") final Class<?> type) {
        return dbProvider.get().browseClass(type.getSimpleName());
    }

    // delegate extension will found method by name and default converter will convert iterator to list
    public Iterator<ODocument> getAllAsList(@Generic("T") final Class<?> type) {
        return getAll(type);
    }

    public ODocument create(@Generic("T") final Class<?> type) {
        // document creation requires db object bound to current thread, so to make sure
        // we need to start transaction (if not already started)
        dbProvider.get();
        return new ODocument(type.getSimpleName());
    }
}

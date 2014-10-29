package ru.vyarus.guice.persist.orient.support.finder.mixin.crud;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * Crud mixin implementation for document database.
 *
 * @author Vyacheslav Rusakov
 * @since 15.10.2014
 */
@Singleton
public class DocumentCrudDelegate implements DocumentCrudMixin {

    private final Provider<ODatabaseDocumentTx> dbProvider;

    @Inject
    public DocumentCrudDelegate(final Provider<ODatabaseDocumentTx> dbProvider) {
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
}

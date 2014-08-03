package ru.vyarus.guice.persist.orient.db.pool;

import com.orientechnologies.orient.core.db.ODatabasePoolBase;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.db.transaction.TransactionManager;

import javax.inject.Inject;

/**
 * Document connection pool.
 *
 * @author Vyacheslav Rusakov
 * @since 24.07.2014
 */
public class DocumentPool extends AbstractPool<ODatabaseDocumentTx> {

    @Inject
    public DocumentPool(TransactionManager transactionManager) {
        super(transactionManager);
    }

    @Override
    protected ODatabasePoolBase createPool(String uri, String user, String pass) {
        return new ODatabaseDocumentPool(uri, user, pass);
    }

    @Override
    public DbType getType() {
        return DbType.DOCUMENT;
    }
}

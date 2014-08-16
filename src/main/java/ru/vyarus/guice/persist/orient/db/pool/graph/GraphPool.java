package ru.vyarus.guice.persist.orient.db.pool.graph;

import com.orientechnologies.orient.core.db.ODatabaseComplex;
import com.orientechnologies.orient.core.db.ODatabasePoolBase;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.tx.OTransaction;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.db.pool.AbstractPool;
import ru.vyarus.guice.persist.orient.db.transaction.TransactionManager;

import javax.inject.Inject;

/**
 * Graph pool. Provides base graph connection (see additional providers for specific graph connections).
 * This pool works a bit differently from other pools: actually it maintains document connections pool and
 * creates graph connection instances when requested. If you look orient graph pool it works the same way.
 * <p>Also, one more thread local is used to mold created graph connection instances during transaction
 * (it's just to avoid redundant objects creation on every connection request; underlining transaction
 * is handled by document connection, controlled by abstract pool)</p>
 *
 * @author Vyacheslav Rusakov
 * @since 24.07.2014
 */
public class GraphPool extends AbstractPool<OrientBaseGraph> {

    // underlying base pool maintains document connections;
    // this pool manage just wrapped instances to reduce number of created objects
    private ThreadLocal<OrientBaseGraph> transaction = new ThreadLocal<OrientBaseGraph>();
    private TransactionManager transactionManager;

    @Inject
    public GraphPool(final TransactionManager transactionManager) {
        super(transactionManager);
        this.transactionManager = transactionManager;
    }

    @Override
    protected ODatabasePoolBase createPool(final String uri, final String user, final String pass) {
        return new ODatabaseDocumentPool(uri, user, pass);
    }

    @Override
    protected OrientBaseGraph convertDbInstance(final ODatabaseComplex<?> db) {
        if (transaction.get() == null) {
            final ODatabaseDocumentTx documentDb = (ODatabaseDocumentTx) db;
            final OrientBaseGraph graph = transactionManager.getActiveTransactionType() == OTransaction.TXTYPE.NOTX
                    ? new OrientGraphNoTx(documentDb) : new OrientGraph(documentDb);
            transaction.set(graph);
        }
        return transaction.get();
    }

    @Override
    public void commit() {
        super.commit();
        transaction.remove();
    }

    @Override
    public void rollback() {
        super.rollback();
        transaction.remove();
    }

    @Override
    public DbType getType() {
        return DbType.GRAPH;
    }
}

package ru.vyarus.guice.persist.orient.db.pool.graph;

import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.tx.OTransaction;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.db.pool.DocumentPool;
import ru.vyarus.guice.persist.orient.db.pool.PoolManager;
import ru.vyarus.guice.persist.orient.db.transaction.TransactionManager;
import ru.vyarus.guice.persist.orient.db.user.UserManager;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Graph pool implementation. Provides base graph connection (see additional providers for specific graph connections).
 * Use document pool connection to merge graph transaction with document transaction.
 *
 * @author Vyacheslav Rusakov
 * @since 24.07.2014
 */
public class GraphPool implements PoolManager<OrientBaseGraph> {
    private final Logger logger = LoggerFactory.getLogger(GraphPool.class);

    private final ThreadLocal<OrientBaseGraph> transaction = new ThreadLocal<>();
    private final Provider<OrientDB> orientDB;
    private final TransactionManager transactionManager;
    private final DocumentPool documentPool;
    private final UserManager userManager;

    @Inject
    public GraphPool(final Provider<OrientDB> orientDB,
                     final TransactionManager transactionManager,
                     final DocumentPool documentPool,
                     final UserManager userManager) {
        this.orientDB = orientDB;
        this.transactionManager = transactionManager;
        this.documentPool = documentPool;
        this.userManager = userManager;
    }

    @Override
    public void start(final String database) {
        // test connection and let orient configure database
        new OrientGraph(
                (ODatabaseDocumentInternal) orientDB.get()
                        .open(database, userManager.getUser(), userManager.getPassword()))
                .getRawGraph().close();
        logger.debug("Pool {} started for database '{}'", getType(), database);
    }

    @Override
    public void stop() {
        // no stop logic
    }

    @Override
    public OrientBaseGraph get() {
        if (transaction.get() == null) {
            final ODatabaseDocumentInternal documentDb = (ODatabaseDocumentInternal) documentPool.get();
            final OrientBaseGraph graph = transactionManager.getActiveTransactionType() == OTransaction.TXTYPE.NOTX
                    ? new OrientGraphNoTx(documentDb) : new OrientGraph(documentDb);
            transaction.set(graph);
        }
        final OrientBaseGraph db = transaction.get();
        db.getRawGraph().activateOnCurrentThread();
        return db;
    }

    @Override
    public void commit() {
        transaction.remove();
    }

    @Override
    public void rollback() {
        transaction.remove();
    }

    @Override
    public DbType getType() {
        return DbType.GRAPH;
    }
}

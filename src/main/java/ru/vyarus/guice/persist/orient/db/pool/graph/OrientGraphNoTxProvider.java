package ru.vyarus.guice.persist.orient.db.pool.graph;

import com.google.common.base.Preconditions;
import com.google.inject.Provider;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Specific provider for non transactional graph connection. Connection is obtained from base pool (using provider)
 * and casted to target type or fail if transactional mode enabled (transaction type is not notx).
 *
 * @author Vyacheslav Rusakov
 * @since 25.07.2014
 */
@Singleton
public class OrientGraphNoTxProvider implements Provider<OrientGraphNoTx> {

    private final Provider<OrientBaseGraph> provider;

    @Inject
    public OrientGraphNoTxProvider(final Provider<OrientBaseGraph> provider) {
        this.provider = provider;
    }

    @Override
    public OrientGraphNoTx get() {
        final OrientBaseGraph graph = provider.get();
        Preconditions.checkState(graph instanceof OrientGraphNoTx,
                "You must use OrientGraph within transaction or disable transaction "
                        + "(or use OrientBaseGraph as universal solution for all cases)");
        return (OrientGraphNoTx) graph;
    }
}

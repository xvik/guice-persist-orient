package ru.vyarus.guice.persist.orient.db.pool.graph;

import com.google.common.base.Preconditions;
import com.google.inject.Provider;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Specific provider for transactional graph connection. Connection is obtained from base pool (using provider)
 * and cased to target type or fail if non transaction mode defined (transaction type is notx).
 *
 * @author Vyacheslav Rusakov
 * @since 25.07.2014
 */
@Singleton
public class OrientGraphProvider implements Provider<OrientGraph> {

    private final Provider<OrientBaseGraph> provider;

    @Inject
    public OrientGraphProvider(final Provider<OrientBaseGraph> provider) {
        this.provider = provider;
    }

    @Override
    public OrientGraph get() {
        final OrientBaseGraph graph = provider.get();
        Preconditions.checkState(graph instanceof OrientGraph,
                "Transaction started in NOTX mode. You must use OrientGraphNoTx or enable transaction "
                        + "(or use OrientBaseGraph as universal solution for all cases)");
        return (OrientGraph) graph;
    }
}

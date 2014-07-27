package ru.vyarus.guice.persist.orient.support.compat;

import com.google.inject.Binder;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import ru.vyarus.guice.persist.orient.OrientModule;
import ru.vyarus.guice.persist.orient.db.pool.graph.GraphPool;
import ru.vyarus.guice.persist.orient.db.pool.graph.OrientGraphNoTxProvider;
import ru.vyarus.guice.persist.orient.db.pool.graph.OrientGraphProvider;

import java.lang.reflect.Method;

/**
 * Class will fail to load if graph jars are not in classpath, as a result graph pool will not be loaded.
 *
 * @author Vyacheslav Rusakov
 * @since 27.07.2014
 */
public class GraphPoolBinder {
    public GraphPoolBinder(final OrientModule module, final Binder binder) throws Exception {
        final Method bindPool = OrientModule.class.getDeclaredMethod("bindPool", Class.class, Class.class);
        bindPool.setAccessible(true);
        try {
            binder.bind(OrientGraph.class).toProvider(OrientGraphProvider.class);
            binder.bind(OrientGraphNoTx.class).toProvider(OrientGraphNoTxProvider.class);
            bindPool.invoke(module, OrientBaseGraph.class, GraphPool.class);
        } finally {
            bindPool.setAccessible(false);
        }
    }
}

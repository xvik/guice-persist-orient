package ru.vyarus.guice.persist.orient.support.finder;

import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import ru.vyarus.guice.persist.orient.FinderModule;
import ru.vyarus.guice.persist.orient.finder.executor.GraphFinderExecutor;

import java.lang.reflect.Method;

/**
 * Class will fail to load if graph jars are not in classpath, as a result graph executor will not be loaded.
 *
 * @author Vyacheslav Rusakov
 * @since 04.08.2014
 */
public class GraphFinderExecutorBinder {
    public GraphFinderExecutorBinder(final FinderModule module,
                                     final Method bindExecutor) throws Exception {
        // explicit dependency on class required to fail
        OrientBaseGraph.class.getName();
        bindExecutor.invoke(module, GraphFinderExecutor.class);
    }
}

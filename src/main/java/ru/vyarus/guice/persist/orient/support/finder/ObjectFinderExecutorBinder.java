package ru.vyarus.guice.persist.orient.support.finder;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import ru.vyarus.guice.persist.orient.FinderModule;
import ru.vyarus.guice.persist.orient.finder.executor.ObjectFinderExecutor;

import java.lang.reflect.Method;

/**
 * Class will fail to load if object jar is not in classpath, as a result object executor will not be loaded.
 *
 * @author Vyacheslav Rusakov
 * @since 04.08.2014
 */
public class ObjectFinderExecutorBinder {
    public ObjectFinderExecutorBinder(final FinderModule module,
                                      final Method bindExecutor) throws Exception {
        // explicit dependency on class required to fail
        OObjectDatabaseTx.class.getName();
        bindExecutor.invoke(module, ObjectFinderExecutor.class);
    }
}

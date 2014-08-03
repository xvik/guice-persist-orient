package ru.vyarus.guice.persist.orient.support.pool;

import com.google.inject.Binder;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import ru.vyarus.guice.persist.orient.OrientModule;
import ru.vyarus.guice.persist.orient.db.pool.ObjectPool;

import java.lang.reflect.Method;

/**
 * Class will fail to load if object jar is not in classpath, as a result object pool will not be loaded.
 *
 * @author Vyacheslav Rusakov
 * @since 27.07.2014
 */
public class ObjectPoolBinder {
    public ObjectPoolBinder(final OrientModule module, final Method bindPool, final Binder binder) throws Exception {
        bindPool.invoke(module, OObjectDatabaseTx.class, ObjectPool.class);
    }
}

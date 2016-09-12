package ru.vyarus.guice.persist.orient.db.scheme.impl;

import ru.vyarus.guice.persist.orient.db.scheme.ClassLoaderInitializer;

/**
 * Default no-op initializer. Used if no specific implementation provided.
 *
 * @author Vyacheslav Rusakov
 * @since 26.07.2014
 */
public class DefaultClassLoaderInitializer implements ClassLoaderInitializer {

    @Override
    public ClassLoader getLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}

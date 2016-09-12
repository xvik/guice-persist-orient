package ru.vyarus.guice.persist.orient.db.scheme;

import com.google.inject.ImplementedBy;
import ru.vyarus.guice.persist.orient.db.scheme.impl.DefaultClassLoaderInitializer;

/**
 * <p>Initialize a custom classloader. Useful when application is running in separate context</p>
 *
 * @author Derric Gilling
 * @since 30.01.2015
 */
@ImplementedBy(DefaultClassLoaderInitializer.class)
public interface ClassLoaderInitializer {

    ClassLoader getLoader();
}

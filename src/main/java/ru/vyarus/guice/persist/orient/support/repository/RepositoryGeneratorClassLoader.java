package ru.vyarus.guice.persist.orient.support.repository;

import com.google.inject.Inject;
import ru.vyarus.guice.ext.core.generator.GeneratorClassLoader;
import ru.vyarus.guice.persist.orient.db.scheme.ClassLoaderInitializer;
/**
 * Class Loader for Dynamic Generator.
 *
 * @author Derric Gilling
 * @since 11.05.2016
 */
public class RepositoryGeneratorClassLoader implements GeneratorClassLoader {

    @Inject
    ClassLoaderInitializer classLoaderInitializer;

    @Override
    public ClassLoader getLoader() {
        return classLoaderInitializer.getLoader();
    }
}

package ru.vyarus.guice.persist.orient.support;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.persist.finder.Finder;
import com.orientechnologies.common.reflection.OReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.FinderModule;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Finder module which able to scan classpath to dynamically find and register finder interfaces.
 * Anyway, ability to manually add interfaces still remain: part of classpath may be scanned and finders from other
 * parts may be added manually. The reason for this could be some optional classes in classpath, which will cause class
 * loading exception (e.g. some jars are absent).
 *
 * @author Vyacheslav Rusakov
 * @since 04.08.2014
 */
public class AutoScanFinderModule extends FinderModule {
    private final Logger logger = LoggerFactory.getLogger(AutoScanFinderModule.class);
    private final String pkg;

    public AutoScanFinderModule(final String pkg) {
        super();
        this.pkg = Preconditions.checkNotNull(pkg, "Package is required to reduce classpath scanning scope.");
        registerFinders();
    }

    private void registerFinders() {
        final List<Class<?>> finderClasses = Lists.newArrayList();
        try {
            final List<Class<?>> foundClasses = OReflectionHelper
                    .getClassesFor(pkg, Thread.currentThread().getContextClassLoader());
            for (Class<?> cls : foundClasses) {
                if (cls.isInterface()) {
                    // at least one method annotated with finder (complete check module will perform during binding)
                    for (Method method : cls.getMethods()) {
                        if (method.isAnnotationPresent(Finder.class)) {
                            finderClasses.add(cls);
                            break;
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Failed to resolve finder classes from package: " + pkg, e);
        }
        for (Class<?> cls : finderClasses) {
            logger.info("Registering finder interface: {}", cls);
            addFinder(cls);
        }
    }
}

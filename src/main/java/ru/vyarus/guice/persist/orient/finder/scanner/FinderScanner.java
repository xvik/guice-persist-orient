package ru.vyarus.guice.persist.orient.finder.scanner;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.inject.persist.finder.Finder;
import com.orientechnologies.common.reflection.OReflectionHelper;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Utility class to scan classpath and find finder interfaces.
 *
 * @author Vyacheslav Rusakov
 * @since 17.10.2014
 */
public final class FinderScanner {
    private FinderScanner() {
    }

    /**
     * Scans provided packages for finder interfaces.
     *
     * @param packages packages to scan
     * @return list of found finder interfaces
     */
    public static List<Class<?>> scan(final String... packages) {
        final List<Class<?>> finderClasses = Lists.newArrayList();
        for (String pkg : packages) {
            finderClasses.addAll(scanPackage(pkg));
        }
        return finderClasses;
    }

    private static List<Class<?>> scanPackage(final String pkg) {
        final List<Class<?>> finderClasses = Lists.newArrayList();
        try {
            final List<Class<?>> foundClasses = OReflectionHelper
                    .getClassesFor(pkg, Thread.currentThread().getContextClassLoader());
            finderClasses.addAll(Collections2.filter(foundClasses, new Predicate<Class<?>>() {
                @Override
                public boolean apply(@Nonnull final Class<?> input) {
                    return isFinderClass(input);
                }
            }));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Failed to resolve finder classes from package: " + pkg, e);
        }
        return finderClasses;
    }

    private static boolean isFinderClass(final Class<?> type) {
        boolean resolution = false;
        // only top level finders resolved, avoiding generified mixin finders
        final boolean acceptable = type.isInterface()
                && type.getTypeParameters().length == 0
                && !type.isAnnotationPresent(InvisibleForScanner.class);
        if (acceptable) {
            // at least one method annotated with finder (complete check module will perform during binding)
            for (Method method : type.getMethods()) {
                if (method.isAnnotationPresent(Finder.class)) {
                    resolution = true;
                    break;
                }
            }
        }
        return resolution;
    }
}

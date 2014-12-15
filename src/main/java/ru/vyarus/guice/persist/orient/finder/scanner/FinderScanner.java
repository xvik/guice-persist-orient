package ru.vyarus.guice.persist.orient.finder.scanner;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.orientechnologies.common.reflection.OReflectionHelper;
import ru.vyarus.guice.persist.orient.finder.util.FinderUtils;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Utility class to scan classpath and find finder interfaces.
 *
 * @author Vyacheslav Rusakov
 * @since 17.10.2014
 * @deprecated finders now completely
 * <a href="https://github.com/xvik/guice-ext-annotations#usage">controlled by guice</a>
 * you can use @ProvidedBy instead of classpath scanning
 */
@Deprecated
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
        return FinderUtils.isFinderInterface(type)
                && !type.isAnnotationPresent(InvisibleForScanner.class);
    }
}

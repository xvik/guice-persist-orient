package ru.vyarus.guice.persist.orient.finder.util;

import com.google.inject.persist.finder.Finder;
import ru.vyarus.guice.persist.orient.finder.delegate.FinderDelegate;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Finder specific utilities.
 *
 * @author Vyacheslav Rusakov
 * @since 26.10.2014
 */
public final class FinderUtils {

    private FinderUtils() {
    }

    /**
     * Resolves finder class from finder instance. For interface finder detects proxy and returns implemented
     * interface. For bean finder returns bean class (not guice proxy).
     *
     * @param finder finder instance
     * @return base finder type
     */
    public static Class<?> resolveFinderClass(final Object finder) {
        Class<?> result;
        if (finder instanceof Proxy) {
            // finder proxy always created around single interface
            result = finder.getClass().getInterfaces()[0];
        } else {
            // bean finder
            result = finder.getClass();
            if (result.getName().contains("$$EnhancerByGuice")) {
                result = result.getSuperclass();
            }
        }
        return result;
    }

    public static boolean isFinderMethod(final Method method) {
        return isDirectFinderMethod(method) || isMixin(method.getDeclaringClass());
    }

    /**
     * @param method method to check
     * @return true if method annotated with {@code @Finder} or {@code FinderDelegate}, false otherwise
     */
    public static boolean isDirectFinderMethod(final Method method) {
        return method.isAnnotationPresent(Finder.class) || method.isAnnotationPresent(FinderDelegate.class);
    }

    /**
     * Checks if finder is not mixin, is interface and contains at least one method annotated with
     * {@code @Finder} or {@code @FinderDelegate} (single method is enough, complete check will be performed
     * later during binding).
     *
     * @param finder finder interface to analyze
     * @return true if finder interface, false if not finder or generic finder
     */
    public static boolean isFinderInterface(final Class<?> finder) {
        boolean res = false;
        final boolean isMixin = isMixin(finder);
        if (!isMixin && finder.isInterface()) {
            for (Method method : finder.getDeclaredMethods()) {
                if (isDirectFinderMethod(method)) {
                    res = true;
                    break;
                }
            }
            if (!res && finder.getDeclaredMethods().length == 0) {
                // finder may not contain methods and just extend mixins
                for (Class<?> iface : finder.getInterfaces()) {
                    if (isMixin(iface)) {
                        res = true;
                        break;
                    }
                }
            }
        }
        return res;
    }

    /**
     * Detects mixin interface. Interface is mixin if it has generics in signature or {@code @FinderDelegate}
     * annotation defined on type. It's not absolutely accurate, but handles most generic cases.
     *
     * @param finder finder interface to analyze
     * @return true if mixin signs detected, false otherwise (still it's not guarantee this type is not mixin -
     * it depends on usage)
     */
    public static boolean isMixin(final Class<?> finder) {
        boolean res = false;
        if (finder.isInterface()) {
            res = finder.getTypeParameters().length > 0 || finder.isAnnotationPresent(FinderDelegate.class);
        }
        return res;
    }
}

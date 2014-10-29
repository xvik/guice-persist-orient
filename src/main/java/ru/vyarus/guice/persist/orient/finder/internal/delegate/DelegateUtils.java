package ru.vyarus.guice.persist.orient.finder.internal.delegate;

import com.google.common.base.Strings;
import ru.vyarus.guice.persist.orient.finder.delegate.FinderDelegate;
import ru.vyarus.guice.persist.orient.finder.internal.FinderDefinitionException;

import java.lang.reflect.Method;

/**
 * Delegate helper utilities.
 *
 * @author Vyacheslav Rusakov
 * @since 21.10.2014
 */
public final class DelegateUtils {

    private DelegateUtils() {
    }

    /**
     * Looks for {@link ru.vyarus.guice.persist.orient.finder.delegate.FinderDelegate} annotation on method or type.
     *
     * @param method method to lookup annotation
     * @return method or type annotation
     * @throws FinderDefinitionException if annotation declared on type and method hint specified
     */
    public static FinderDelegate findAnnotation(final Method method) {
        FinderDelegate annotation = method.getAnnotation(FinderDelegate.class);
        if (annotation == null) {
            annotation = method.getDeclaringClass().getAnnotation(FinderDelegate.class);
            if (annotation != null) {
                FinderDefinitionException.check(Strings.emptyToNull(annotation.method()) == null,
                        "Method attribute must not be used when defining class wide delegate.");
            }
        }
        return annotation;
    }
}

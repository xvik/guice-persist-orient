package ru.vyarus.guice.persist.orient.db.transaction.internal;

import com.google.inject.persist.Transactional;
import com.orientechnologies.orient.core.tx.OTransaction;
import ru.vyarus.guice.persist.orient.db.transaction.TxConfig;
import ru.vyarus.guice.persist.orient.db.transaction.TxType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Transaction config builder by analyzing method/type annotations.
 * May be used in other places to easily add @Transactional annotations support.
 *
 * @author Vyacheslav Rusakov
 * @since 01.08.2014
 */
public final class AnnotationTxConfigBuilder {

    private AnnotationTxConfigBuilder() {
    }

    /**
     * Build transaction config for type.
     *
     * @param type        type to analyze
     * @param method      method to analyze
     * @param useDefaults true to build default config if annotation not found
     * @return tx config for found annotation, null if useDefaults false and default config otherwise
     */
    public static TxConfig buildConfig(final Class<?> type, final Method method, final boolean useDefaults) {
        final Transactional transactional = findAnnotation(type, method, Transactional.class, useDefaults);
        TxConfig res = null;
        if (transactional != null) {
            final TxType txType = findAnnotation(type, method, TxType.class, true);
            res = new TxConfig(wrapExceptions(transactional.rollbackOn()),
                    wrapExceptions(transactional.ignore()), txType.value());
        }
        return res;
    }

    private static <T extends Annotation> T findAnnotation(final Class<?> type, final Method method,
                                                           final Class<T> target, final boolean useDefaults) {
        T transactional;

        transactional = method.getAnnotation(target);
        if (null == transactional) {
            // If none on method, try the class.
            transactional = type.getAnnotation(target);
        }
        if (null == transactional && useDefaults) {
            // If there is no transactional annotation present, use the default
            transactional = Internal.class.getAnnotation(target);
        }

        return transactional;
    }

    /**
     * Avoid creation of empty list.
     *
     * @param list array of exceptions
     * @return converted list or null if array os empty
     */
    private static List<Class<? extends Exception>> wrapExceptions(final Class<? extends Exception>... list) {
        return list.length == 0 ? null : Arrays.asList(list);
    }

    /**
     * Default annotations definitions.
     */
    @Transactional
    @TxType(OTransaction.TXTYPE.OPTIMISTIC)
    private static class Internal {
    }
}

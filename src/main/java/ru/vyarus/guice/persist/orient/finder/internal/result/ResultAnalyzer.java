package ru.vyarus.guice.persist.orient.finder.internal.result;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.finder.result.Optionals;
import ru.vyarus.guice.persist.orient.finder.result.ResultType;
import ru.vyarus.java.generics.resolver.context.GenericsContext;
import ru.vyarus.java.generics.resolver.util.NoGenericException;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;

import static ru.vyarus.guice.persist.orient.finder.internal.FinderDefinitionException.check;
import static ru.vyarus.guice.persist.orient.finder.result.ResultType.*;

/**
 * Analyze finder method return type.
 *
 * @author Vyacheslav Rusakov
 * @since 26.09.2014
 */
public final class ResultAnalyzer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultAnalyzer.class);

    private ResultAnalyzer() {
    }

    /**
     * Analyze return type.
     *
     * @param method               finder method
     * @param generics             finder interface hierarchy generic parameters context
     * @param returnCollectionType collection implementation to convert to or null if conversion not required
     * @return result description object
     */
    public static ResultDescriptor analyzeReturnType(final Method method, final GenericsContext generics,
                                                     final Class<? extends Collection> returnCollectionType) {
        final Class<?> returnClass = generics.resolveReturnClass(method);
        final ResultDescriptor descriptor = new ResultDescriptor();
        descriptor.expectType = resolveExpectedType(returnClass, returnCollectionType);

        ResultType type;
        Class<?> entityClass;
        if (!ODocument.class.isAssignableFrom(returnClass)
                && (Collection.class.isAssignableFrom(returnClass)
                || Iterator.class.isAssignableFrom(returnClass)
                || Iterable.class.isAssignableFrom(returnClass))) {
            type = COLLECTION;
            entityClass = resolveGenericType(method.getGenericReturnType(), method, generics);
        } else if (returnClass.isArray()) {
            type = ARRAY;
            entityClass = returnClass.getComponentType();
        } else {
            type = PLAIN;
            // support for guava and jdk8 optionals
            entityClass = Optionals.isOptional(returnClass)
                    ? resolveGenericType(method.getGenericReturnType(), method, generics) : returnClass;
        }

        descriptor.returnType = type;
        descriptor.entityType = entityClass;
        return descriptor;
    }

    private static Class<?> resolveExpectedType(final Class<?> returnClass, final Class<?> returnCollectionType) {
        Class<?> expected;
        if (returnCollectionType != null) {
            check(returnClass.isAssignableFrom(returnCollectionType),
                    "Requested collection %s is incompatible with method return type %s",
                    returnCollectionType, returnClass);
            expected = returnCollectionType;
        } else {
            expected = returnClass;
        }
        return expected;
    }

    private static Class<?> resolveGenericType(final Type returnClass, final Method method,
                                               final GenericsContext generics) {
        Class res;
        try {
            res = generics.resolveGenericOf(returnClass);
        } catch (NoGenericException e) {
            res = Object.class;
            LOGGER.warn(
                    "Can't detect entity: no generic set in finder method return type: {}#{}.",
                    method.getDeclaringClass(), method.getName());
        }
        return res;
    }
}

package ru.vyarus.guice.persist.orient.repository.core.result;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Primitives;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.Optionals;
import ru.vyarus.guice.persist.orient.repository.core.spi.DescriptorContext;
import ru.vyarus.guice.persist.orient.repository.core.util.RepositoryUtils;
import ru.vyarus.java.generics.resolver.context.MethodGenericsContext;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;
import static ru.vyarus.guice.persist.orient.repository.core.result.ResultType.*;

/**
 * Analyze repository method return type.
 *
 * @author Vyacheslav Rusakov
 * @since 26.09.2014
 */
public final class ResultAnalyzer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultAnalyzer.class);
    private static final List<Class> VOID_TYPES = ImmutableList.<Class>of(Void.class, void.class);

    private ResultAnalyzer() {
    }

    /**
     * Analyze return type.
     *
     * @param context              repository method context
     * @param returnCollectionType collection implementation to convert to or null if conversion not required
     * @return result description object
     */
    public static ResultDescriptor analyzeReturnType(final DescriptorContext context,
                                                     final Class<? extends Collection> returnCollectionType) {
        final Method method = context.method;
        final MethodGenericsContext generics = context.generics.method(method);
        final Class<?> returnClass = generics.resolveReturnClass();
        final ResultDescriptor descriptor = new ResultDescriptor();
        descriptor.expectType = resolveExpectedType(returnClass, returnCollectionType);

        final ResultType type;
        final Class<?> entityClass;
        if (isCollection(returnClass)) {
            type = COLLECTION;
            entityClass = resolveGenericType(method, generics);
        } else if (returnClass.isArray()) {
            type = ARRAY;
            entityClass = returnClass.getComponentType();
        } else if (VOID_TYPES.contains(returnClass)) {
            type = VOID;
            entityClass = Void.class;
        } else {
            type = PLAIN;
            // support for guava and jdk8 optionals
            entityClass = Optionals.isOptional(returnClass)
                    ? resolveGenericType(method, generics) : returnClass;
        }
        descriptor.returnType = type;
        descriptor.entityType = entityClass;
        return descriptor;
    }

    private static boolean isCollection(final Class<?> type) {
        return !ODocument.class.isAssignableFrom(type)
                && (Collection.class.isAssignableFrom(type)
                || Iterator.class.isAssignableFrom(type)
                || Iterable.class.isAssignableFrom(type));
    }

    private static Class<?> resolveExpectedType(final Class<?> returnClass, final Class<?> returnCollectionType) {
        final Class<?> expected;
        if (returnCollectionType != null) {
            check(returnClass.isAssignableFrom(returnCollectionType),
                    "Requested collection %s is incompatible with method return type %s",
                    returnCollectionType, returnClass);
            expected = returnCollectionType;
        } else {
            expected = returnClass;
        }
        // wrap primitive, because result will always be object
        return Primitives.wrap(expected);
    }

    private static Class<?> resolveGenericType(final Method method, final MethodGenericsContext generics) {
        Class res = generics.resolveReturnTypeGeneric();
        if (res == null) {
            res = Object.class;
            LOGGER.warn(
                    "Can't detect entity: no generic set in repository method return type: {}.",
                    RepositoryUtils.methodToString(method));
        }
        return res;
    }
}

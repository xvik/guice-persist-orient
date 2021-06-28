package ru.vyarus.guice.persist.orient.repository.core.ext.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.Optionals;
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.ResultConversionException;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Utility to validate result conversion correctness.
 *
 * @author Vyacheslav Rusakov
 * @since 09.10.2017
 */
@SuppressWarnings("PMD.GodClass")
public final class ResultUtils {

    private ResultUtils() {
    }

    /**
     * Check converted result compatibility with required type.
     *
     * @param result     result object
     * @param targetType target type
     * @throws ResultConversionException if result doesn't match required type
     */
    public static void check(final Object result, final Class<?> targetType) {
        if (result != null && !targetType.isAssignableFrom(result.getClass())) {
            // note: conversion logic may go wrong (e.g. because converter expect collection input mostly and may
            // not work correctly for single element), but anyway overall conversion would be considered failed.
            throw new ResultConversionException(String.format("Failed to convert %s to %s",
                    toStringType(result), targetType.getSimpleName()));
        }
    }

    /**
     * Convert result to plain value. The simplest cases are update queries where orient already returns plain value.
     * For other cases, when orient returns collection, took first element (an possibly apply projection).
     * <p>
     * Special case is {@code Optional} result wrapping when plain value (from the logic above) is wrapped into
     * optional (guava or jdk).
     *
     * @param result      result object
     * @param returnClass required return class
     * @param entityClass entity class (in most cases would be the same as resultClass, except optional cases)
     * @param projection  true to apply projection, false otherwise
     * @return converted result
     */
    @SuppressWarnings("unchecked")
    public static Object convertToPlain(final Object result, final Class returnClass,
                                        final Class entityClass, final boolean projection) {
        final boolean isOptional = Optionals.isOptional(returnClass);
        Object converted = convertPlainImpl(result, isOptional ? entityClass : returnClass, projection);
        if (isOptional) {
            // jdk8 or guava optional
            converted = Optionals.create(converted, returnClass);
        }
        return converted;
    }

    /**
     * Convert result object to collection. In some cases, this could be do nothing case, because orient already
     * returns collection. If projection is required or when collection type is different from requested type,
     * result will be re-packaged into the new collection.
     *
     * @param result         result instance
     * @param collectionType target collection type
     * @param targetEntity   target entity type
     * @param projection     true to apply projection, false otherwise
     * @return converted result
     */
    @SuppressWarnings("unchecked")
    public static Object convertToCollection(final Object result, final Class collectionType,
                                             final Class targetEntity, final boolean projection) {
        final Object converted;
        if (collectionType.equals(Iterator.class)) {
            converted = toIterator(result, targetEntity, projection);
        } else if (collectionType.isAssignableFrom(List.class)) {
            converted = Lists.newArrayList(toIterator(result, targetEntity, projection));
        } else if (collectionType.isAssignableFrom(Set.class)) {
            converted = Sets.newHashSet(toIterator(result, targetEntity, projection));
        } else if (!collectionType.isInterface()) {
            converted = convertToCollectionImpl(result, collectionType, targetEntity, projection);
        } else {
            throw new ResultConversionException(String.format(
                    "Incompatible result type requested %s for conversion from actual result %s",
                    collectionType, result.getClass()));
        }
        return converted;
    }

    /**
     * Convert result object to array.
     *
     * @param result     result object
     * @param entityType target entity type
     * @param projection true to apply projection, false otherwise
     * @return converted result
     */
    @SuppressWarnings("PMD.LooseCoupling")
    public static Object convertToArray(final Object result, final Class entityType, final boolean projection) {
        final Collection res = result instanceof Collection
                // no projection because its applied later
                ? (Collection) result : convertToCollectionImpl(result, ArrayList.class, entityType, false);
        final Object array = Array.newInstance(entityType, res.size());
        int i = 0;
        for (Object obj : res) {
            Array.set(array, i++, projection ? applyProjection(obj, entityType) : obj);
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    public static Iterator toIterator(final Object result, final Class targetEntity, final boolean projection) {
        Iterator res;
        if (result instanceof Iterator) {
            res = (Iterator) result;
        } else if (result instanceof Iterable) {
            res = ((Iterable) result).iterator();
        } else {
            throw new ResultConversionException("Can't convert " + result.getClass() + " to iterator");
        }
        // "expensive" step, but will be executed only if projection is really required
        if (projection) {
            final List tmp = new ArrayList();
            while (res.hasNext()) {
                tmp.add(applyProjection(res.next(), targetEntity));
            }
            res = tmp.iterator();
        }
        return res;
    }

    /**
     * Flattening is important for simple cases: when querying for count (or other aggregated function) or
     * for single field (column).
     *
     * @param object      result object
     * @param returnClass expected type
     * @return either object itself or just object field (extracted)
     */
    @SuppressWarnings("checkstyle:IllegalIdentifierName")
    public static Object applyProjection(final Object object, final Class<?> returnClass) {
        Object res = object;
        if (!ODocument.class.isAssignableFrom(returnClass)) {
            ODocument doc = null;
            if (object instanceof ODocument) {
                doc = (ODocument) object;
            }
            if (object instanceof OIdentifiable) {
                // most likely OrientVertex, which is returned under graph connection, even for partial requests
                final Object record = ((OIdentifiable) object).getRecord();
                if (record instanceof ODocument) {
                    doc = (ODocument) record;
                }
            }
            if (doc != null && doc.fieldNames().length == 1) {
                res = doc.fieldValues()[0];
                // if required, perform result correction
                if (res != null && !returnClass.isAssignableFrom(res.getClass())) {
                    res = convertPlainImpl(res, returnClass, true);
                }
            }
        }
        return res;
    }

    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
    private static String toStringType(final Object result) {
        final String sourceType;
        if (!ORecord.class.isAssignableFrom(result.getClass()) && result instanceof Collection) {
            final Iterator it = ((Collection) result).iterator();
            Object first = null;
            while (first == null || it.hasNext()) {
                first = it.next();
            }
            sourceType = "Collection" + (first == null ? "" : ("<" + first.getClass().getSimpleName() + ">"));
        } else {
            sourceType = result.getClass().getSimpleName();
        }
        return sourceType;
    }

    private static Object convertPlainImpl(final Object result, final Class returnClass, final boolean projection) {
        Object converted = null;
        // only update query returns simple number
        if (returnClass.equals(Long.class) && result instanceof Number) {
            converted = ((Number) result).longValue();
        } else if (returnClass.equals(Integer.class) && result instanceof Number) {
            converted = ((Number) result).intValue();
        } else {
            if (result instanceof ORecord) {
                // most likely ResultConverter call (because queries always return collections)
                converted = projection ? applyProjection(result, returnClass) : result;
            } else {
                // if single type required convert from collection
                // simple single type case will be handled on checking assignment (at the top).
                // No projection to apply it to one element only
                final Iterator it = toIterator(result, returnClass, false);
                if (it.hasNext()) {
                    converted = projection ? applyProjection(it.next(), returnClass) : it.next();
                }
            }
        }
        return converted;
    }

    @SuppressWarnings("unchecked")
    private static Collection convertToCollectionImpl(final Object result, final Class collectionImplType,
                                                      final Class targetEntity, final boolean projection) {
        final Collection collection;
        try {
            collection = (Collection) collectionImplType.newInstance();
        } catch (InstantiationException e) {
            throw new ResultConversionException(
                    "Specified method's collection class could not be instantiated: " + collectionImplType, e);
        } catch (IllegalAccessException e) {
            throw new ResultConversionException(
                    "Specified method's collection class could not be instantiated (do not have access privileges): "
                            + collectionImplType, e);
        }
        final Iterator it = toIterator(result, targetEntity, projection);
        while (it.hasNext()) {
            collection.add(it.next());
        }
        return collection;
    }
}

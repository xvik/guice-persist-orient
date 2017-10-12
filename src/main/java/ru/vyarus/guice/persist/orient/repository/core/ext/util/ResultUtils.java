package ru.vyarus.guice.persist.orient.repository.core.ext.util;

import com.orientechnologies.orient.core.record.ORecord;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.ResultConversionException;

import java.util.Collection;
import java.util.Iterator;

/**
 * Utility to validate result conversion correctness.
 *
 * @author Vyacheslav Rusakov
 * @since 09.10.2017
 */
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
}

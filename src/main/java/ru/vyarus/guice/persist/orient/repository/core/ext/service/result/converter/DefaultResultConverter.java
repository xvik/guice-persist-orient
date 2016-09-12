package ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Primitives;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import ru.vyarus.guice.persist.orient.repository.core.result.ResultDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.result.ResultType;

import javax.inject.Singleton;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Default converter implementation.
 * May be substituted for another one by simply defining new converter instance in guice context.
 *
 * @author Vyacheslav Rusakov
 * @since 04.08.2014
 */
@Singleton
public class DefaultResultConverter implements ResultConverter {

    @Override
    @SuppressWarnings("unchecked")
    public Object convert(final ResultDescriptor desc, final Object result) {
        // wrap primitive, because result will always be object
        final Class<?> expectType = desc.expectType;
        final Class<?> returnClass = expectType.isPrimitive() ? Primitives.wrap(expectType) : expectType;

        Object res = null;
        if (result != null && !ResultType.VOID.equals(desc.returnType)) {
            res = returnClass.isAssignableFrom(result.getClass())
                    ? result : convertResult(desc.returnType, returnClass, desc.entityType, result);
        }
        return res;
    }

    private Object convertResult(final ResultType type, final Class returnClass,
                                 final Class entityClass, final Object result) {
        final Object converted;
        switch (type) {
            case COLLECTION:
                converted = handleCollection(result, returnClass);
                break;
            case ARRAY:
                converted = handleArray(result, entityClass);
                break;
            case PLAIN:
                converted = handlePlain(result, returnClass, entityClass);
                break;
            default:
                throw new ResultConversionException("Unsupported return type " + type);
        }
        return converted;
    }

    @SuppressWarnings("unchecked")
    private Object handleCollection(final Object result, final Class returnClass) {
        final Object converted;
        if (returnClass.equals(Iterator.class)) {
            converted = toIterator(result);
        } else if (returnClass.isAssignableFrom(List.class)) {
            converted = Lists.newArrayList(toIterator(result));
        } else if (returnClass.isAssignableFrom(Set.class)) {
            converted = Sets.newHashSet(toIterator(result));
        } else if (!returnClass.isInterface()) {
            converted = convertToCollection(result, returnClass);
        } else {
            throw new ResultConversionException(String.format(
                    "Incompatible result type requested %s for conversion from actual result %s",
                    returnClass, result.getClass()));
        }
        return converted;
    }

    @SuppressWarnings("PMD.LooseCoupling")
    private Object handleArray(final Object result, final Class entityType) {
        final Collection res = result instanceof Collection
                ? (Collection) result : convertToCollection(result, ArrayList.class);
        final Object array = Array.newInstance(entityType, res.size());
        int i = 0;
        for (Object obj : res) {
            Array.set(array, i++, flattenSimple(obj, entityType));
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    private Object handlePlain(final Object result, final Class returnClass, final Class entityClass) {
        final boolean isOptional = Optionals.isOptional(returnClass);
        Object converted = handlePlainValue(result, isOptional ? entityClass : returnClass);
        if (isOptional) {
            // jdk8 or guava optional
            converted = Optionals.create(converted, returnClass);
        }
        return converted;
    }

    private Object handlePlainValue(final Object result, final Class returnClass) {
        Object converted = null;
        if (returnClass.equals(Long.class) && result instanceof Number) {
            converted = ((Number) result).longValue();
        } else if (returnClass.equals(Integer.class) && result instanceof Number) {
            converted = ((Number) result).intValue();
        } else {
            // if single type required convert from collection
            // simple single type case will be handled on checking assignment (at the top)
            final Iterator it = toIterator(result);
            if (it.hasNext()) {
                converted = it.next();
                if (!ODocument.class.equals(returnClass) && !returnClass.isInstance(converted)) {
                    converted = flattenSimple(converted, returnClass);
                }
            }
        }
        return converted;
    }

    private Iterator toIterator(final Object result) {
        final Iterator res;
        if (result instanceof Iterator) {
            res = (Iterator) result;
        } else if (result instanceof Iterable) {
            res = ((Iterable) result).iterator();
        } else {
            throw new ResultConversionException("Can't convert " + result.getClass() + " to iterator");
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    private Collection convertToCollection(final Object result, final Class returnClass) {
        final Collection collection;
        try {
            collection = (Collection) returnClass.newInstance();
        } catch (InstantiationException e) {
            throw new ResultConversionException(
                    "Specified method's collection class could not be instantiated: " + returnClass, e);
        } catch (IllegalAccessException e) {
            throw new ResultConversionException(
                    "Specified method's collection class could not be instantiated (do not have access privileges): "
                            + returnClass, e);
        }
        final Iterator it = toIterator(result);
        while (it.hasNext()) {
            collection.add(it.next());
        }
        return collection;
    }

    /**
     * Flattening is important for simple cases: when querying for count (or other aggregated function) or
     * for single field (column).
     *
     * @param object      result object
     * @param returnClass expected type
     * @return either object itself or just object field (extracted)
     */
    private Object flattenSimple(final Object object, final Class<?> returnClass) {
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
                    res = handlePlainValue(res, returnClass);
                }
            }
        }
        return res;
    }
}

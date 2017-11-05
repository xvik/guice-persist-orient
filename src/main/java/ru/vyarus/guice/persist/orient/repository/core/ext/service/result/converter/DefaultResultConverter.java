package ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import ru.vyarus.guice.persist.orient.db.DbType;
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
@SuppressWarnings("PMD.GodClass")
public class DefaultResultConverter implements ResultConverter {

    @Override
    @SuppressWarnings("unchecked")
    public Object convert(final ResultDescriptor desc, final Object result) {
        final Class<?> returnClass = desc.expectType;

        Object res = null;
        if (result != null && !ResultType.VOID.equals(desc.returnType)) {
            res = isCompatible(result, returnClass, desc)
                    ? result : convertResult(
                    desc.returnType,
                    returnClass,
                    desc.entityType,
                    result,
                    desc.entityDbType == DbType.UNKNOWN);
        }
        // converted result may not match required return type! this is important for cases with custom
        // result converter extensions which will use default conversion as a source for final conversion
        return res;
    }

    @SuppressWarnings("unchecked")
    private boolean isCompatible(final Object result, final Class<?> returnClass, final ResultDescriptor desc) {
        boolean compatible = false;
        if (returnClass.isAssignableFrom(result.getClass())) {
            // type compatibility checked only for collections when target entity type is specified
            // and its not native orient type (connection will already return required object).
            compatible = desc.returnType != ResultType.COLLECTION
                    || desc.entityDbType != DbType.UNKNOWN
                    || Object.class.equals(desc.entityType)
                    // In case of graph connection, there could be an iterator which should be ignored
                    // to avoid premature database load (this is possible only if connection hint was manually set,
                    // because otherwise document connection will be used)
                    || !Collection.class.isAssignableFrom(result.getClass());
        }
        return compatible;
    }

    private Object convertResult(final ResultType type, final Class returnClass,
                                 final Class entityClass, final Object result, final boolean projection) {
        final Object converted;
        switch (type) {
            case COLLECTION:
                converted = handleCollection(result, returnClass, entityClass, projection);
                break;
            case ARRAY:
                converted = handleArray(result, entityClass, projection);
                break;
            case PLAIN:
                converted = handlePlain(result, returnClass, entityClass, projection);
                break;
            default:
                throw new ResultConversionException("Unsupported return type " + type);
        }
        return converted;
    }

    @SuppressWarnings("unchecked")
    private Object handleCollection(final Object result, final Class returnClass,
                                    final Class targetEntity, final boolean projection) {
        final Object converted;
        if (returnClass.equals(Iterator.class)) {
            converted = toIterator(result, targetEntity, projection);
        } else if (returnClass.isAssignableFrom(List.class)) {
            converted = Lists.newArrayList(toIterator(result, targetEntity, projection));
        } else if (returnClass.isAssignableFrom(Set.class)) {
            converted = Sets.newHashSet(toIterator(result, targetEntity, projection));
        } else if (!returnClass.isInterface()) {
            converted = convertToCollection(result, returnClass, targetEntity, projection);
        } else {
            throw new ResultConversionException(String.format(
                    "Incompatible result type requested %s for conversion from actual result %s",
                    returnClass, result.getClass()));
        }
        return converted;
    }

    @SuppressWarnings("PMD.LooseCoupling")
    private Object handleArray(final Object result, final Class entityType, final boolean projection) {
        final Collection res = result instanceof Collection
                // no projection because its applied later
                ? (Collection) result : convertToCollection(result, ArrayList.class, entityType, false);
        final Object array = Array.newInstance(entityType, res.size());
        int i = 0;
        for (Object obj : res) {
            Array.set(array, i++, projection ? flattenSimple(obj, entityType) : obj);
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    private Object handlePlain(final Object result, final Class returnClass,
                               final Class entityClass, final boolean projection) {
        final boolean isOptional = Optionals.isOptional(returnClass);
        Object converted = handlePlainValue(result, isOptional ? entityClass : returnClass, projection);
        if (isOptional) {
            // jdk8 or guava optional
            converted = Optionals.create(converted, returnClass);
        }
        return converted;
    }

    private Object handlePlainValue(final Object result, final Class returnClass, final boolean projection) {
        Object converted = null;
        // only update query returns simple number
        if (returnClass.equals(Long.class) && result instanceof Number) {
            converted = ((Number) result).longValue();
        } else if (returnClass.equals(Integer.class) && result instanceof Number) {
            converted = ((Number) result).intValue();
        } else {
            if (result instanceof ORecord) {
                // most likely ResultConverter call (because queries always return collections)
                converted = projection ? flattenSimple(result, returnClass) : result;
            } else {
                // if single type required convert from collection
                // simple single type case will be handled on checking assignment (at the top).
                // No projection to apply it to one element only
                final Iterator it = toIterator(result, returnClass, false);
                if (it.hasNext()) {
                    converted = projection ? flattenSimple(it.next(), returnClass) : it.next();
                }
            }
        }
        return converted;
    }

    @SuppressWarnings("unchecked")
    private Iterator toIterator(final Object result, final Class targetEntity, final boolean projection) {
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
                tmp.add(flattenSimple(res.next(), targetEntity));
            }
            res = tmp.iterator();
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    private Collection convertToCollection(final Object result, final Class returnClass,
                                           final Class targetEntity, final boolean projection) {
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
        final Iterator it = toIterator(result, targetEntity, projection);
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
                    res = handlePlainValue(res, returnClass, true);
                }
            }
        }
        return res;
    }
}

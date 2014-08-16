package ru.vyarus.guice.persist.orient.finder.result;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Primitives;

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
    private static final List<Class> VOID_TYPES = ImmutableList.<Class>of(Void.class, void.class);

    @Override
    @SuppressWarnings("unchecked")
    public Object convert(final ResultDesc desc) {
        final Object result = desc.result;
        // wrap primitive, because result will always be object
        final Class returnClass = desc.returnClass.isPrimitive() ? Primitives.wrap(desc.returnClass) : desc.returnClass;

        if (result == null || VOID_TYPES.contains(returnClass)) {
            // no result expected
            return null;
        }

        if (returnClass.isAssignableFrom(result.getClass())) {
            // no need for conversion
            return result;
        }

        Object converted;

        switch (desc.type) {
            case COLLECTION:
                converted = handleCollection(result, returnClass);
                break;
            case ARRAY:
                converted = handleArray(result, desc.entityClass);
                break;
            case PLAIN:
                converted = handlePlain(result, returnClass);
                break;
            default:
                throw new IllegalStateException("Unsupported return type " + desc.type);
        }

        return converted;
    }

    @SuppressWarnings("unchecked")
    private Object handleCollection(final Object result, final Class returnClass) {
        Object converted;
        if (returnClass.equals(Iterator.class)) {
            converted = toIterator(result);
        } else if (returnClass.isAssignableFrom(List.class)) {
            converted = Lists.newArrayList(toIterator(result));
        } else if (returnClass.isAssignableFrom(Set.class)) {
            converted = Sets.newHashSet(toIterator(result));
        } else if (!returnClass.isInterface()) {
            converted = convertToCollection(result, returnClass);
        } else {
            throw new IllegalStateException(String.format(
                    "Incompatible result type requested %s for conversion from actual result %s",
                    returnClass, result.getClass()));
        }
        return converted;
    }

    private Object handleArray(final Object result, final Class entityType) {
        final Collection res = result instanceof Collection
                ? (Collection) result : convertToCollection(result, ArrayList.class);
        final Object array = Array.newInstance(entityType, res.size());
        int i = 0;
        for (Object obj : res) {
            Array.set(array, i++, obj);
        }
        return array;
    }

    private Object handlePlain(final Object result, final Class returnClass) {
        Object converted;
        if (returnClass.equals(Long.class) && result instanceof Number) {
            converted = ((Number) result).longValue();
        } else if (returnClass.equals(Integer.class) && result instanceof Number) {
            converted = ((Number) result).intValue();
        } else {
            // if single type required convert from collection
            // simple single type case will be handled on checking assignment (at the top)
            converted = toIterator(result).next();
        }
        return converted;
    }

    private Iterator toIterator(final Object result) {
        if (result instanceof Iterator) {
            return (Iterator) result;
        } else if (result instanceof Iterable) {
            return ((Iterable) result).iterator();
        } else {
            throw new IllegalStateException("Can't convert " + result.getClass() + " to iterator");
        }
    }

    @SuppressWarnings("unchecked")
    private Collection convertToCollection(final Object result, final Class returnClass) {
        Collection collection;
        try {
            collection = (Collection) returnClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(
                    "Specified finder's collection class could not be instantiated: " + returnClass, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(
                    "Specified finder's collection class could not be instantiated (do not have access privileges): "
                            + returnClass, e);
        }
        final Iterator it = toIterator(result);
        while (it.hasNext()) {
            collection.add(it.next());
        }
        return collection;
    }
}

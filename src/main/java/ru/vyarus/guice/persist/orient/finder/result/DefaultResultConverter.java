package ru.vyarus.guice.persist.orient.finder.result;

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

    @Override
    @SuppressWarnings("unchecked")
    public Object convert(final ResultDesc desc) {
        final Object result = desc.result;
        // wrap primitive, because result will always be object
        final Class returnClass = desc.returnClass.isPrimitive() ? Primitives.wrap(desc.returnClass) : desc.returnClass;

        if (result == null || returnClass.equals(Void.class) || returnClass.equals(void.class)) {
            return null;
        }

        final boolean isResultCompatible = returnClass.isAssignableFrom(result.getClass());
        if (isResultCompatible) {
            // no need for conversion
            return result;
        }

        Object converted;

        switch (desc.type) {
            case COLLECTION:
                if (returnClass.equals(Iterator.class)) {
                    converted = toIterator(result);
                } else if (returnClass.isAssignableFrom(List.class)) {
                    converted = Lists.newArrayList(toIterator(result));
                } else if (returnClass.isAssignableFrom(Set.class)) {
                    converted = Sets.newHashSet(toIterator(result));
                } else if (!returnClass.isInterface()) {
                    converted = convertToCollection(result, returnClass);
                } else {
                    throw new IllegalStateException("Incompatible result type requested " + returnClass +
                            " for conversion from actual result " + result.getClass());
                }
                break;
            case ARRAY:
                final Collection res = (result instanceof Collection ?
                        (Collection) result : convertToCollection(result, ArrayList.class));
                Object array = Array.newInstance(desc.entityClass, res.size());
                int i = 0;
                for (Object obj : res) {
                    Array.set(array, i++, obj);
                }
                converted = array;
                break;
            case PLAIN:
                if (returnClass.equals(Long.class) && result instanceof Number) {
                    converted = ((Number) result).longValue();
                } else if (returnClass.equals(Integer.class) && result instanceof Number) {
                    converted = ((Number) result).intValue();
                } else {
                    // if single type required convert from collection
                    // simple single type case will be handled on checking assignment (at the top)
                    converted = toIterator(result).next();
                }
                break;
            default:
                throw new IllegalStateException("Unsupported return type " + desc.type);
        }

        return converted;
    }

    private Iterator toIterator(Object result) {
        if (result instanceof Iterator) {
            return (Iterator) result;
        } else if (result instanceof Iterable) {
            return ((Iterable) result).iterator();
        } else {
            throw new IllegalStateException("Can't convert " + result.getClass() + " to iterator");
        }
    }

    @SuppressWarnings("unchecked")
    private Collection convertToCollection(Object result, Class returnClass) {
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

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
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class DefaultResultConverter implements ResultConverter {
    private static final List<Class> VOID_TYPES = ImmutableList.<Class>of(Void.class, void.class);

    @Override
    @SuppressWarnings("unchecked")
    public Object convert(final ResultDesc desc) {
        final Object result = desc.result;
        // wrap primitive, because result will always be object
        final Class returnClass = desc.returnClass.isPrimitive() ? Primitives.wrap(desc.returnClass) : desc.returnClass;

        Object res = null;
        if (result != null && !VOID_TYPES.contains(returnClass)) {
            res = returnClass.isAssignableFrom(result.getClass())
                    ? result : convertResult(desc.type, returnClass, desc.entityClass, result);
        }
        return res;
    }

    private Object convertResult(final ResultType type, final Class returnClass,
                                 final Class entityClass, final Object result) {
        Object converted;
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
                throw new FinderResultConversionException("Unsupported return type " + type);
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
            throw new FinderResultConversionException(String.format(
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
            Array.set(array, i++, obj);
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

    @SuppressWarnings("PMD.UnusedPrivateMethod")
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
            }
        }
        return converted;
    }

    private Iterator toIterator(final Object result) {
        Iterator res;
        if (result instanceof Iterator) {
            res = (Iterator) result;
        } else if (result instanceof Iterable) {
            res = ((Iterable) result).iterator();
        } else {
            throw new FinderResultConversionException("Can't convert " + result.getClass() + " to iterator");
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    private Collection convertToCollection(final Object result, final Class returnClass) {
        Collection collection;
        try {
            collection = (Collection) returnClass.newInstance();
        } catch (InstantiationException e) {
            throw new FinderResultConversionException(
                    "Specified finder's collection class could not be instantiated: " + returnClass, e);
        } catch (IllegalAccessException e) {
            throw new FinderResultConversionException(
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

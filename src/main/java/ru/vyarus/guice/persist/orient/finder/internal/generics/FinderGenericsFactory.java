package ru.vyarus.guice.persist.orient.finder.internal.generics;

import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;

import javax.inject.Singleton;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Analyze finder interface hierarchies in order to resolve extended interfaces generics
 * and allow using resolved generics for query placeholders.
 *
 * @author Vyacheslav Rusakov
 * @since 16.10.2014
 */
@Singleton
public class FinderGenericsFactory {

    private final Map<Class<?>, GenericsDescriptor> hierarchyCache = new MapMaker().weakKeys().makeMap();
    // lock will not affect performance for cached descriptors, just to make sure nothing was build two times
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * @param type finder type to investigate
     * @return descriptor for interface hierarchy generics substitution
     */
    public GenericsDescriptor create(final Class<?> type) {
        GenericsDescriptor descriptor = hierarchyCache.get(type);
        if (descriptor == null) {
            lock.lock();
            try {
                if (hierarchyCache.get(type) != null) {
                    // descriptor could be created while thread wait for lock
                    descriptor = hierarchyCache.get(type);
                } else {
                    descriptor = buildDescriptor(type);
                    // internal check
                    Preconditions.checkState(hierarchyCache.get(type) == null,
                            "Bad concurrency: descriptor already present in cache");
                    hierarchyCache.put(type, descriptor);
                }
            } finally {
                lock.unlock();
            }
        }
        return descriptor;
    }

    private GenericsDescriptor buildDescriptor(final Class<?> type) {
        final GenericsDescriptor descriptor = new GenericsDescriptor();
        descriptor.root = type;
        analyzeType(descriptor, type);
        return descriptor;
    }

    private void analyzeType(final GenericsDescriptor descriptor, final Class<?> type) {
        Class<?> supertype = type;
        while (supertype != null && Object.class != supertype) {
            for (Type iface : supertype.getGenericInterfaces()) {
                if (iface instanceof ParameterizedType) {
                    final ParameterizedType parametrization = (ParameterizedType) iface;
                    final Class interfaceType = (Class) parametrization.getRawType();
                    final Map<String, Type> generics =
                            resolveGenerics(parametrization, descriptor.types.get(supertype));

                    if (generics != null && descriptor.types.containsKey(interfaceType)) {
                        throw new IllegalStateException(String.format(
                                "Duplicate interface %s declaration in hierarchy: "
                                        + "can't properly resolve generics.", interfaceType.getName()));
                    }
                    descriptor.types.put(interfaceType, generics);
                    analyzeType(descriptor, interfaceType);
                } else {
                    analyzeType(descriptor, (Class) iface);
                }
            }
            final Class next = supertype.getSuperclass();
            final Map<String, Type> generics = analyzeParent(supertype, descriptor.types.get(supertype));
            if (generics != null) {
                descriptor.types.put(next, generics);
            }
            supertype = next;
        }
    }

    private Map<String, Type> analyzeParent(final Class type, final Map<String, Type> rootGenerics) {
        Map<String, Type> generics = null;
        final Class parent = type.getSuperclass();
        if (!type.isInterface() && parent != null && parent != Object.class
                && type.getGenericSuperclass() instanceof ParameterizedType) {
            // case: bean finder
            generics = resolveGenerics((ParameterizedType) type.getGenericSuperclass(), rootGenerics);
        }
        return generics;
    }

    private static Map<String, Type> resolveGenerics(final ParameterizedType type,
                                                     final Map<String, Type> rootGenerics) {
        final Map<String, Type> generics = Maps.newHashMap();
        final Type[] genericTypes = type.getActualTypeArguments();
        final Class interfaceType = (Class) type.getRawType();
        final TypeVariable[] genericNames = interfaceType.getTypeParameters();

        final int cnt = genericNames.length;
        for (int i = 0; i < cnt; i++) {
            final Type genericType = genericTypes[i];
            Type resolvedGenericType;
            if (genericType instanceof TypeVariable) {
                // simple named generics resolved to target types
                resolvedGenericType = rootGenerics.get(((TypeVariable) genericType).getName());
            } else {
                // composite generics passed as is
                resolvedGenericType = genericType;
            }
            generics.put(genericNames[i].getName(), resolvedGenericType);
        }
        return generics;
    }
}

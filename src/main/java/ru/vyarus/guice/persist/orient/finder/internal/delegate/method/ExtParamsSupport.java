package ru.vyarus.guice.persist.orient.finder.internal.delegate.method;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import ru.vyarus.guice.persist.orient.finder.delegate.mixin.FinderDb;
import ru.vyarus.guice.persist.orient.finder.delegate.mixin.FinderGeneric;
import ru.vyarus.guice.persist.orient.finder.delegate.mixin.FinderInstance;
import ru.vyarus.guice.persist.orient.finder.internal.FinderDefinitionException;
import ru.vyarus.guice.persist.orient.finder.internal.query.params.ParamsUtils;
import ru.vyarus.java.generics.resolver.context.GenericsContext;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Utility methods for extended delegate parameters support.
 *
 * @author Vyacheslav Rusakov
 * @since 30.10.2014
 */
public final class ExtParamsSupport {
    private static final String INSTANCE = "__FINDER_INSTANCE__";
    private static final String CONNECTION = "__FINDER_CONNECTION__";

    private ExtParamsSupport() {
    }

    public static Map<Integer, String> findSpecialParams(final Method method,
                                                         final Collection<String> genericNames) {
        final Map<Integer, String> extParams = Maps.newHashMap();
        ParamsUtils.process(method, new SpecialParamsVisitor(method, genericNames, extParams), null);
        return extParams;
    }

    public static MethodDescriptor buildExtendedDeclaration(final Method method,
                                                            final Map<Integer, String> extensions,
                                                            final GenericsContext generics,
                                                            final Class<?> finderType) {
        final MethodDescriptor ext = new MethodDescriptor(method);
        final Set<String> usedGenerics = Sets.newHashSet();
        for (Map.Entry<Integer, String> entry : extensions.entrySet()) {
            final int pos = entry.getKey();
            final String value = entry.getValue();
            if (!assignInstance(value, pos, method, ext, finderType)
                    && !assignConnection(value, pos, method, ext)) {
                FinderDefinitionException.check(!usedGenerics.contains(value),
                        "Finder delegate method definition error: duplicate generic parameter "
                                + "'%s' in %s#%s%s",
                        value, method.getDeclaringClass().getName(), method.getName(),
                        Arrays.toString(method.getParameterTypes()));
                if (ext.typeParams == null) {
                    ext.typeParams = Maps.newHashMap();
                }
                ext.typeParams.put(pos, generics.resolveClass(generics.genericsMap().get(value)));
                usedGenerics.add(value);
            }
        }
        ext.extendedParamsPositions = Lists.newArrayList(extensions.keySet());
        return ext;
    }

    private static boolean assignInstance(final String name, final int pos, final Method method,
                                          final MethodDescriptor descriptor, final Class<?> finderType) {
        boolean accepted = false;
        if (INSTANCE.equals(name)) {
            FinderDefinitionException.check(descriptor.instancePosition == null,
                    "Finder delegate method definition error: duplicate instance parameter in %s#%s%s",
                    method.getDeclaringClass().getName(), method.getName(),
                    Arrays.toString(method.getParameterTypes()));
            FinderDefinitionException.check(
                    method.getParameterTypes()[pos].isAssignableFrom(finderType),
                    "Finder delegate method definition error: finder instance parameter "
                            + "is incompatible %s#%s%s",
                    method.getDeclaringClass().getName(), method.getName(),
                    Arrays.toString(method.getParameterTypes()));
            descriptor.instancePosition = pos;
            accepted = true;
        }
        return accepted;
    }

    private static boolean assignConnection(final String name, final int pos, final Method method,
                                            final MethodDescriptor descriptor) {
        boolean accepted = false;
        if (CONNECTION.equals(name)) {
            FinderDefinitionException.check(descriptor.connectionPosition == null,
                    "Finder delegate method definition error: duplicate connection parameter in %s#%s%s",
                    method.getDeclaringClass().getName(), method.getName(),
                    Arrays.toString(method.getParameterTypes()));
            descriptor.connectionPosition = pos;
            accepted = true;
        }
        return accepted;
    }

    public static MethodDescriptor findSingleExtended(final Collection<MethodDescriptor> possibilities) {
        final Collection<MethodDescriptor> extended = Collections2.filter(possibilities,
                new Predicate<MethodDescriptor>() {
                    @Override
                    public boolean apply(@Nonnull final MethodDescriptor input) {
                        return input.isExtended();
                    }
                });
        return extended.size() == 1 ? extended.iterator().next() : null;
    }

    /**
     * Method parameters visitor for special delegate parameters detection.
     */
    private static class SpecialParamsVisitor implements ParamsUtils.ParamVisitor {
        private final Method method;
        private final Collection<String> generics;
        private final Map<Integer, String> extensions;

        public SpecialParamsVisitor(final Method method, final Collection<String> generics,
                                    final Map<Integer, String> extensions) {
            this.method = method;
            this.generics = generics;
            this.extensions = extensions;
        }

        @Override
        public boolean accept(final Annotation annotation, final int position, final Class<?> type) {
            boolean ret = false;
            if (annotation != null) {
                if (FinderGeneric.class.equals(annotation.annotationType())) {
                    final FinderGeneric generic = (FinderGeneric) annotation;
                    FinderDefinitionException.check(method.getParameterTypes()[position].equals(Class.class),
                            "Finder delegate method definition error: generic type parameter "
                                    + "must be Class %s#%s%s",
                            method.getDeclaringClass().getName(), method.getName(),
                            Arrays.toString(method.getParameterTypes()));
                    // looking if calling finder has such generic value
                    // in case of mistype target method would not be found (obvious error)
                    // but as side effect could be used to guide detection in some complex cases
                    if (generics.contains(generic.value())) {
                        extensions.put(position, generic.value());
                        ret = true;
                    }
                } else if (FinderInstance.class.equals(annotation.annotationType())) {
                    // instance type isn't validated here, because this method might not be target method and supposed
                    // to be called by other finder
                    extensions.put(position, INSTANCE);
                    ret = true;
                } else if (FinderDb.class.equals(annotation.annotationType())) {
                    // type isn't checked and error will appear in runtime only!
                    extensions.put(position, CONNECTION);
                    ret = true;
                }
            }
            return ret;
        }
    }
}


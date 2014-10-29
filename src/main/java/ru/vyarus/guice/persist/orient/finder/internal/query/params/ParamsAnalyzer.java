package ru.vyarus.guice.persist.orient.finder.internal.query.params;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static ru.vyarus.guice.persist.orient.finder.internal.FinderDefinitionException.check;

/**
 * Analyze finder method parameters.
 *
 * @author Vyacheslav Rusakov
 * @since 26.09.2014
 */
public final class ParamsAnalyzer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParamsAnalyzer.class);

    private ParamsAnalyzer() {
    }

    @SuppressWarnings("PMD.BooleanInversion")
    public static ParamsDescriptor analyzeParameters(final Method method, final List<Integer> skip) {
        final ParamsContext context = new ParamsContext();
        ParamsUtils.process(method, new ParamsVisitor(context, method), skip);

        final ParamsDescriptor descriptor = new ParamsDescriptor();
        if (context.useOrdinalParams == null) {
            // no-arg method
            descriptor.useNamedParameters = false;
            descriptor.parametersIndex = new Integer[0];
        } else {

            // copy composed data into descriptor
            descriptor.useNamedParameters = !context.useOrdinalParams;
            if (descriptor.useNamedParameters) {
                descriptor.namedParametersIndex = context.namedParams;
            } else {
                final List<Integer> params = context.params;
                descriptor.parametersIndex = params.toArray(new Integer[params.size()]);
            }
        }
        return descriptor;
    }

    private static void bindParam(final String name, final int position,
                                  final ParamsContext context, final Method method) {
        if (context.useOrdinalParams == null) {
            // type of params not recognized yet (recognizing by first parameter - either named or positional)
            context.useOrdinalParams = name == null;
            if (context.useOrdinalParams) {
                context.params = Lists.newArrayList();
            } else {
                context.namedParams = Maps.newHashMap();
            }
        }

        if (context.useOrdinalParams) {
            context.params.add(position);
            if (name != null) {
                // if first parameter without annotation, ignoring all other annotations
                LOGGER.warn("Named parameter {} registered as ordinal. Either annotate all parameters "
                                + "or remove annotations in finder method {}#{}",
                        name, method.getDeclaringClass(), method.getName());
            }
        } else {
            // if first parameter was named all other must be named too (without duplicates)
            check(name != null, "Named parameter not annotated at position %s", position);
            check(!context.namedParams.containsKey(name),
                    "Duplicate parameter %s declaration at position %s", name, position);
            context.namedParams.put(name, position);
        }
    }

    /**
     * Parameters visitor. Assumed to be called after all other custom parameters detected.
     */
    private static class ParamsVisitor implements ParamsUtils.ParamVisitor {
        private final ParamsContext context;
        private final Method method;

        public ParamsVisitor(final ParamsContext context, final Method method) {
            this.context = context;
            this.method = method;
        }

        @Override
        public boolean accept(final Annotation annotation, final int position, final Class<?> type) {
            boolean res = false;
            if (annotation != null) {
                // first parameter defines if we use named or ordinal parameters
                final Class<? extends Annotation> annotationType = annotation.annotationType();
                if (Named.class.equals(annotationType)) {
                    final Named namedAnnotation = (Named) annotation;
                    bindParam(namedAnnotation.value(), position, context, method);
                    res = true;
                } else if (javax.inject.Named.class.equals(annotationType)) {
                    final javax.inject.Named namedAnnotation = (javax.inject.Named) annotation;
                    bindParam(namedAnnotation.value(), position, context, method);
                    res = true;
                }
            } else {
                // positional parameter
                bindParam(null, position, context, method);
            }
            return res;
        }
    }

    /**
     * Finder parameters object, used during analysis.
     */
    @SuppressWarnings({
            "checkstyle:visibilitymodifier",
            "PMD.DefaultPackage"})
    private static class ParamsContext {
        Boolean useOrdinalParams;
        List<Integer> params;
        Map<String, Integer> namedParams;
    }
}

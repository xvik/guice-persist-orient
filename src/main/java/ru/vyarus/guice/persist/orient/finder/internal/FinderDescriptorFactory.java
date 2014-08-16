package ru.vyarus.guice.persist.orient.finder.internal;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.persist.finder.Finder;
import com.google.inject.persist.finder.FirstResult;
import com.google.inject.persist.finder.MaxResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.finder.FinderExecutor;
import ru.vyarus.guice.persist.orient.finder.Use;
import ru.vyarus.guice.persist.orient.finder.result.ResultType;

import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static ru.vyarus.guice.persist.orient.finder.result.ResultType.*;

/**
 * Analyze annotated method and provides descriptor.
 *
 * @author Vyacheslav Rusakov
 * @since 30.07.2014
 */
@Singleton
public class FinderDescriptorFactory {
    private static final List<Class> PRIMITIVE_NUMBERS = ImmutableList.<Class>of(int.class, long.class);

    private final Logger logger = LoggerFactory.getLogger(FinderDescriptorFactory.class);
    private final Map<Method, FinderDescriptor> finderCache = new MapMaker().weakKeys().makeMap();

    private Set<FinderExecutor> executors;
    private FinderExecutor defaultExecutor;

    // lock will not affect performance for cached descriptors, just to make sure nothing was build two times
    private ReentrantLock lock = new ReentrantLock();

    @Inject
    public FinderDescriptorFactory(final Set<FinderExecutor> executors,
                                   @Named("orient.finder.default.connection") final DbType type) {
        this.executors = executors;
        this.defaultExecutor = Preconditions.checkNotNull(find(type),
                "No executor found for type " + type);
    }

    public FinderDescriptor create(final Method method) throws Throwable {
        FinderDescriptor descriptor = finderCache.get(method);
        if (null != descriptor) {
            return descriptor;
        }

        lock.lock();
        try {
            if (finderCache.get(method) != null) {
                return finderCache.get(method);
            }
            final Finder finderAnnotation = method.getAnnotation(Finder.class);

            final String functionName = Strings.emptyToNull(finderAnnotation.namedQuery());
            final String query = Strings.emptyToNull(finderAnnotation.query());
            Preconditions.checkState(Strings.isNullOrEmpty(functionName) || Strings.isNullOrEmpty(query),
                    "Choose what to use function or query, but not both");

            final Class<? extends Collection> returnCollectionType = finderAnnotation.returnAs();

            descriptor = new FinderDescriptor();
            descriptor.functionName = functionName;
            descriptor.query = query;
            descriptor.isFunctionCall = functionName != null;

            analyzeReturnType(method,
                    Collection.class.equals(returnCollectionType) ? null : returnCollectionType,
                    descriptor);
            analyzeExecutor(method, descriptor);
            analyzeParameters(method, descriptor);

            // internal check
            Preconditions.checkState(finderCache.get(method) == null,
                    "Bad concurrency: descriptor already present in cache");
            finderCache.put(method, descriptor);
            return descriptor;
        } finally {
            lock.unlock();
        }
    }

    private void analyzeReturnType(final Method method,
                                   final Class<? extends Collection> returnCollectionType,
                                   final FinderDescriptor descriptor) {
        final Class<?> returnClass = method.getReturnType();

        if (returnCollectionType != null) {
            Preconditions.checkState(returnClass.isAssignableFrom(returnCollectionType),
                    String.format("Requested collection %s is incompatible with method return type %s",
                            returnCollectionType, returnClass));
            descriptor.expectType = returnCollectionType;
        } else {
            descriptor.expectType = returnClass;
        }

        ResultType type;
        Class<?> entityClass;
        if (Collection.class.isAssignableFrom(returnClass)
                || Iterator.class.isAssignableFrom(returnClass)
                || Iterable.class.isAssignableFrom(returnClass)) {
            type = COLLECTION;
            entityClass = resolveRealObjectTypeFromCollection(method.getGenericReturnType(), method);
        } else if (returnClass.isArray()) {
            type = ARRAY;
            entityClass = returnClass.getComponentType();
        } else {
            type = PLAIN;
            entityClass = returnClass;
        }

        descriptor.returnType = type;
        descriptor.returnEntity = entityClass;
    }

    private void analyzeExecutor(final Method method, final FinderDescriptor descriptor) {
        FinderExecutor executor = null;

        // even if annotation set trying to detect to later check compatibility
        for (FinderExecutor support : executors) {
            if (support.accept(descriptor.returnEntity)) {
                executor = support;
                break;
            }
        }

        // @Use annotation
        final DbType requestedType = getRequestedConnectionType(method);

        // annotation guides just ambiguous cases
        if (executor != null && requestedType != null
                && !executor.getType().equals(requestedType)) {
            logger.warn(
                    "@Usa annotation ignored, because correct execution type recognized from return type "
                            + "in finder method {}#{}", method.getDeclaringClass(), method.getName());
        }

        if (executor == null) {
            executor = requestedType != null ? find(requestedType) : defaultExecutor;
            // we may still use default connection here, but better fail because it's configuration error
            // (and behaviour will be more predictable)
        } else {
            // special case, sometimes document connection could be overridden, for example:
            // when querying for fields in object connection, documents returned, but still we can use object connection
            if (executor.getType().equals(DbType.DOCUMENT) && requestedType != null) {
                executor = find(requestedType);
            }
        }
        Preconditions.checkState(executor != null,
                "Executor not found for type set in @Use annotation " + requestedType);
        descriptor.executor = executor;
    }

    private DbType getRequestedConnectionType(final Method method) {
        final Use use = method.getAnnotation(Use.class);
        return use == null ? null : use.value();
    }

    private Class resolveRealObjectTypeFromCollection(final Type returnClass, final Method method) {
        if (returnClass == null || !(returnClass instanceof ParameterizedType)
                || ((ParameterizedType) returnClass).getActualTypeArguments().length == 0) {
            logger.warn(
                    "Can't detect collection entity: no generic set in finder method return type: {}#{}.",
                    method.getDeclaringClass(), method.getName());
            return Object.class;
        }
        final Type[] actual = ((ParameterizedType) returnClass).getActualTypeArguments();
        return (Class) actual[0];
    }

    private void analyzeParameters(final Method method, final FinderDescriptor descriptor) {
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        final ParamsContext context = new ParamsContext();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            // first parameter defines if we use named or ordinal parameters
            final Annotation[] annotations = parameterAnnotations[i];
            boolean processed = false;
            for (Annotation annotation : annotations) {
                if (recognizeParamAnnotation(annotation, i, context, descriptor, method)) {
                    processed = true;
                    break;
                }
            }
            if (!processed) {
                // positional parameter
                bindParam(null, i, context, method);
            }
        }

        if (context.useOrdinalParams == null) {
            // no-arg method
            descriptor.useNamedParameters = false;
            descriptor.parametersIndex = new Integer[0];
            return;
        }

        // copy composed data into descriptor
        descriptor.useNamedParameters = !context.useOrdinalParams;
        if (descriptor.useNamedParameters) {
            descriptor.namedParametersIndex = context.namedParams;
        } else {
            final List<Integer> params = context.params;
            descriptor.parametersIndex = params.toArray(new Integer[params.size()]);
        }
    }

    private boolean recognizeParamAnnotation(final Annotation annotation, final int pos,
                                             final ParamsContext context, final FinderDescriptor descriptor,
                                             final Method method) {
        final Class<? extends Annotation> annotationType = annotation.annotationType();
        if (Named.class.equals(annotationType)) {
            final Named namedAnnotation = (Named) annotation;
            bindParam(namedAnnotation.value(), pos, context, method);
            return true;
        } else if (javax.inject.Named.class.equals(annotationType)) {
            final javax.inject.Named namedAnnotation = (javax.inject.Named) annotation;
            bindParam(namedAnnotation.value(), pos, context, method);
            return true;
        } else if (FirstResult.class.equals(annotationType)) {
            Preconditions.checkState(descriptor.firstResultParamIndex == null,
                    "Duplicate @FirstResult definition");
            descriptor.firstResultParamIndex = pos;
            isNumber(method.getParameterTypes()[pos], "Number must be used as @FirstResult parameter");
            return true;
        } else if (MaxResults.class.equals(annotationType)) {
            Preconditions.checkState(descriptor.maxResultsParamIndex == null,
                    "Duplicate @MaxResults definition");
            descriptor.maxResultsParamIndex = pos;
            isNumber(method.getParameterTypes()[pos], "Number must be used as @MaxResults parameter");
            return true;
        }
        return false;
    }

    private void bindParam(final String name, final int position, final ParamsContext context, final Method method) {
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
                logger.warn("Named parameter {} registered as ordinal. Either annotate all parameters "
                                + "or remove annotations in finder method {}#{}",
                        name, method.getDeclaringClass(), method.getName());
            }
        } else {
            // if first parameter was named all other must be named too (without duplicates)
            Preconditions.checkState(name != null, "Named parameter not annotated at position " + position);
            Preconditions.checkState(context.namedParams.get(name) == null, String.format(
                    "Duplicate parameter %s declaration at position %s", name, position));
            context.namedParams.put(name, position);
        }
    }

    private void isNumber(final Class type, final String message) {
        final boolean isPrimitiveNumber = type.isPrimitive() && PRIMITIVE_NUMBERS.contains(type);
        Preconditions.checkState(isPrimitiveNumber || Number.class.isAssignableFrom(type), message);
    }

    private FinderExecutor find(final DbType type) {
        for (FinderExecutor executor : executors) {
            if (executor.getType().equals(type)) {
                return executor;
            }
        }
        return null;
    }

    /**
     * Finder parameters object, used during analysis.
     */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    private static class ParamsContext {
        Boolean useOrdinalParams;
        List<Integer> params;
        Map<String, Integer> namedParams;
    }
}

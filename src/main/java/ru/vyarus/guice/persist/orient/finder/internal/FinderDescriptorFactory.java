package ru.vyarus.guice.persist.orient.finder.internal;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ru.vyarus.guice.persist.orient.finder.internal.FinderDescriptor.ReturnType.*;

/**
 * @author Vyacheslav Rusakov
 * @since 30.07.2014
 */
public class FinderDescriptorFactory {
    private final static Logger LOGGER = LoggerFactory.getLogger(FinderDescriptorFactory.class);
    private final Map<Method, FinderDescriptor> finderCache = new MapMaker().weakKeys().makeMap();

    private Set<FinderExecutor> executors;
    private FinderExecutor defaultExecutor;

    @Inject
    public FinderDescriptorFactory(Set<FinderExecutor> executors,
                                   @Named("orient.finder.default.connection") DbType type) {
        this.executors = executors;
        this.defaultExecutor = Preconditions.checkNotNull(find(type),
                "No executor found for type " + type);
    }

    public FinderDescriptor create(Method method) throws Throwable {
        FinderDescriptor descriptor = finderCache.get(method);
        if (null != descriptor) {
            return descriptor;
        }

        Finder finderAnnotation = method.getAnnotation(Finder.class);
        String functionName = Strings.emptyToNull(finderAnnotation.namedQuery());
        String query = Strings.emptyToNull(finderAnnotation.query());
        Preconditions.checkState(Strings.isNullOrEmpty(functionName) || Strings.isNullOrEmpty(query),
                "Choose what to use named query or just query, but not both");
        Class<? extends Collection> returnCollectionType = finderAnnotation.returnAs();

        descriptor = new FinderDescriptor();
        descriptor.functionName = functionName;
        descriptor.query = query;
        descriptor.isFunctionCall = (functionName != null);
        // todo annotation to guice pool selection
        // todo bind to used pool in ambigous situation
        analyzeReturnType(method, returnCollectionType, descriptor);
        analyzeParameters(method, descriptor);

        finderCache.put(method, descriptor);
        return descriptor;
    }

    private void analyzeReturnType(Method method, Class<? extends Collection> returnCollectionType, FinderDescriptor descriptor) {
        Class<?> returnClass = method.getReturnType();
        FinderDescriptor.ReturnType type;
        Class<?> analyzingClass;
        if (Collection.class.isAssignableFrom(returnClass)) {
            type = COLLECTION;
            if (!returnCollectionType.equals(Collection.class)) {
                //todo wrong
                Preconditions.checkArgument(returnClass.isAssignableFrom(returnCollectionType),
                        "Return collection " + returnClass.getName() + " is not compatible with requested collection type " +
                                returnCollectionType.getName());
                descriptor.returnCollectionType = returnCollectionType;
            }
            analyzingClass = resolveRealObjectTypeFromCollection(method.getGenericReturnType());
        } else if (returnClass.isArray()) {
            type = ARRAY;
            analyzingClass = returnClass.getComponentType();
        } else {
            type = PLAIN;
            analyzingClass = returnClass;
        }

        descriptor.returnType = type;
        descriptor.returnEntity = analyzingClass;

        DbType requestedType = getRequestedConnectionType(method);

        // todo implement compatibility check between requested type and detected type
        if (requestedType == null) {
            for (FinderExecutor support : executors) {
                if (support.accept(analyzingClass)) {
                    descriptor.executor = support;
                    break;
                }
            }
            if (descriptor.executor == null) {
                descriptor.executor = defaultExecutor;
                LOGGER.trace("No executor found for class {}, retrieved from method return type {}. Using default: {}",
                        analyzingClass, returnClass, defaultExecutor.getType());
            }
        } else {
            for (FinderExecutor support : executors) {
                if (support.getType().equals(requestedType)) {
                    descriptor.executor = support;
                    break;
                }
            }
            Preconditions.checkNotNull(descriptor.executor, "No executor found for specified type "+requestedType);
        }
    }

    private DbType getRequestedConnectionType(Method method) {
        Use use = method.getAnnotation(Use.class);
        return use == null ? null : use.value();
    }

    private Class resolveRealObjectTypeFromCollection(Type returnClass) {
        if (returnClass == null || !(returnClass instanceof ParameterizedType)) {
            LOGGER.warn("No generic provided for return type collection: {}.", returnClass);
            return Object.class;
        }
        Type[] actual = ((ParameterizedType) returnClass).getActualTypeArguments();
        if (actual.length > 0) {
            return (Class) actual[0];
        } else {
            LOGGER.warn("No generic provided for return type collection: {}.", returnClass);
            return Object.class;
        }
    }

    private void analyzeParameters(Method method, FinderDescriptor descriptor) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        ParamsContext context = new ParamsContext();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            // first parameter defines if we use named or ordinal parameters
            Annotation[] annotations = parameterAnnotations[i];
            boolean processed = false;
            for (Annotation annotation : annotations) {
                Class<? extends Annotation> annotationType = annotation.annotationType();
                if (Named.class.equals(annotationType)) {
                    Named namedAnnotation = (Named) annotation;
                    bindParam(namedAnnotation.value(), i, context);
                    processed = true;
                    break;
                } else if (javax.inject.Named.class.equals(annotationType)) {
                    javax.inject.Named namedAnnotation = (javax.inject.Named) annotation;
                    bindParam(namedAnnotation.value(), i, context);
                    processed = true;
                    break;
                } else if (FirstResult.class.equals(annotationType)) {
                    Preconditions.checkArgument(descriptor.firstResultParamIndex == null, "Duplicate first result definition");
                    descriptor.firstResultParamIndex = i;
                    isNumber(method.getParameterTypes()[i], "Number must be used as first result parameter");
                    processed = true;
                    break;
                } else if (MaxResults.class.equals(annotationType)) {
                    Preconditions.checkArgument(descriptor.maxResultsParamIndex == null, "Duplicate max results definition");
                    descriptor.maxResultsParamIndex = i;
                    isNumber(method.getParameterTypes()[i], "Number must be used as max results parameter");
                    processed = true;
                    break;
                }
            }
            if (!processed) {
                bindParam(null, i, context);
            }
        }

        if (context.useOrdinalParams == null) {
            // no-arg method
            descriptor.useNamedParameters = false;
            descriptor.parametersIndex = new Integer[0];
            return;
        }

        descriptor.useNamedParameters = !context.useOrdinalParams;
        if (descriptor.useNamedParameters) {
            descriptor.namedParametersIndex = context.namedParams;
        } else {
            List<Integer> params = context.params;
            descriptor.parametersIndex = params.toArray(new Integer[params.size()]);
        }
    }

    private void bindParam(String name, int position, ParamsContext context) {
        if (context.useOrdinalParams == null) {
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
                LOGGER.warn("Named parameter {} registered as ordinal. Either annotate all parameters or remove annotations", name);
            }
        } else {
            Preconditions.checkNotNull(name, "Named parameter not annotated");
            Preconditions.checkState(context.namedParams.get(name) == null, "Duplicate parameter " + name + "declaration");
            context.namedParams.put(name, position);
        }
    }

    private void isNumber(Class type, String message) {
        Preconditions.checkArgument((type.isPrimitive() && (int.class.equals(type) || long.class.equals(type)))
                || Number.class.isAssignableFrom(type), message);
    }

    private FinderExecutor find(DbType type) {
        for (FinderExecutor executor : executors) {
            if (executor.getType().equals(type)) {
                return executor;
            }
        }
        return null;
    }

    private static class ParamsContext {
        Boolean useOrdinalParams;
        List<Integer> params;
        Map<String, Integer> namedParams;
    }
}

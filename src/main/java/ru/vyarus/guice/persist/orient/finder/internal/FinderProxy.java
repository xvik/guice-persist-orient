package ru.vyarus.guice.persist.orient.finder.internal;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import ru.vyarus.guice.persist.orient.finder.command.SqlCommandDesc;

import javax.inject.Singleton;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static ru.vyarus.guice.persist.orient.finder.internal.FinderDescriptor.ReturnType;

/**
 * @author Vyacheslav Rusakov
 * @since 30.07.2014
 */
@Singleton
public class FinderProxy implements MethodInterceptor {

    private FinderDescriptorFactory factory;
    private Provider<ODatabaseDocumentTx> provider;

    @Inject
    public FinderProxy(FinderDescriptorFactory factory, Provider<ODatabaseDocumentTx> provider) {
        this.factory = factory;
        this.provider = provider;
    }

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {

        //obtain a cached finder descriptor (or create a new one)
        FinderDescriptor descriptor = factory.create(methodInvocation.getMethod());
        Object[] arguments = methodInvocation.getArguments();

        Object result;

        SqlCommandDesc command = new SqlCommandDesc();
        command.isFunctionCall = descriptor.isFunctionCall;
        command.function = descriptor.functionName;
        command.query = descriptor.query;
        command.useNamedParams = descriptor.useNamedParameters;
        if (command.useNamedParams) {
            command.namedParams = getNamedParams(descriptor.namedParametersIndex, arguments);
        } else {
            command.params = getParams(descriptor.parametersIndex, arguments);
        }

        if (descriptor.returnType == ReturnType.COLLECTION || descriptor.returnType == ReturnType.ARRAY) {
            if (descriptor.firstResultParamIndex != null) {
                Number rawStartValue = (Number) arguments[descriptor.firstResultParamIndex];
                Integer startValue = rawStartValue == null ? null : rawStartValue.intValue();
                command.start = Objects.firstNonNull(startValue, 0);
            }

            if (descriptor.maxResultsParamIndex != null) {
                Number rawMaxValue = (Number) arguments[descriptor.maxResultsParamIndex];
                Integer maxValue = rawMaxValue == null ? null : rawMaxValue.intValue();
                command.max = Objects.firstNonNull(maxValue, -1);
            }
        } else if (descriptor.returnType == ReturnType.PLAIN) {
            // limit query for single return result
            command.max = 1;
        }

        result = descriptor.executor.executeQuery(command);

        //depending upon return type, decorate or return the result as is
        if (ReturnType.COLLECTION.equals(descriptor.returnType)) {
            result = getAsCollection(descriptor, (List) result);
        } else if (ReturnType.ARRAY.equals(descriptor.returnType)) {
            List res = (List) result;
            Object array = Array.newInstance(descriptor.returnEntity, res.size());
            for (int i = 0; i < res.size(); i++) {
                Array.set(array, i, res.get(i));
            }
            result = array;
        } else if (result != null && result instanceof Collection && ReturnType.PLAIN.equals(descriptor.returnType)) {
            // if single type required convert from collection
            result = ((Collection) result).iterator().next();
        }

        return result;
    }

    private Object[] getParams(Integer[] positions, Object[] arguments) {
        Object[] res = new Object[positions.length];
        for (int i = 0; i < positions.length; i++) {
            res[i] = arguments[positions[i]];
        }
        return res;
    }

    private Map<String, Object> getNamedParams(Map<String, Integer> positions, Object[] arguments) {
        Map<String, Object> res = Maps.newHashMap();
        for (Map.Entry<String, Integer> entry : positions.entrySet()) {
            res.put(entry.getKey(), arguments[entry.getValue()]);
        }
        return res;
    }

    private Object getAsCollection(FinderDescriptor finderDescriptor, List results) {
        if (finderDescriptor.returnCollectionType == null) {
            return results;
        }
        Collection<?> collection;
        try {
            collection = (Collection) finderDescriptor.returnCollectionType.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(
                    "Specified collection class of Finder's returnAs could not be instantiated: "
                            + finderDescriptor.returnCollectionType, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(
                    "Specified collection class of Finder's returnAs could not be instantiated (do not have access privileges): "
                            + finderDescriptor.returnCollectionType, e);
        }

        collection.addAll(results);
        return collection;
    }
}


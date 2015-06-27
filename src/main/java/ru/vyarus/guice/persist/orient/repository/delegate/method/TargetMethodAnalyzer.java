package ru.vyarus.guice.persist.orient.repository.delegate.method;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import ru.vyarus.guice.persist.orient.repository.core.ext.util.ExtUtils;
import ru.vyarus.guice.persist.orient.repository.core.spi.DescriptorContext;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamInfo;
import ru.vyarus.guice.persist.orient.repository.core.util.RepositoryUtils;
import ru.vyarus.java.generics.resolver.GenericsResolver;
import ru.vyarus.java.generics.resolver.context.GenericsContext;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;

/**
 * Searches for target delegate method.
 *
 * @author Vyacheslav Rusakov
 * @see ru.vyarus.guice.persist.orient.repository.delegate.Delegate for algorithm description
 * @since 21.10.2014
 */
public final class TargetMethodAnalyzer {

    private TargetMethodAnalyzer() {
    }


    public static Method findDelegateMethod(final DescriptorContext context,
                                            final Class<?> target,
                                            final String methodHint) {
        final Method method = context.method;
        final List<Class<?>> params = context.generics.method(method).resolveParameters();
        final List<MatchedMethod> possibilities = findPossibleMethods(params, target, methodHint);
        check(!possibilities.isEmpty(),
                "No matched method found in target bean %s for delegation", target.getName());
        // if method hint wasn't used trying repository method name for guessing
        return resolveMethod(methodHint != null ? null : method.getName(),
                params, possibilities);
    }

    /**
     * Analyze target bean methods, finding all matching (by parameters) methods.
     * If method name was specified, only methods with the same name resolved.
     *
     * @param target target bean type
     * @param params repository method params
     * @param hint   method name hint (may be null)
     * @return descriptor of all matching methods
     */
    private static List<MatchedMethod> findPossibleMethods(final List<Class<?>> params, final Class<?> target,
                                                           final String hint) {
        final List<MatchedMethod> possibilities = Lists.newArrayList();
        // use generics to enforce type checks
        final GenericsContext targetGenerics = GenericsResolver.resolve(target);
        for (Method method : target.getMethods()) {
            // method hint force to check only methods with this name
            final boolean methodHintValid = hint == null || method.getName().equals(hint);
            if (!isAcceptableMethod(method) || !methodHintValid) {
                continue;
            }
            final MatchedMethod matched = analyzeMethod(method, params, targetGenerics);
            if (matched != null) {
                possibilities.add(matched);
            }
        }
        return possibilities;
    }

    @SuppressWarnings({"unchecked", "PMD.AvoidInstantiatingObjectsInLoops"})
    private static MatchedMethod analyzeMethod(final Method method, final List<Class<?>> params,
                                               final GenericsContext targetGenerics) {
        final List<ParamInfo> ordinalParamsInfo = Lists.newArrayList();
        final List<Class<?>> types = targetGenerics.method(method).resolveParameters();
        final Annotation[][] annotations = method.getParameterAnnotations();
        boolean extended = false;
        for (int i = 0; i < types.size(); i++) {
            // ignore extensions (they always add value)
            try {
                if (ExtUtils.findParameterExtension(annotations[i]) == null) {
                    ordinalParamsInfo.add(new ParamInfo(i, types.get(i)));
                } else {
                    extended = true;
                }
            } catch (Exception ex) {
                throw new IllegalStateException(String.format("Error analysing method %s parameter %s",
                        RepositoryUtils.methodToString(method), i), ex);
            }
        }
        MatchedMethod res = null;
        if (isParametersCompatible(params, ordinalParamsInfo)) {
            res = new MatchedMethod(method, ordinalParamsInfo, extended);
        }
        return res;
    }

    /**
     * Allows only public not synthetic methods (e.g. groovy additional methods).
     * Possible super$* methods also not allowed. Also, no need to evaluate bridges: bridged method would be
     * properly recognized.
     *
     * @param method possible delegate method
     * @return true if method is acceptable
     */
    private static boolean isAcceptableMethod(final Method method) {
        final boolean isSynthetic = method.isSynthetic() || method.isBridge() || method.getName().contains("$");
        return Modifier.isPublic(method.getModifiers())
                && !isSynthetic
                && method.getDeclaringClass() != Object.class;
    }

    /**
     * Checking method parameters compatibility.
     *
     * @param params            repository method params to check against
     * @param ordinalParamInfos target method ordinal params (excluding extension parameters)
     * @return true if method is compatible, false otherwise
     */
    @SuppressWarnings("unchecked")
    private static boolean isParametersCompatible(final List<Class<?>> params,
                                                  final List<ParamInfo> ordinalParamInfos) {
        boolean resolution = params.size() == ordinalParamInfos.size();
        if (resolution && !params.isEmpty()) {
            final Iterator<Class<?>> paramsIt = params.iterator();
            final Iterator<ParamInfo> targetIt = ordinalParamInfos.iterator();
            while (paramsIt.hasNext()) {
                final Class<?> type = paramsIt.next();
                final ParamInfo paramInfo = targetIt.next();
                if (!paramInfo.type.isAssignableFrom(type)) {
                    resolution = false;
                    break;
                }
            }
        }
        return resolution;
    }

    /**
     * If more than one possibility found, filter methods using repository method name (if exact method name wasn't
     * specified). If only one method has extended parameters it will be chosen, otherwise filtering
     * possibilities by most specific type and look after that for single method with extended params.
     *
     * @param name          repository method name if exact method hint wasn't specified in annotation or null
     * @param params        repository method parameters
     * @param possibilities all found methods
     * @return descriptor if guessing was successful
     * @throws ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException if method guess fails
     */
    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    private static Method resolveMethod(final String name, final List<Class<?>> params,
                                        final List<MatchedMethod> possibilities) {
        MatchedMethod result;
        if (possibilities.size() == 1) {
            result = possibilities.get(0);
        } else {
            // using repository method name to reduce possibilities
            Collection<MatchedMethod> filtered = MethodFilters.filterByMethodName(possibilities, name);
            checkGuessSuccess(!filtered.isEmpty(), filtered);
            if (filtered.size() == 1) {
                result = filtered.iterator().next();
            } else {
                // extended method has higher priority
                result = MethodFilters.findSingleExtended(filtered);
                if (result == null) {
                    // try to filter using most closest parameters
                    filtered = MethodFilters.filterByClosestParams(filtered, params.size());
                    if (filtered.size() == 1) {
                        result = filtered.iterator().next();
                    } else {
                        result = MethodFilters.findSingleExtended(filtered);
                    }
                }
                checkGuessSuccess(result != null, filtered);
            }
        }
        return result == null ? null : result.method;
    }

    private static void checkGuessSuccess(final boolean condition,
                                          final Collection<MatchedMethod> possibilities) {
        check(condition,
                "Can't detect exact target delegate method. Try to rename repository method to "
                        + "match repository method name or specify exact method using annotation."
                        + "Found possibilities: %s",
                Joiner.on(",").join(Collections2.transform(possibilities, new Function<MatchedMethod, Object>() {
                    @Override
                    public Object apply(@Nonnull final MatchedMethod input) {
                        return RepositoryUtils.methodToString(input.method);
                    }
                })));
    }
}

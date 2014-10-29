package ru.vyarus.guice.persist.orient.finder.internal.delegate.method;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import ru.vyarus.guice.persist.orient.finder.internal.FinderDefinitionException;
import ru.vyarus.guice.persist.orient.finder.internal.generics.GenericsUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Searches for target delegate method. Current limitation: searches only for public methods and doesn't scan
 * class hierarchy.
 *
 * @author Vyacheslav Rusakov
 * @see ru.vyarus.guice.persist.orient.finder.delegate.FinderDelegate for algorithm description
 * @since 21.10.2014
 */
public final class MethodDescriptorAnalyzer {

    private MethodDescriptorAnalyzer() {
    }

    public static MethodDescriptor analyzeMethod(final Method method,
                                                 final Class<?> target,
                                                 final String methodHint,
                                                 final Map<String, Type> generics) {
        final Map<String, Type> finderGenerics = com.google.common.base.Objects.firstNonNull(generics,
                Collections.<String, Type>emptyMap());
        final List<Class<?>> params = GenericsUtils.getMethodParameters(method, finderGenerics);
        final List<MethodDescriptor> possibilities = findPossibilities(target, params, methodHint,
                finderGenerics, method.getDeclaringClass());
        FinderDefinitionException.check(!possibilities.isEmpty(),
                "No matched method found in target bean %s for delegation", target.getName());
        // if method hint wasn't used trying finder method name for guessing
        final MethodDescriptor descriptor = guessMethod(methodHint != null ? null : method.getName(),
                possibilities, params);
        descriptor.target = target;
        return descriptor;
    }

    /**
     * Analyze target bean methods, finding all matching (by parameters) methods.
     * If method name was specified, only methods with the same name resolved.
     *
     * @param target target bean type
     * @param params finder params
     * @param hint   method name hint (may be null)
     * @return descriptor of all matching methods
     */
    private static List<MethodDescriptor> findPossibilities(final Class<?> target,
                                                            final List<Class<?>> params, final String hint,
                                                            final Map<String, Type> generics,
                                                            final Class<?> finderType) {
        final List<MethodDescriptor> possibilities = Lists.newArrayList();
        for (Method method : target.getDeclaredMethods()) {
            // method hint force to check only methods with this name
            final boolean methodHintValid = hint == null || method.getName().equals(hint);
            if (!isAcceptableMethod(method) || !methodHintValid) {
                continue;
            }
            final Map<Integer, String> extParams = ExtParamsSupport.findSpecialParams(method, generics.keySet());
            if (isParametersCompatible(method, params, extParams.keySet())) {
                possibilities.add(
                        buildDescriptor(method, extParams, generics, finderType));
            }
        }
        return possibilities;
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
        return Modifier.isPublic(method.getModifiers())
                && !method.isSynthetic()
                && !method.isBridge()
                && !method.getName().contains("$");
    }

    /**
     * Checking method parameters compatibility.
     *
     * @param method method to check
     * @param params finder params to check against
     * @return true if method is tail compatible, false otherwise
     */
    private static boolean isParametersCompatible(final Method method, final List<Class<?>> params,
                                                  final Collection<Integer> skip) {
        final int cnt = method.getParameterTypes().length;
        boolean resolution = params.isEmpty() && cnt - skip.size() == 0;
        if (cnt == params.size() + skip.size()) {
            int skipCnt = 0;
            for (int i = 0; i < cnt; i++) {
                if (skip.contains(i)) {
                    skipCnt++;
                    continue;
                }
                final Class<?> param = params.get(i - skipCnt);
                final Class<?> valParam = method.getParameterTypes()[i];
                if (!valParam.isAssignableFrom(param)) {
                    break;
                }
                if (i == cnt - 1) {
                    resolution = true;
                }
            }
        }
        return resolution;
    }

    private static MethodDescriptor buildDescriptor(final Method method, final Map<Integer, String> extParams,
                                                    final Map<String, Type> generics,
                                                    final Class<?> finderType) {
        return extParams.isEmpty()
                ? new MethodDescriptor(method)
                : ExtParamsSupport.buildExtendedDeclaration(method, extParams, generics, finderType);
    }


    /**
     * If more than one possibility found, filter methods using finder method name (if exact method name wasn't
     * specified). If only one method has extended parameters it will be chosen, otherwise filtering
     * possibilities by most specific type and look after that for single method with extended params.
     *
     * @param name          finder method name if exact method hint wasn't specified in annotation or null
     * @param possibilities all found methods
     * @param params        finder method parameter types
     * @return descriptor if guessing was successful
     * @throws ru.vyarus.guice.persist.orient.finder.internal.FinderDefinitionException if method guess fails
     */
    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    private static MethodDescriptor guessMethod(final String name, final List<MethodDescriptor> possibilities,
                                                final List<Class<?>> params) {
        MethodDescriptor result;
        if (possibilities.size() == 1) {
            result = possibilities.get(0);
        } else {
            // using finder method name to reduce possibilities
            Collection<MethodDescriptor> filtered = MethodFilters.filterByMethodName(possibilities, name);
            checkGuessSuccess(!filtered.isEmpty(), filtered);
            if (filtered.size() == 1) {
                result = filtered.iterator().next();
            } else {
                // extended method has higher priority
                result = ExtParamsSupport.findSingleExtended(filtered);
                if (result == null) {
                    // try to filter using most closest parameters
                    filtered = MethodFilters.filterByClosestParams(filtered, params.size());
                    if (filtered.size() == 1) {
                        result = filtered.iterator().next();
                    } else {
                        result = ExtParamsSupport.findSingleExtended(filtered);
                    }
                }
                checkGuessSuccess(result != null, filtered);
            }
        }
        return result;
    }

    private static void checkGuessSuccess(final boolean condition,
                                          final Collection<MethodDescriptor> possibilities) {
        FinderDefinitionException.check(condition,
                "Can't detect exact target delegate method: %s. Try to rename finder method  to "
                        + "match target method name or specify exact method using annotation",
                Joiner.on(",").join(Collections2.transform(possibilities, new Function<MethodDescriptor, Object>() {
                    @Override
                    public Object apply(@Nonnull final MethodDescriptor input) {
                        return input.method.getName() + " " + Arrays.toString(input.method.getParameterTypes());
                    }
                })));
    }
}

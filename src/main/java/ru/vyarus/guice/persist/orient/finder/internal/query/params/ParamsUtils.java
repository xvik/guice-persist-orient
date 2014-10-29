package ru.vyarus.guice.persist.orient.finder.internal.query.params;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Utility class to simplify parameters recognition.
 *
 * @author Vyacheslav Rusakov
 * @since 26.09.2014
 */
public final class ParamsUtils {

    private ParamsUtils() {
    }

    /**
     * Visitor is called for each parameter with each parameter annotation and after that without annotation at all.
     * If visitor returns true, other annotations not checked. Assuming visitors will aggregate used positions
     * themselves to avoid re-checks by other visitors.
     *
     * @param method        method to analyze parameters
     * @param visitor       visitor object
     * @param skipPositions optional list of positions to skip
     */
    public static void process(final Method method, final ParamVisitor visitor,
                               final List<Integer> skipPositions) {
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            // skip param
            if (skipPositions != null && skipPositions.contains(i)) {
                continue;
            }
            final Annotation[] annotations = parameterAnnotations[i];
            final Class<?> paramType = method.getParameterTypes()[i];
            boolean processed = false;
            // first trying all parameter annotations
            for (Annotation annotation : annotations) {
                if (visitor.accept(annotation, i, paramType)) {
                    processed = true;
                    break;
                }
            }
            // if annotation not recognized or no annotations defined on parameter,
            // call without annotation (default call)
            if (!processed) {
                visitor.accept(null, i, paramType);
            }
        }
    }

    /**
     * Callback interface for parameter visitor.
     * Parameter annotations checked until visitor accepts parameter.
     */
    public interface ParamVisitor {
        /**
         * Called for each method parameter with every annotation and finally with null instead of annotation to
         * indicate default case.
         *
         * @param annotation parameter annotation
         * @param position   parameter position
         * @param type       parameter ty[e
         * @return true to accept parameter and move to next one
         */
        boolean accept(Annotation annotation, int position, Class<?> type);
    }
}

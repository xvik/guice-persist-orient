package ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext;

import com.google.common.collect.Lists;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.SchemeFieldInit;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.SchemeTypeInit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Scheme extensions utilities.
 *
 * @author Vyacheslav Rusakov
 * @since 07.03.2015
 */
public final class ExtUtils {

    private ExtUtils() {
    }

    /**
     * @param type model type
     * @return all type extensions found or empty list
     */
    public static List<Annotation> findTypeAnnotations(final Class<?> type) {
        return filterAnnotations(SchemeTypeInit.class, type.getAnnotations());
    }

    /**
     * @param field model class field
     * @return all field extension found on field or empty list
     */
    public static List<Annotation> findFieldAnnotations(final Field field) {
        return filterAnnotations(SchemeFieldInit.class, field.getAnnotations());
    }


    private static List<Annotation> filterAnnotations(final Class<? extends Annotation> type,
                                                      final Annotation... annotations) {
        final List<Annotation> res = Lists.newArrayList();
        for (Annotation ann : annotations) {
            if (ann.annotationType().isAnnotationPresent(type)) {
                res.add(ann);
            }
        }
        return res;
    }
}

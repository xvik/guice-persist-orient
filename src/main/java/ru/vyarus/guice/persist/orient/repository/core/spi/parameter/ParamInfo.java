package ru.vyarus.guice.persist.orient.repository.core.spi.parameter;

import java.lang.annotation.Annotation;

/**
 * Method parameter object.
 *
 * @param <T> param extension annotation type
 * @author Vyacheslav Rusakov
 * @since 05.02.2015
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class ParamInfo<T extends Annotation> {

    /**
     * Extension annotation (if available).
     */
    public T annotation;

    /**
     * Parameter position.
     */
    public int position;

    /**
     * Parameter type.
     */
    public Class<?> type;

    public ParamInfo(final T annotation, final int position, final Class<?> type) {
        this.annotation = annotation;
        this.position = position;
        this.type = type;
    }

    public ParamInfo(final int position, final Class<?> type) {
        this.position = position;
        this.type = type;
    }
}

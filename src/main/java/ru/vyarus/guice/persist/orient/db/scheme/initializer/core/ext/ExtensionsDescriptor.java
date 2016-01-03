package ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.FieldExtension;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.TypeExtension;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Model class scheme extensions object.
 *
 * @author Vyacheslav Rusakov
 * @since 04.03.2015
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class ExtensionsDescriptor {

    /**
     * Type extensions found on class.
     */
    public List<Ext<TypeExtension, Class>> type;
    /**
     * Field extensions found on class fields.
     */
    public Multimap<String, Ext<FieldExtension, Field>> fields = LinkedHashMultimap.create();

    /**
     * Extension descriptor object.
     *
     * @param <E> extension type
     * @param <S> extension source object
     */
    @SuppressWarnings("checkstyle:VisibilityModifier")
    public static class Ext<E, S> {
        /**
         * Extension instance.
         */
        public E extension;
        /**
         * Extension annotation.
         */
        public Annotation annotation;
        /**
         * Extension source object.
         */
        public S source;

        public Ext(final E extension, final Annotation annotation, final S source) {
            this.extension = extension;
            this.annotation = annotation;
            this.source = source;
        }
    }
}

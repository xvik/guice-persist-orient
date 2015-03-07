package ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext;

import com.google.common.collect.Lists;
import com.google.inject.Injector;
import ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializationException;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.FieldExtension;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.SchemeFieldInit;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.SchemeTypeInit;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.TypeExtension;
import ru.vyarus.guice.persist.orient.db.util.OrderComparator;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Searches for model class extensions, resolve all found extensions and compose extension descriptor object.
 *
 * @author Vyacheslav Rusakov
 * @since 04.03.2015
 */
@Singleton
public class ExtensionsDescriptorFactory {
    private static final Comparator<ExtensionsDescriptor.Ext<?, ?>> EXT_COMPARATOR = new ExtComparator();

    private final Injector injector;

    @Inject
    public ExtensionsDescriptorFactory(final Injector injector) {
        this.injector = injector;
    }

    /**
     * @param model model class
     * @return model extensions descriptor object
     */
    public ExtensionsDescriptor resolveExtensions(final Class<?> model) {
        final ExtensionsDescriptor desc = new ExtensionsDescriptor();
        desc.type = prepareTypeExtensions(model);
        Collections.sort(desc.type, EXT_COMPARATOR);

        for (Field field : model.getDeclaredFields()) {
            final List<ExtensionsDescriptor.Ext<FieldExtension, Field>> exts = prepareFieldExtensions(field);
            Collections.sort(exts, EXT_COMPARATOR);
            if (!exts.isEmpty()) {
                desc.fields.putAll(field.getName(), exts);
            }
        }
        return desc;
    }

    private List<ExtensionsDescriptor.Ext<TypeExtension, Class>> prepareTypeExtensions(final Class<?> model) {
        final List<ExtensionsDescriptor.Ext<TypeExtension, Class>> res = Lists.newArrayList();
        final List<Annotation> typeExtensions = ExtUtils.findTypeAnnotations(model);
        for (Annotation ann : typeExtensions) {
            res.add(createTypeExtension(ann, model));
        }
        return res;
    }

    private ExtensionsDescriptor.Ext<TypeExtension, Class> createTypeExtension(
            final Annotation ann, final Class<?> model) {
        final Class<? extends TypeExtension> ext = ann.annotationType()
                .getAnnotation(SchemeTypeInit.class).value();
        try {
            return new ExtensionsDescriptor.Ext<TypeExtension, Class>(injector.getInstance(ext), ann, model);
        } catch (Throwable th) {
            throw new SchemeInitializationException(String.format(
                    "Failed to create extension %s declared by annotation @%s on class %s",
                    ext.getSimpleName(), ann.annotationType().getSimpleName(), model.getSimpleName()), th);
        }
    }

    private List<ExtensionsDescriptor.Ext<FieldExtension, Field>> prepareFieldExtensions(final Field field) {
        final List<ExtensionsDescriptor.Ext<FieldExtension, Field>> res = Lists.newArrayList();
        final List<Annotation> fieldExtensions = ExtUtils.findFieldAnnotations(field);
        for (Annotation ann : fieldExtensions) {
            res.add(createFieldExtension(ann, field));
        }
        return res;
    }

    private ExtensionsDescriptor.Ext<FieldExtension, Field> createFieldExtension(
            final Annotation ann, final Field field) {
        final Class<? extends FieldExtension> ext = ann.annotationType()
                .getAnnotation(SchemeFieldInit.class).value();
        try {
            return new ExtensionsDescriptor.Ext<FieldExtension, Field>(injector.getInstance(ext), ann, field);
        } catch (Throwable th) {
            throw new SchemeInitializationException(String.format(
                    "Failed to create extension %s declared by annotation @%s on field %s#%s",
                    ext.getSimpleName(), ann.annotationType().getSimpleName(),
                    field.getDeclaringClass().getSimpleName(), field.getName()), th);
        }
    }

    /**
     * Extensions comparator.
     */
    private static class ExtComparator implements Comparator<ExtensionsDescriptor.Ext<?, ?>> {
        private final OrderComparator orderedComparator = new OrderComparator();

        @Override
        public int compare(final ExtensionsDescriptor.Ext<?, ?> o1, final ExtensionsDescriptor.Ext<?, ?> o2) {
            return orderedComparator.compare(o1.extension, o2.extension);
        }
    }
}

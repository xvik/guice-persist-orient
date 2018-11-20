package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index;

import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.FieldExtension;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Field;

/**
 * {@link Index.List} scheme model field extension.
 *
 * @author Vyacheslav Rusakov
 * @since 09.03.2015
 */
@Singleton
public class MultipleIndexesFiledExtension implements FieldExtension<Index.List> {

    private final IndexFieldExtension extension;

    @Inject
    public MultipleIndexesFiledExtension(final IndexFieldExtension extension) {
        this.extension = extension;
    }

    @Override
    public void beforeRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                   final Field field, final Index.List annotation) {
        for (Index index : annotation.value()) {
            extension.beforeRegistration(db, descriptor, field, index);
        }
    }

    @Override
    public void afterRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                  final Field field, final Index.List annotation) {
        for (Index index : annotation.value()) {
            extension.afterRegistration(db, descriptor, field, index);
        }
    }
}

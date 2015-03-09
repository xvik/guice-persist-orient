package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.FieldExtension;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Field;

/**
 * {@link Indexes} scheme model field extension.
 *
 * @author Vyacheslav Rusakov
 * @since 09.03.2015
 */
@Singleton
public class MultipleIndexesFiledExtension implements FieldExtension<Indexes> {

    private final IndexFieldExtension extension;

    @Inject
    public MultipleIndexesFiledExtension(final IndexFieldExtension extension) {
        this.extension = extension;
    }

    @Override
    public void beforeRegistration(final OObjectDatabaseTx db, final SchemeDescriptor descriptor,
                                   final Field field, final Indexes annotation) {
        for (Index index : annotation.value()) {
            extension.beforeRegistration(db, descriptor, field, index);
        }
    }

    @Override
    public void afterRegistration(final OObjectDatabaseTx db, final SchemeDescriptor descriptor,
                                  final Field field, final Indexes annotation) {
        for (Index index : annotation.value()) {
            extension.afterRegistration(db, descriptor, field, index);
        }
    }
}

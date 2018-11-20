package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index;

import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.TypeExtension;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * {@link CompositeIndex.List} scheme model type extension.
 *
 * @author Vyacheslav Rusakov
 * @since 09.03.2015
 */
@Singleton
public class MultipleIndexesTypeExtension implements TypeExtension<CompositeIndex.List> {

    private final IndexTypeExtension extension;

    @Inject
    public MultipleIndexesTypeExtension(final IndexTypeExtension extension) {
        this.extension = extension;
    }

    @Override
    public void beforeRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                   final CompositeIndex.List annotation) {
        for (CompositeIndex index : annotation.value()) {
            extension.beforeRegistration(db, descriptor, index);
        }
    }

    @Override
    public void afterRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                  final CompositeIndex.List annotation) {
        for (CompositeIndex index : annotation.value()) {
            extension.afterRegistration(db, descriptor, index);
        }
    }
}

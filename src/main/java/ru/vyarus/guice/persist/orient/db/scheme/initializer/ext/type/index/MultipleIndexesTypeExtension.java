package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.TypeExtension;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * {@link CompositeIndexes} scheme model type extension.
 *
 * @author Vyacheslav Rusakov
 * @since 09.03.2015
 */
@Singleton
public class MultipleIndexesTypeExtension implements TypeExtension<CompositeIndexes> {

    private final IndexTypeExtension extension;

    @Inject
    public MultipleIndexesTypeExtension(final IndexTypeExtension extension) {
        this.extension = extension;
    }

    @Override
    public void beforeRegistration(final OObjectDatabaseTx db, final SchemeDescriptor descriptor,
                                   final CompositeIndexes annotation) {
        for (CompositeIndex index : annotation.value()) {
            extension.beforeRegistration(db, descriptor, index);
        }
    }

    @Override
    public void afterRegistration(final OObjectDatabaseTx db, final SchemeDescriptor descriptor,
                                  final CompositeIndexes annotation) {
        for (CompositeIndex index : annotation.value()) {
            extension.afterRegistration(db, descriptor, index);
        }
    }
}

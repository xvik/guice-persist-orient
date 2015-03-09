package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index.drop;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.TypeExtension;

import javax.inject.Singleton;

/**
 * @author Vyacheslav Rusakov
 * @since 09.03.2015
 */
@Singleton
public class DropIndexesTypeExtension implements TypeExtension<DropIndexes> {

    @Override
    public void beforeRegistration(final OObjectDatabaseTx db, final SchemeDescriptor descriptor,
                                   final DropIndexes annotation) {
        for (String index : annotation.value()) {
            db.getMetadata().getIndexManager().dropIndex(index);
        }
    }

    @Override
    public void afterRegistration(final OObjectDatabaseTx db, final SchemeDescriptor descriptor,
                                  final DropIndexes annotation) {
        // not needed
    }
}

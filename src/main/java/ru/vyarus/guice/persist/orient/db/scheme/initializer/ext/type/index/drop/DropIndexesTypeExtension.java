package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index.drop;

import com.orientechnologies.orient.core.index.OIndexManagerProxy;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.TypeExtension;

import javax.inject.Singleton;

/**
 * @author Vyacheslav Rusakov
 * @since 09.03.2015
 */
@Singleton
public class DropIndexesTypeExtension implements TypeExtension<DropIndexes> {
    private final Logger logger = LoggerFactory.getLogger(DropIndexesTypeExtension.class);

    @Override
    public void beforeRegistration(final OObjectDatabaseTx db, final SchemeDescriptor descriptor,
                                   final DropIndexes annotation) {
        for (String index : annotation.value()) {
            final OIndexManagerProxy indexManager = db.getMetadata().getIndexManager();
            if (indexManager.existsIndex(index)) {
                indexManager.dropIndex(index);
                logger.debug("Index {} dropped for type {}", index, descriptor.schemeClass);
            }
        }
    }

    @Override
    public void afterRegistration(final OObjectDatabaseTx db, final SchemeDescriptor descriptor,
                                  final DropIndexes annotation) {
        // not needed
    }
}

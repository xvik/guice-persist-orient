package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index.drop;

import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.index.OIndexManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.TypeExtension;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.util.SchemeUtils;

import javax.inject.Singleton;

/**
 * @author Vyacheslav Rusakov
 * @since 09.03.2015
 */
@Singleton
public class DropIndexesTypeExtension implements TypeExtension<DropIndexes> {
    private final Logger logger = LoggerFactory.getLogger(DropIndexesTypeExtension.class);

    @Override
    public void beforeRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                   final DropIndexes annotation) {
        for (String index : annotation.value()) {
            final OIndexManager indexManager = db.getMetadata().getIndexManager();
            if (indexManager.existsIndex(index)) {
                SchemeUtils.dropIndex(db, index);
                logger.info("Index '{}' dropped for type {}", index, descriptor.schemeClass);
            }
        }
    }

    @Override
    public void afterRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                  final DropIndexes annotation) {
        // not needed
    }
}

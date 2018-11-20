package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.recreate;

import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.TypeExtension;
import ru.vyarus.guice.persist.orient.db.util.Order;

import javax.inject.Singleton;

/**
 * {@link Recreate} scheme model type extension.
 *
 * @author Vyacheslav Rusakov
 * @since 09.03.2015
 */
@Singleton
// should be executed before other extensions
@Order(-10)
public class RecreateTypeExtension implements TypeExtension<Recreate> {
    private final Logger logger = LoggerFactory.getLogger(RecreateTypeExtension.class);

    @Override
    public void beforeRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                   final Recreate annotation) {
        if (!descriptor.initialRegistration) {
            db.getMetadata().getSchema().dropClass(descriptor.schemeClass);
            logger.debug("Model {} scheme dropped for re-creation", descriptor.schemeClass);
            // now its fresh registration
            descriptor.initialRegistration = true;
        }
    }

    @Override
    public void afterRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                  final Recreate annotation) {
        // not needed
    }
}

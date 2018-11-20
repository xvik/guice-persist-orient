package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.notnull;

import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.FieldExtension;

import javax.inject.Singleton;
import java.lang.reflect.Field;

/**
 * {@link ONotNull} scheme model field extension.
 *
 * @author Vyacheslav Rusakov
 * @since 09.03.2015
 */
@Singleton
public class NotNullFieldExtension implements FieldExtension<ONotNull> {
    private final Logger logger = LoggerFactory.getLogger(NotNullFieldExtension.class);

    @Override
    public void beforeRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                   final Field field, final ONotNull annotation) {
        // not needed
    }

    @Override
    public void afterRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                  final Field field, final ONotNull annotation) {
        final String name = field.getName();
        final boolean notnull = annotation.value();
        final OProperty property = db.getMetadata().getSchema()
                .getClass(descriptor.schemeClass).getProperty(name);
        if (property.isNotNull() != notnull) {
            property.setNotNull(notnull);
            logger.debug("Set {}.{} property notnull={}", descriptor.schemeClass, name, notnull);
        }
    }
}

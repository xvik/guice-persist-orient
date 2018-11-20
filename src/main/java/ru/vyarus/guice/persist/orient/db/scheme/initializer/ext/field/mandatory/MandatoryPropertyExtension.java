package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.mandatory;

import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.FieldExtension;

import javax.inject.Singleton;
import java.lang.reflect.Field;

/**
 * {@link Mandatory} scheme model field extension.
 *
 * @author Vyacheslav Rusakov
 * @since 09.03.2015
 */
@Singleton
public class MandatoryPropertyExtension implements FieldExtension<Mandatory> {
    private final Logger logger = LoggerFactory.getLogger(MandatoryPropertyExtension.class);

    @Override
    public void beforeRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                   final Field field, final Mandatory annotation) {
        // not needed
    }

    @Override
    public void afterRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                  final Field field, final Mandatory annotation) {
        final String name = field.getName();
        final boolean mandatory = annotation.value();
        final OProperty property = db.getMetadata().getSchema()
                .getClass(descriptor.schemeClass).getProperty(name);
        if (property.isMandatory() != mandatory) {
            property.setMandatory(mandatory);
            logger.debug("Set {}.{} property mandatory={}", descriptor.schemeClass, name, mandatory);
        }
    }
}

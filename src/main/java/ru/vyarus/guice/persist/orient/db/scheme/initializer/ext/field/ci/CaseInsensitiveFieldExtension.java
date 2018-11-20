package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.ci;

import com.orientechnologies.orient.core.collate.OCaseInsensitiveCollate;
import com.orientechnologies.orient.core.collate.ODefaultCollate;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.FieldExtension;

import javax.inject.Singleton;
import java.lang.reflect.Field;

/**
 * {@link CaseInsensitive} scheme field extension.
 *
 * @author Vyacheslav Rusakov
 * @since 09.06.2015
 */
@Singleton
public class CaseInsensitiveFieldExtension implements FieldExtension<CaseInsensitive> {
    private final Logger logger = LoggerFactory.getLogger(CaseInsensitiveFieldExtension.class);

    @Override
    public void beforeRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                   final Field field, final CaseInsensitive annotation) {
        // not needed
    }

    @Override
    public void afterRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                  final Field field, final CaseInsensitive annotation) {
        final String name = field.getName();
        final boolean ci = annotation.value();
        final String collate = ci ? OCaseInsensitiveCollate.NAME : ODefaultCollate.NAME;
        final OProperty property = db.getMetadata().getSchema()
                .getClass(descriptor.schemeClass).getProperty(name);
        if (!property.getCollate().getName().equals(collate)) {
            property.setCollate(collate);
            logger.debug("Set {}.{} property case insensitive={}", descriptor.schemeClass, name, ci);
        }
    }
}

package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.readonly;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.FieldExtension;

import javax.inject.Singleton;
import java.lang.reflect.Field;

/**
 * {@link Readonly} scheme model field extension.
 *
 * @author Vyacheslav Rusakov
 * @since 09.03.2015
 */
@Singleton
public class ReadonlyFieldExtension implements FieldExtension<Readonly> {
    private final Logger logger = LoggerFactory.getLogger(ReadonlyFieldExtension.class);

    @Override
    public void beforeRegistration(final OObjectDatabaseTx db, final SchemeDescriptor descriptor,
                                   final Field field, final Readonly annotation) {
        // not needed
    }

    @Override
    public void afterRegistration(final OObjectDatabaseTx db, final SchemeDescriptor descriptor,
                                  final Field field, final Readonly annotation) {
        final String name = field.getName();
        final boolean readonly = annotation.value();
        final String type = descriptor.modelClass.getSimpleName();
        final OProperty property = db.getMetadata().getSchema()
                .getClass(type).getProperty(name);
        if (property.isReadonly() != readonly) {
            property.setReadonly(readonly);
            logger.debug("Set {}.{} property readonly={}", type, name, readonly);
        }
    }
}

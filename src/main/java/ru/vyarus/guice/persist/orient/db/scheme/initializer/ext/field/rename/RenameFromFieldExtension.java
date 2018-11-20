package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.rename;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.FieldExtension;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.util.SchemeUtils;
import ru.vyarus.guice.persist.orient.db.util.Order;

import javax.inject.Singleton;
import java.lang.reflect.Field;

import static ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializationException.check;

/**
 * {@link RenamePropertyFrom} scheme model field extension.
 *
 * @author Vyacheslav Rusakov
 * @since 07.03.2015
 */
@Singleton
// property rename should be performed before other extensions
@Order(-10)
public class RenameFromFieldExtension implements FieldExtension<RenamePropertyFrom> {
    private final Logger logger = LoggerFactory.getLogger(RenameFromFieldExtension.class);

    @Override
    public void beforeRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                   final Field field, final RenamePropertyFrom annotation) {
        final String oldName = Strings.emptyToNull(annotation.value().trim());
        final String name = field.getName();
        check(oldName != null, "Old name not specified");
        check(!oldName.equals(name), "Defined old name is the same as current property name: %s", name);
        // if class not registered no need  to rename
        if (!descriptor.initialRegistration) {
            final String modelName = descriptor.schemeClass;
            final OClass scheme = db.getMetadata().getSchema().getClass(modelName);
            // old property not exist - no need to rename
            if (scheme.getProperty(oldName) != null) {
                check(scheme.getProperty(name) == null,
                        "Model property %s.%s already exist and can't be renamed from %s",
                        modelName, name, oldName);
                SchemeUtils.command(db, "alter property %s.%s name %s", modelName, oldName, name);
                logger.debug("Scheme property {}.{} renamed from {}", modelName, name, oldName);
            }
        }
    }

    @Override
    public void afterRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                  final Field field, final RenamePropertyFrom annotation) {
        // not needed
    }
}

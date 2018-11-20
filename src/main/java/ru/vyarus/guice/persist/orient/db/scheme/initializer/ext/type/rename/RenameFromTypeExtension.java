package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.rename;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.TypeExtension;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.util.SchemeUtils;
import ru.vyarus.guice.persist.orient.db.util.Order;

import javax.inject.Singleton;

import static ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializationException.check;

/**
 * {@link RenameFrom} scheme model type extension.
 *
 * @author Vyacheslav Rusakov
 * @since 07.03.2015
 */
@Singleton
// class rename should be performed before other extensions
@Order(-10)
public class RenameFromTypeExtension implements TypeExtension<RenameFrom> {
    private final Logger logger = LoggerFactory.getLogger(RenameFromTypeExtension.class);

    @Override
    public void beforeRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                   final RenameFrom annotation) {
        final String oldName = Strings.emptyToNull(annotation.value().trim());
        final String name = descriptor.schemeClass;
        check(oldName != null, "Old name not specified");
        check(!oldName.equals(name), "Defined old name is the same as current model name: %s", name);
        if (db.getMetadata().getSchema().getClass(oldName) != null) {
            check(descriptor.initialRegistration, "Model class %s already exist and can't be renamed from %s",
                    name, oldName);
            SchemeUtils.command(db, "alter class %s name %s", oldName, name);
            // not a new registration anymore
            descriptor.initialRegistration = false;
            logger.debug("Scheme class {} renamed from {}", name, oldName);
        }
    }

    @Override
    public void afterRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                  final RenameFrom annotation) {
        // not needed
    }
}

package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.rename;

import com.google.common.base.Strings;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
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

    @Override
    public void beforeRegistration(final OObjectDatabaseTx db, final SchemeDescriptor descriptor,
                                   final RenameFrom annotation) {
        final String oldName = Strings.emptyToNull(annotation.value().trim());
        final String name = descriptor.modelClass.getSimpleName();
        check(oldName != null, "Old name not specified");
        check(!oldName.equals(name), "Defined old name is the same as current model name: %s", name);
        if (db.getMetadata().getSchema().getClass(oldName) != null) {
            check(descriptor.initialRegistration, "Model class %s already exist and can't be renamed from %s",
                    name, oldName);
            SchemeUtils.command(db, "alter class %s name %s", oldName, name);
            // not a new registration anymore
            descriptor.initialRegistration = false;
        }
    }

    @Override
    public void afterRegistration(final OObjectDatabaseTx db, final SchemeDescriptor descriptor,
                                  final RenameFrom annotation) {
        // not needed
    }
}

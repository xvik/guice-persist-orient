package ru.vyarus.guice.persist.orient.db.scheme.initializer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.object.metadata.schema.OSchemaProxyObject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializationException;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.ExtensionsDescriptor;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.ExtensionsDescriptorFactory;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.FieldExtension;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.TypeExtension;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.util.SchemeUtils;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

/**
 * Extends default orient scheme initialization with plugins. Very useful for development to quickly update
 * scheme, but in production its better to use some other tool with incremental patch applying.
 * <p>
 * Looks model class hierarchy and process all child classes before root model class processing (bottom to top).
 * So extension annotations will work even if they are defined in lower layers (and not inherited).
 * <p>
 * Two types of extensions supported:
 * {@link ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.TypeExtension} and
 * {@link ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.field.FieldExtension}.
 * Type extension annotations are searched on type level. Filed extensions searched on fields.
 * All extensions are executed twice: before orient registration and after that.
 * <p>
 * Extensions are guice beans. If default (prototype) scope is used on extension bean, then
 * new extension instance will be used for each class registration.
 * <p>
 * To avoid processing same types many times (e.g. some base class, extended by may model classes),
 * all classes are processed just once (cached). To clear case use {@link #clearModelCache()}.
 * <p>
 * Class is not intended to be used in concurrent environment (usually model update is synchronous).
 *
 * @author Vyacheslav Rusakov
 * @since 04.03.2015
 */
@Singleton
@SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
public class ObjectSchemeInitializer {
    private final Set<Class<?>> processingCache = Sets.newHashSet();

    private final Provider<ODatabaseObject> dbProvider;
    private final ExtensionsDescriptorFactory extFactory;

    @Inject
    public ObjectSchemeInitializer(final Provider<ODatabaseObject> dbProvider,
                                   final ExtensionsDescriptorFactory extFactory) {
        this.dbProvider = dbProvider;
        this.extFactory = extFactory;
    }

    /**
     * Register model scheme. If model class extends other classes, superclasses will be processed first
     * (bottom to top). On each class from hierarchy extensions are searched and applied. Processed classes are
     * cashed to avoid multiple processing of the same base model class.
     *
     * @param model model class
     */
    public void register(final Class<?> model) {
        final ODatabaseObject db = dbProvider.get();
        // auto create schema for new classes
        ((OObjectDatabaseTx) db).setAutomaticSchemaGeneration(true);
        // processing lower hierarchy types first
        try {
            for (Class<?> type : Lists.reverse(SchemeUtils.resolveHierarchy(model))) {
                processType(db, type);
            }
        } catch (Throwable th) {
            throw new SchemeInitializationException("Failed to register model class " + model.getName(), th);
        }
    }

    /**
     * Clear initialized models cache. Use if type re-initialization is required.
     */
    public void clearModelCache() {
        processingCache.clear();
    }

    private void processType(final ODatabaseObject db, final Class<?> model) {
        // avoid processing same types
        if (processingCache.contains(model)) {
            return;
        }
        final ExtensionsDescriptor extDesc = extFactory.resolveExtensions(model);

        // synchronize before registration to make sure actual scheme will be used (some classes may be registered
        // manually and not visible in schema yet)
        final OSchemaProxyObject schema = db.getMetadata().getSchema();
        schema.synchronizeSchema();
        schema.reload();
        final SchemeDescriptor desc = buildDescriptor(db, model);

        // synchronization is performed after each extension to make sure local schema model is actual
        // (especially important for remote connection)
        executeBefore(extDesc, desc, db);
        db.getEntityManager().registerEntityClass(model);
        desc.registered = true;
        schema.synchronizeSchema();
        schema.reload();
        executeAfter(extDesc, desc, db);
        processingCache.add(model);
    }

    private SchemeDescriptor buildDescriptor(final ODatabaseObject db, final Class<?> model) {
        final SchemeDescriptor desc = new SchemeDescriptor();
        desc.modelClass = model;
        desc.schemeClass = model.getSimpleName();
        desc.modelHierarchy = SchemeUtils.resolveHierarchy(model);
        desc.modelRootClass = desc.modelHierarchy.get(desc.modelHierarchy.size() - 1);
        desc.initialRegistration = db.getMetadata().getSchema().getClass(model.getSimpleName()) == null;
        desc.registered = false;
        return desc;
    }

    @SuppressWarnings("unchecked")
    private void executeBefore(final ExtensionsDescriptor extDesc, final SchemeDescriptor desc,
                               final ODatabaseObject db) {
        final OSchemaProxyObject schema = db.getMetadata().getSchema();
        for (ExtensionsDescriptor.Ext<TypeExtension, Class> ext : extDesc.type) {
            ext.extension.beforeRegistration(db, desc, ext.annotation);
            schema.reload();
        }
        for (Map.Entry<String, ExtensionsDescriptor.Ext<FieldExtension, Field>> entry
                : extDesc.fields.entries()) {
            final ExtensionsDescriptor.Ext<FieldExtension, Field> ext = entry.getValue();
            ext.extension.beforeRegistration(db, desc, ext.source, ext.annotation);
            schema.reload();
        }
    }

    @SuppressWarnings("unchecked")
    private void executeAfter(final ExtensionsDescriptor extDesc, final SchemeDescriptor desc,
                              final ODatabaseObject db) {
        final OSchemaProxyObject schema = db.getMetadata().getSchema();
        for (ExtensionsDescriptor.Ext<TypeExtension, Class> ext : extDesc.type) {
            ext.extension.afterRegistration(db, desc, ext.annotation);
            schema.reload();
        }
        for (Map.Entry<String, ExtensionsDescriptor.Ext<FieldExtension, Field>> entry
                : extDesc.fields.entries()) {
            final ExtensionsDescriptor.Ext<FieldExtension, Field> ext = entry.getValue();
            ext.extension.afterRegistration(db, desc, ext.source, ext.annotation);
            schema.reload();
        }
    }
}

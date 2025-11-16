package ru.vyarus.guice.persist.orient.db.scheme;

import com.google.inject.Provider;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.serialization.serializer.object.OObjectSerializer;
import com.orientechnologies.orient.object.serialization.OObjectSerializerContext;
import com.orientechnologies.orient.object.serialization.OObjectSerializerHelper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Installs registered custom type serializers (registered in
 * {@link ru.vyarus.guice.persist.orient.OrientModule#withCustomTypes(java.lang.Class[])}).
 * Registration order of serializers is preserved.
 * <p>
 * Registered serializers are resolved using {@code Multibinder<OObjectSerializer>} and so will also install
 * serializers directly configured for multibinder (in some 3rd party module).
 * <p>
 * Serializers are global, but it's ok to call installation multiple times (it will just re-create types context).
 * Normally this must be called automatically before schema initialization.
 *
 * @author Vyacheslav Rusakov
 * @since 25.09.2017
 */
@Singleton
public class CustomTypesInstaller {
    private final Logger logger = LoggerFactory.getLogger(CustomTypesInstaller.class);

    private final Set<OObjectSerializer> customTypes;
    // there can't be object db provider as object database might not be configured (only document and/or graph used)
    // but this class is used in all cases, and so must be generic
    private final Provider<ODatabaseDocument> dbProvider;

    @Inject
    public CustomTypesInstaller(final Set<OObjectSerializer> customTypes,
                                final Provider<ODatabaseDocument> dbProvider) {
        this.customTypes = customTypes;
        this.dbProvider = dbProvider;
    }

    /**
     * Install custom type serializers.
     * <p>
     * Custom types are global, so calling init for each database seems to not have sense. But, when multiple
     * orient databases configured, calling this for each url will grant that custom type classes will be correctly
     * removed from target database (if they were registered).
     */
    public void install() {
        if (!customTypes.isEmpty()) {
            // only URl is required, so document database instance is enough
            final ODatabase db = dbProvider.get();
            final OObjectSerializerContext context = new OObjectSerializerContext();
            for (OObjectSerializer type : customTypes) {
                // only database url is used to de-register custom type classes if any was registered
                // it does not matter if database is actually open or closed
                context.bind(type, db);
                logger.trace("Custom orient type serializer bound: {}", type.getClass().getName());
            }

            // use default (null) context to delegate serializer resolution to context with all custom serializers
            // there is no (good) way to check if null context is already used (in this case it would be overridden)
            // also, no way to prevent null context overriding
            OObjectSerializerHelper.bindSerializerContext(null, context);
            logger.debug("Custom orient type serializers registered: {}", customTypes.size());
        }
    }
}

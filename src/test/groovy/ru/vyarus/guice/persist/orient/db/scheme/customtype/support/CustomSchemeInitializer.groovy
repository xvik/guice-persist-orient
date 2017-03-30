package ru.vyarus.guice.persist.orient.db.scheme.customtype.support

import com.google.inject.Provider
import com.google.inject.matcher.Matchers
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import com.orientechnologies.orient.object.serialization.OObjectSerializerContext
import com.orientechnologies.orient.object.serialization.OObjectSerializerHelper
import ru.vyarus.guice.persist.orient.db.scheme.annotation.Persistent
import ru.vyarus.guice.persist.orient.db.scheme.customtype.support.model.SecurityRole
import ru.vyarus.guice.persist.orient.db.scheme.impl.AbstractObjectInitializer
import ru.vyarus.guice.persist.orient.db.scheme.impl.AutoScanSchemeInitializer
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ObjectSchemeInitializer

import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Compose custom logic with {@link ru.vyarus.guice.persist.orient.db.scheme.impl.AutoScanSchemeInitializer}
 *
 * @author Vyacheslav Rusakov
 * @since 30.03.2017
 */
@Singleton
class CustomSchemeInitializer extends AbstractObjectInitializer {

    final Provider<OObjectDatabaseTx> dbProvider
    final SecurityRoleSerializer serializer

    @Inject
    CustomSchemeInitializer(@Named("orient.model.package") final String appPkgs,
                                     final Provider<OObjectDatabaseTx> dbProvider,
                                     final ObjectSchemeInitializer schemeInitializer,
                            final SecurityRoleSerializer serializer) {
        super(dbProvider, schemeInitializer, Matchers.annotatedWith(Persistent.class), appPkgs.split(","));
        this.dbProvider = dbProvider
        this.serializer = serializer
    }

    @Override
    void initialize() {
        // custom type registration
        final OObjectDatabaseTx db = dbProvider.get();
        // http://orientdb.com/docs/2.2/Object-2-Record-Java-Binding.html
        OObjectSerializerContext context = new OObjectSerializerContext()
        context.bind(serializer, db)
        OObjectSerializerHelper.bindSerializerContext(SecurityRole, context);

        // normal model registration
        super.initialize()
    }
}

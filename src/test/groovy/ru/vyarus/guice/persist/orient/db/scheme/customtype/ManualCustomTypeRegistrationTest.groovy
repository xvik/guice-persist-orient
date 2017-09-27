package ru.vyarus.guice.persist.orient.db.scheme.customtype

import com.google.inject.AbstractModule
import com.google.inject.multibindings.Multibinder
import com.orientechnologies.orient.core.metadata.schema.OType
import com.orientechnologies.orient.core.serialization.serializer.object.OObjectSerializer
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.OrientModule
import ru.vyarus.guice.persist.orient.db.scheme.customtype.support.SecurityRoleSerializer
import ru.vyarus.guice.persist.orient.db.scheme.customtype.support.model.SecurityRole
import ru.vyarus.guice.persist.orient.db.scheme.customtype.support.model.User
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.AutoScanSchemeModule
import ru.vyarus.guice.persist.orient.support.Config
import spock.guice.UseModules

import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author Vyacheslav Rusakov
 * @since 27.09.2017
 */
@UseModules(Mod)
class ManualCustomTypeRegistrationTest extends AbstractTest {

    @Inject
    SecurityRoleSerializer serializer

    def "Check custom type manual installation support"() {

        expect: "saved entity with custom type"
        context.doInTransaction({ OObjectDatabaseTx db ->

            assert db.entityManager.registeredEntities.contains(User)
            assert db.metadata.schema.getClass(User).getProperty('role').type == OType.STRING

            db.save(new User(name: 'secured', role: SecurityRole.ADMIN))
            User res = db.browseClass(User).next()

            assert res.name == 'secured'
            assert res.role == SecurityRole.ADMIN

            true
        } as SpecificTxAction<Boolean, OObjectDatabaseTx>)

        serializer.serializeUsed
        serializer.unserializeUsed
    }

    static class Mod extends AbstractModule {

        @Override
        protected void configure() {
            install(new OrientModule(Config.DB, Config.USER, Config.PASS))
            install(new AutoScanSchemeModule(User.package.name))

            bind(SecurityRoleSerializer).in(Singleton)
            // registering directly to multibinder (with full binding control) instead of OrientModule#withCustomTypes
            Multibinder.newSetBinder(binder(), OObjectSerializer)
                    .addBinding().to(SecurityRoleSerializer)
        }
    }
}
package ru.vyarus.guice.persist.orient.db.scheme

import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.PackageSchemeModule
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 18.07.2014
 */
@UseModules(PackageSchemeModule)
class DefaultModelTest extends AbstractTest {

    def "Check default startup"() {
        given: "schema initialized"
        final Collection<Class<?>> schemaEntries =
                context.doInTransaction({ db ->
                    return db.entityManager.registeredEntities
                } as SpecificTxAction<Collection<Class<?>>, OObjectDatabaseTx>)

        expect: "all model classes in package where loaded"
        schemaEntries.contains(Model);
    }
}

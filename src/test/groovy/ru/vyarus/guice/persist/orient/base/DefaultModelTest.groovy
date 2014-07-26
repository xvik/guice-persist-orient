package ru.vyarus.guice.persist.orient.base

import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.base.model.Model
import ru.vyarus.guice.persist.orient.base.model.ModelAuto
import ru.vyarus.guice.persist.orient.base.modules.SimpleModule
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 18.07.2014
 */
@UseModules(SimpleModule.class)
class DefaultModelTest extends AbstractTest {

    def "Check default startup"() {
        given: "schema initialized"
        final Collection<Class<?>> schemaEntries =
                template.doInTransaction({ db ->
                    return db.entityManager.registeredEntities
                } as SpecificTxAction<Collection<Class<?>>, OObjectDatabaseTx>)

        expect: "all model classes in package where loaded"
        schemaEntries.contains(Model);
        schemaEntries.contains(ModelAuto);
    }
}

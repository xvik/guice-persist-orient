package ru.vyarus.guice.persist.orient.base

import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.model.ModelAuto
import ru.vyarus.guice.persist.orient.support.modules.AutoScanModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 18.07.2014
 */
@UseModules(AutoScanModule)
class AutoScanModelTest extends AbstractTest {

    def "Check autoscan startup"() {
        final Collection<Class<?>> schemaEntries =
                template.doInTransaction({ db ->
                    return db.entityManager.registeredEntities
                } as SpecificTxAction<Collection<Class<?>>, OObjectDatabaseTx>)

        expect: "only annotated model loaded"
        schemaEntries.contains(ModelAuto)
        !schemaEntries.contains(Model)
    }
}

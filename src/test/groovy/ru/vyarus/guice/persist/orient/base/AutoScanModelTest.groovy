package ru.vyarus.guice.persist.orient.base

import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.base.model.Model
import ru.vyarus.guice.persist.orient.base.model.ModelAuto
import ru.vyarus.guice.persist.orient.base.modules.AutoScanModule
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 18.07.2014
 */
@UseModules(AutoScanModule.class)
class AutoScanModelTest extends AbstractTest{

    def "Check autoscan startup"() {
        final Collection<Class<?>> schemaEntries =
                template.doInTransaction({ db ->
                    return db.entityManager.registeredEntities
                } as SpecificTxAction<Collection<Class<?>>, OObjectDatabaseTx>)

        expect:
        schemaEntries.contains(ModelAuto.class)
        !schemaEntries.contains(Model.class)
    }
}

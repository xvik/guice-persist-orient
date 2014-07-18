package ru.vyarus.guice.persist.orient.base

import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.base.model.Model
import ru.vyarus.guice.persist.orient.base.model.ModelAuto
import ru.vyarus.guice.persist.orient.base.modules.AutoScanModule
import ru.vyarus.guice.persist.orient.template.TransactionalAction
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 18.07.2014
 */
@UseModules(AutoScanModule.class)
class AutoScanModelTest extends AbstractTest{

    def "Check autoscan startup"() {
        final Collection<Class<?>> schemaEntries =
                template.doWithTransaction({ db ->
                    return db.entityManager.registeredEntities
                } as TransactionalAction<Collection<Class<?>>>)

        expect:
        schemaEntries.contains(ModelAuto.class)
        !schemaEntries.contains(Model.class)
    }
}

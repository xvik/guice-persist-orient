package ru.vyarus.guice.persist.orient.base

import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.BootstrappedModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 28.07.2014
 */
@UseModules(BootstrappedModule)
class InitTest extends AbstractTest {

    def "Check bootstrap"() {
        long cnt = context.doInTransaction({ db ->
            db.countClass(Model.class)
        } as SpecificTxAction<Long, OObjectDatabaseTx>)
        expect: "check db initialized by DataInitializer"
        cnt > 0
    }
}
package ru.vyarus.guice.persist.orient.repository.command.script

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.repository.RepositoryException
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 25.02.2015
 */
@UseModules(RepositoryTestModule)
class ScriptsExecutionTest extends AbstractTest {

    @Inject
    ScriptCases dao

    def "Check script methods"() {

        context.doInTransaction({ db ->
            db.save(new Model(name: "first", nick: 'done'))
        } as SpecificTxAction)
//
        when: "executing simple script"
        def res = dao.nick()
        then: "ok"
        res == 'done'

        when: "executing simple script with inline transaction"
        res = dao.nickUnderTransaction()
        then: "ok"
        res == 'done'

        when: "executing js script"
        dao.jsScript()
        res = context.doInTransaction({ db ->
            db.countClass(Model)
        } as SpecificTxAction)
        then: "ok"
        res == 1001

        when: "positional params"
        dao.positional('test')
        then: "ok"
        true
    }
}
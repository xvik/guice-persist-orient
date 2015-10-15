package ru.vyarus.guice.persist.orient.repository.command.ext.timeout

import com.google.inject.Inject
import com.orientechnologies.orient.core.command.OCommandContext
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.repository.RepositoryException
import ru.vyarus.guice.persist.orient.repository.command.ext.timeout.support.TimeoutCases
import ru.vyarus.guice.persist.orient.repository.command.ext.timeout.support.ext.TimeoutCheckExtension
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.BootstrapModule
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 24.02.2015
 */
@UseModules(RepositoryTestModule)
class TimeoutExecutionTest extends AbstractTest {

    @Inject
    TimeoutCases dao

    def "Check timeout assignment"() {

        setup:
        context.doInTransaction({db ->
            1000.times {db.save(new Model(name: 'name'+it))}
        } as SpecificTxAction)

        // important to go first to avoid cache
        when: "executing with too short timeout"
        TimeoutCheckExtension.expected = new TimeoutDescriptor(1, OCommandContext.TIMEOUT_STRATEGY.RETURN)
        List<Model> res = dao.neverDone()
        then: "seems to be impossible to trigger timeout with local connection"
        res.size() < 1000

        when: "checking that timeout check works"
        TimeoutCheckExtension.expected = null
        dao.all()
        then: 'check failed'
        thrown(RepositoryException)

        when: "executing with timeout"
        TimeoutCheckExtension.expected = new TimeoutDescriptor(200, OCommandContext.TIMEOUT_STRATEGY.EXCEPTION)
        res = dao.all()
        then: "success"
        res.size() == 10

        when: "executing zero timeout"
        TimeoutCheckExtension.expected = new TimeoutDescriptor(0, null)
        res = dao.noTimeout()
        then: "timeout not applied"
        res.size() == 10
    }
}
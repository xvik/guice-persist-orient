package ru.vyarus.guice.persist.orient.repository.command

import com.google.inject.Inject
import com.orientechnologies.orient.core.record.impl.ODocument
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.repository.command.support.CustomResultCases
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 14.02.2015
 */
@UseModules(RepositoryTestModule)
class CustomResultExecutionTest extends AbstractTest {

    @Inject
    CustomResultCases dao

    def "Check wrapped results"() {

        context.doInTransaction({ db ->
            2.times {
                db.save(new Model(name: "name$it", nick: "nick$it"))
            }
        } as SpecificTxAction)

        when: "count call"
        ODocument count = dao.getCount()
        then: "single document returned"
        count.field('count') == 2

        when: "single field select"
        List<ODocument> res = dao.getNames()
        then: "list of documents returned"
        res.size() == 2
        res[0].field('name') == 'name0'

        when: "single field select as array"
        ODocument[] res2 = dao.getNamesArray()
        then: "array of documents returned"
        res2.length == 2
        res[0].field('name') == 'name0'
    }
}
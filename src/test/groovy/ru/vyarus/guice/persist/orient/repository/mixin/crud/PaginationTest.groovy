package ru.vyarus.guice.persist.orient.repository.mixin.crud

import com.google.inject.Inject
import com.orientechnologies.orient.core.record.impl.ODocument
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.repository.RepositoryException
import ru.vyarus.guice.persist.orient.repository.mixin.crud.support.DocumentDao
import ru.vyarus.guice.persist.orient.repository.mixin.crud.support.ObjectDao
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import ru.vyarus.guice.persist.orient.support.repository.mixin.pagination.Page
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 13.02.2015
 */
@UseModules(RepositoryTestModule)
class PaginationTest extends AbstractTest {

    @Inject
    ObjectDao objectDao
    @Inject
    DocumentDao documentDao

    def "Test pagination mixin"() {

        setup:
        context.transactionManager.begin()
        context.doInTransaction({ db ->
            10.times {
                db.save(new Model(name: "name$it", nick: "nick$it"))
            }
        } as SpecificTxAction)

        when: "getting page"
        Page<Model> page = objectDao.getPage(1, 3)
        then:
        page.currentPage == 1
        page.pageSize == 3
        page.totalCount == 10
        page.totalPages == 4
        page.content.size() == 3
        page.content[0].name == 'name0'

        when: "getting page 2"
        page = objectDao.getPage(2, 3)
        then:
        page.currentPage == 2
        page.pageSize == 3
        page.totalCount == 10
        page.totalPages == 4
        page.content.size() == 3
        page.content[0].name == 'name3'

        when: "getting page 2"
        page = objectDao.getPage(4, 3)
        then:
        page.currentPage == 4
        page.content.size() == 1
        page.content[0].name == 'name9'

        when: "calling bad page"
        objectDao.getPage(5, 3)
        then: "bad page exception"
        thrown(RepositoryException)

        when: "selecting all records"
        List<Model> all = objectDao.getAll(0, -1);
        then: "all selected"
        all.size() == 10

        when: "selecting non existent range"
        all = objectDao.getAll(20, -1);
        then: "nothing selected"
        all.size() == 0

        when: "getting documents page"
        Page<ODocument> page2 = documentDao.getPage(1, 3)
        then:
        page2.currentPage == 1
        page2.pageSize == 3
        page2.totalCount == 10
        page2.totalPages == 4
        page2.content.size() == 3
        page2.content[0].field('name') == 'name0'

        cleanup:
        context.transactionManager.end()
    }
}
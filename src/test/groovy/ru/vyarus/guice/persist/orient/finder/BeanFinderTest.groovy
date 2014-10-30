package ru.vyarus.guice.persist.orient.finder

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.finder.BeanFinder
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.TestFinderModule
import spock.guice.UseModules
import spock.lang.Specification


/**
 * @author Vyacheslav Rusakov 
 * @since 03.08.2014
 */
@UseModules(TestFinderModule)
class BeanFinderTest extends AbstractTest {

    @Inject
    BeanFinder finder

    def "Test bean finder"() {

        template.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "calling finder method in bean"
        List<Model> res = finder.selectAll()
        then: "finder detected and executed"
        res.size() == 1

        when: "calling finder delegate method"
        res = finder.selectAllAsArray()
        then: "delegate call detected and executed"
        res.size() == 1

    }
}
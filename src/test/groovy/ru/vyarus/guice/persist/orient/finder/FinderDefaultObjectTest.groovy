package ru.vyarus.guice.persist.orient.finder

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.finder.InterfaceFinder
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.FinderDefaultObjectModule
import spock.guice.UseModules
import spock.lang.Specification


/**
 * Tests for recognition ambiguous cases, when object db must be used instead of document.
 * @author Vyacheslav Rusakov 
 * @since 02.08.2014
 */
@UseModules(FinderDefaultObjectModule)
class FinderDefaultObjectTest extends AbstractTest {

    @Inject
    InterfaceFinder finder

    def "Check default connection"() {
        template.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "select wil chose default connection, it should be object"
        List<Model> res = finder.selectAllNoType();
        then:
        res.size() == 1

    }
}
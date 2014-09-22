package ru.vyarus.guice.persist.orient.finder

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.finder.FinderWithPlaceholders
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.AutoScanFinderTestModule
import spock.guice.UseModules
import spock.lang.Specification


/**
 * Check queries execution with placeholders.
 *
 * @author Vyacheslav Rusakov 
 * @since 22.09.2014
 */
@UseModules(AutoScanFinderTestModule)
class FinderPlaceholdersExecutionTest extends AbstractTest {

    @Inject
    FinderWithPlaceholders finder

    def "Check placeholders"() {
        template.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "select by dynamic field"
        Model res = finder.findByField("name", 'John');
        then:
        res
    }
}
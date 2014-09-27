package ru.vyarus.guice.persist.orient.finder

import com.google.inject.Inject
import com.orientechnologies.orient.core.sql.OCommandSQL
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.finder.internal.FinderExecutionException
import ru.vyarus.guice.persist.orient.support.finder.FinderWithPlaceholders
import ru.vyarus.guice.persist.orient.support.finder.PlaceholdersEnum
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.AutoScanFinderTestModule
import spock.guice.UseModules

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
            db.command(new OCommandSQL("CREATE FUNCTION funcname \"select from Model\" LANGUAGE SQL ")).execute();
        } as SpecificTxAction)

        when: "select by dynamic field"
        Model res = finder.findByField("name", 'John');
        then: "found"
        res

        when: "select by two dynamic fields"
        res = finder.findByTwoFields("name", "nick", 'John', 'Doe');
        then: "found"
        res

        when: "select by two dynamic fields"
        res = finder.findByEnumField(PlaceholdersEnum.NAME as PlaceholdersEnum, 'John');
        then: "found"
        res

        when: "select by dynamic function"
        List<Model> res2 = finder.functionWithPlaceholder('name');
        then: "found"
        res2.size() > 0

        when: "select by dynamic function with enum name"
        res2 = finder.functionWithPlaceholderEnum(PlaceholdersEnum.NAME as PlaceholdersEnum);
        then: "found"
        res2.size() > 0
    }

    def "Check incorrect params"() {
        template.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "wrong placeholder name"
        finder.findByField("bad", 'John');
        then: "mistype catches"
        thrown(IllegalArgumentException)

        when: "null instead of placeholder"
        finder.findByField(null, 'John');
        then: "mistype catches"
        thrown(IllegalArgumentException)

        when: "bad placeholder with no defaults defined"
        finder.functionWithPlaceholder('bad');
        then: "mistype not catches and failed inside orient"
        thrown(FinderExecutionException)
    }
}
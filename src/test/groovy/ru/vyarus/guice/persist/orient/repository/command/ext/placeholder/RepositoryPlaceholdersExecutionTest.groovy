package ru.vyarus.guice.persist.orient.repository.command.ext.placeholder

import com.google.inject.Inject
import com.orientechnologies.orient.core.sql.OCommandSQL
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.repository.command.ext.placeholder.support.RepositoryWithPlaceholders
import ru.vyarus.guice.persist.orient.repository.command.ext.placeholder.support.PlaceholdersEnum
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * Check queries execution with placeholders.
 *
 * @author Vyacheslav Rusakov 
 * @since 22.09.2014
 */
@UseModules(RepositoryTestModule)
class RepositoryPlaceholdersExecutionTest extends AbstractTest {

    @Inject
    RepositoryWithPlaceholders repository

    def "Check placeholders"() {
        context.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
            db.command(new OCommandSQL("CREATE FUNCTION funcname \"select from Model\" LANGUAGE SQL ")).execute();
        } as SpecificTxAction)

        when: "select by dynamic field"
        Model res = repository.findByField("name", 'John');
        then: "found"
        res

        when: "select by two dynamic fields"
        res = repository.findByTwoFields("name", "nick", 'John', 'Doe');
        then: "found"
        res

        when: "select by two dynamic fields"
        res = repository.findByEnumField(PlaceholdersEnum.NAME as PlaceholdersEnum, 'John');
        then: "found"
        res

        when: "select by dynamic function"
        List<Model> res2 = repository.functionWithPlaceholder('name');
        then: "found"
        res2.size() > 0

        when: "select by dynamic function with enum name"
        res2 = repository.functionWithPlaceholderEnum(PlaceholdersEnum.NAME as PlaceholdersEnum);
        then: "found"
        res2.size() > 0
    }

    def "Check incorrect params"() {
        context.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "wrong placeholder name"
        repository.findByField("bad", 'John');
        then: "mistype catches"
        thrown(IllegalStateException)

        when: "null instead of placeholder"
        repository.findByField(null, 'John');
        then: "mistype catches"
        thrown(IllegalStateException)

        when: "bad placeholder with no defaults defined"
        repository.functionWithPlaceholder('bad');
        then: "mistype not catches and failed inside orient"
        thrown(IllegalStateException)
    }
}
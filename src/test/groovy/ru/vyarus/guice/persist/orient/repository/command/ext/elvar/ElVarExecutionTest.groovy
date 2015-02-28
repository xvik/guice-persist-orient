package ru.vyarus.guice.persist.orient.repository.command.ext.elvar

import com.google.inject.Inject
import com.orientechnologies.orient.core.sql.OCommandSQL
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.repository.RepositoryException
import ru.vyarus.guice.persist.orient.repository.command.ext.elvar.support.ElVarsCases
import ru.vyarus.guice.persist.orient.repository.command.ext.elvar.support.ObjVar
import ru.vyarus.guice.persist.orient.repository.command.ext.elvar.support.VarDefinitionEnum
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
class ElVarExecutionTest extends AbstractTest {

    @Inject
    ElVarsCases repository

    def "Check placeholders"() {
        context.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
            db.command(new OCommandSQL("CREATE FUNCTION funcname \"select from Model\" LANGUAGE SQL ")).execute();
            db.command(new OCommandSQL("CREATE FUNCTION func1 \"select from Model\" LANGUAGE SQL ")).execute();
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
        res = repository.findByEnumField(VarDefinitionEnum.NAME as VarDefinitionEnum, 'John');
        then: "found"
        res

        when: "select by dynamic function"
        List<Model> res2 = repository.functionWithPlaceholder('name');
        then: "found"
        res2.size() > 0

        when: "select by dynamic function with enum name"
        res2 = repository.functionWithEnum(VarDefinitionEnum.NAME as VarDefinitionEnum);
        then: "found"
        res2.size() > 0

        when: "safe string execution"
        res2 = repository.safeString("name");
        then: "found"
        res2.size() > 0

        when: "int var"
        res2 = repository.intVar(1);
        then: "found"
        res2.size() > 0

        when: "integer var"
        res2 = repository.integerVar(1);
        then: "found"
        res2.size() > 0


        when: "object var"
        res2 = repository.objVar(new ObjVar(value: '1'));
        then: "found"
        res2.size() > 0

        when: "class var"
        res2 = repository.classVar(Model.class);
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
        thrown(RepositoryException)

        when: "null instead of placeholder"
        repository.findByField(null, 'John');
        then: "mistype catches"
        thrown(RepositoryException)

        when: "bad placeholder with no defaults defined"
        repository.functionWithPlaceholder('bad');
        then: "mistype not catches and failed inside orient"
        thrown(RepositoryException)
    }
}
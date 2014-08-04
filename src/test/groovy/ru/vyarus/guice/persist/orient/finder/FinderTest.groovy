package ru.vyarus.guice.persist.orient.finder

import com.google.inject.Inject
import com.orientechnologies.orient.core.exception.OCommandExecutionException
import com.orientechnologies.orient.core.record.impl.ODocument
import com.orientechnologies.orient.core.sql.OCommandSQL
import com.tinkerpop.blueprints.Vertex
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.finder.InterfaceFinder
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.TestFinderModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 31.07.2014
 */
@UseModules(TestFinderModule)
class FinderTest extends AbstractTest {

    @Inject
    InterfaceFinder finder

    def "Check selects"() {
        template.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "object select"
        List<Model> res = finder.selectAll();
        then:
        res.size() == 1

        when: "object select with array conversion"
        Model[] resArr = finder.selectAllAsArray();
        then:
        resArr.length == 1

        when: "object select with single result"
        Model model = finder.selectUnique();
        then:
        model != null
        model.version != null
        model.id != null

        when: "document select"
        List<ODocument> resDoc = finder.selectAllAsDocument();
        then:
        resDoc.size() == 1

        when: "graph select"
        List<Vertex> resVert = finder.selectAllAsVertex();
        then:
        resVert.size() == 1
    }

    def "Select without type"() {

        template.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "document select (no generic)"
        List resGeneric = finder.selectAllNoType();
        then:
        resGeneric.size() == 1

        when: "document select (no generic)"
        List<ODocument> resDoc = finder.selectAllNoType();
        then:
        resDoc.size() == 1

    }

    def "Check params binding"() {

        template.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "positional params"
        List<Model> res = finder.parametersPositional('John', 'Doe');
        then:
        res.size() == 1

        when: "positional params no result"
        res = finder.parametersPositional('John', 'Doooooe');
        then:
        res.size() == 0

        when: "named params"
        res = finder.parametersNamed('John', 'Doe');
        then:
        res.size() == 1

        when: "named params no results"
        res = finder.parametersNamed('John', 'Doooooe');
        then:
        res.size() == 0

        when: "positional params with warning"
        res = finder.parametersPositionalWithWarning('John', 'Doe');
        then:
        res.size() == 1

        when: "named params wrong declaration"
        res = finder.parametersNames('John', 'Doe');
        then: "error"
        thrown(IllegalStateException)

        when: "named params with duplicate"
        res = finder.parametersNamesDuplicateName('John', 'Doe');
        then: "error"
        thrown(IllegalStateException)

        when: "paged select"
        res = finder.parametersPaged('John', 'Doe', 0, 1);
        then:
        res.size() == 1

        when: "paged select with objects"
        res = finder.parametersPagedObject('John', 'Doe', 0, 1);
        then:
        res.size() == 1

        when: "paged select with wrong page definition"
        res = finder.parametersPagedDouble('John', 'Doe', 0, 1);
        then: "error"
        thrown(IllegalStateException)

        when: "paged select with wrong type"
        res = finder.parametersPagedWrongType('John', 'Doe', '0', 1);
        then: "error"
        thrown(IllegalStateException)

        when: "paged select with other wrong type"
        res = finder.parametersPagedWrongType2('John', 'Doe', 0, '1');
        then: "error"
        thrown(IllegalStateException)

        when: "paged select with null object"
        res = finder.parametersPagedObject('John', 'Doe', null, 1);
        then:
        res.size() == 1

        when: "paged select with other null object"
        res = finder.parametersPagedObject('John', 'Doe', 0, null);
        then:
        res.size() == 1
    }

    def "Check pagination"() {

        template.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe', cnt: 1))
            db.save(new Model(name: 'John', nick: 'Doe', cnt: 2))
            db.save(new Model(name: 'John', nick: 'Doe', cnt: 3))
        } as SpecificTxAction)

        when: "paged select"
        List<Model> res = finder.parametersPaged('John', 'Doe', 0, 1);
        then:
        res.size() == 1
        res[0].cnt == 1

        when: "paged select with shift"
        res = finder.parametersPaged('John', 'Doe', 1, 1);
        then:
        res.size() == 1
        res[0].cnt == 2

        when: "paged select with shift and larger size"
        res = finder.parametersPaged('John', 'Doe', 1, 2);
        then:
        res.size() == 2
        res[0].cnt == 2
        res[1].cnt == 3

        when: "paged select with defaults"
        res = finder.parametersPaged('John', 'Doe', 0, -1);
        then:
        res.size() == 3

        when: "paged select with defaults objects"
        res = finder.parametersPagedObject('John', 'Doe', null, null);
        then:
        res.size() == 3
    }

    def "Check update"() {

        template.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "object updated and selected"
        finder.update();
        Model model = finder.selectUnique()
        then:
        model != null
        model.name == 'changed'
    }

    def "Check update with count"() {

        template.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "object updated and selected"
        int cnt = finder.updateWithCount();
        Model model = finder.selectUnique()
        then:
        cnt == 1
        model != null
        model.name == 'changed'
    }

    def "Check update with count as object"() {

        template.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "object updated and selected"
        Integer cnt = finder.updateWithCountObject();
        Model model = finder.selectUnique()
        then:
        cnt == 1
        model != null
        model.name == 'changed'
    }

    def "Check manual connection type definition"() {
        template.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "object updated and selected"
        Model model
        template.doInTransaction({db ->
            // execute both in single transaction to make sure object connection used
            // (if document used for update, select would not see changes)
            finder.updateUsingObjectConnection();
            model = finder.selectUnique()
        } as SpecificTxAction)
        then:
        model != null
        model.name == 'changed'

    }

    def "Unknown function call"() {

        when: "calling unknown function"
        finder.function()
        then: "internal orient exception"
        thrown(OCommandExecutionException)
    }

    def "Check function calls"() {

        template.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
            db.command(new OCommandSQL("CREATE FUNCTION function1 \"select from Model\" LANGUAGE SQL ")).execute();
        } as SpecificTxAction)

        when: "calling function"
        List<Model> res = finder.function()
        then:
        res.size() == 1

        when: "bad defined function finder"
        finder.functionWrongDefinition()
        then: "error"
        thrown(IllegalStateException)
    }
}
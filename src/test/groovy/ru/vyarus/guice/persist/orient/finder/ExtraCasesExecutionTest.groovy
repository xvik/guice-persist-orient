package ru.vyarus.guice.persist.orient.finder

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.finder.ExtraCasesFinder
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.AutoScanFinderTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 05.08.2014
 */
@UseModules(AutoScanFinderTestModule)
class ExtraCasesExecutionTest extends AbstractTest {

    @Inject
    ExtraCasesFinder finder

    def "Check cases"() {

        template.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "object select for iterable"
        def res = finder.selectAll();
        then: "returned iterable"
        res.iterator().next() != null

        when: "object select for iterator"
        res = finder.selectAllIterator();
        then: "returned iterator"
        res.next() != null

        when: "graph select for iterator"
        res = finder.selectAllVertex();
        then: "returned iterator"
        res.next() != null

        when: "graph select for iterable"
        res = finder.selectAllVertexIterable();
        then: "returned iterable"
        res.iterator().next() != null

        when: "object select with set conversion"
        res = finder.selectAllAsSet();
        then: "returned set"
        res instanceof Set
        res.size() == 1

        when: "graph select with set conversion"
        res = finder.selectAllAsSetGraph();
        then: "returned set"
        res instanceof Set
        res.size() == 1

        when: "vararg check"
        res = finder.findWithVararg('Sam', 'Dan', 'John');
        then: "returned list"
        res.size() == 1

        when: "document connection overridden in select"
        res = finder.documentOverride();
        then: "returned list"
        res.size() == 1

        when: "jdk7 optional"
        res = finder.findJdkOptional();
        then: "returned jdk optional"
        res instanceof Optional
        res.get()

        when: "guava optional"
        res = finder.findGuavaOptional();
        then: "returned guava optional"
        res instanceof com.google.common.base.Optional
        res.get()
    }
}
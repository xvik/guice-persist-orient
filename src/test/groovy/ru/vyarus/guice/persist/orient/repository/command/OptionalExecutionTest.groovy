package ru.vyarus.guice.persist.orient.repository.command

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.repository.command.support.OptionalCases
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 15.02.2015
 */
@UseModules(RepositoryTestModule)
class OptionalExecutionTest extends AbstractTest {

    @Inject
    OptionalCases dao

    def "Check optional support"() {

        context.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "guava optional"
        def res = dao.findGuavaOptional();
        then: "returned guava optional"
        res instanceof com.google.common.base.Optional
        res.get()

        when: "converting empty collection to single element"
        res = dao.emptyCollection();
        then: "empty optional returned"
        res instanceof com.google.common.base.Optional
        !res.isPresent()
    }
}
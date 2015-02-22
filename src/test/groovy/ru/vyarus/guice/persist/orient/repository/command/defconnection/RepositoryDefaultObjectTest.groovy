package ru.vyarus.guice.persist.orient.repository.command.defconnection

import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.model.Model
import spock.guice.UseModules

/**
 * Tests for recognition ambiguous cases, when object db must be used instead of document.
 * @author Vyacheslav Rusakov 
 * @since 02.08.2014
 */
@UseModules(RepositoryDefaultObjectModule)
class RepositoryDefaultObjectTest extends AbstractTest {

    @Inject
    DefObjectDao repository

    def "Check default connection"() {
        context.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
        } as SpecificTxAction)

        when: "select wil chose default connection, it should be object"
        List<Model> res = repository.selectAllNoType();
        then:
        res.size() == 1

    }
}
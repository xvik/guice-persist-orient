package ru.vyarus.guice.persist.orient.repository.command.function

import com.google.inject.Inject
import com.orientechnologies.orient.core.sql.OCommandSQL
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.repository.RepositoryException
import ru.vyarus.guice.persist.orient.repository.core.MethodExecutionException
import ru.vyarus.guice.persist.orient.support.Config
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules
import spock.lang.IgnoreIf

/**
 * @author Vyacheslav Rusakov 
 * @since 14.02.2015
 */
@UseModules(RepositoryTestModule)
class FunctionRecognitionTest extends AbstractTest {

    @Inject
    Functions dao

    def "Unknown function call"() {

        when: "calling unknown function"
        dao.function()
        then: "internal orient exception"
        thrown(RepositoryException)
    }

    // see FunctionRemoteTest
    @IgnoreIf({ Config.DB.startsWith('memory') })
    def "Check function calls"() {

        context.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
            db.command(new OCommandSQL("CREATE FUNCTION function1 \"select from Model\" LANGUAGE SQL ")).execute();
        } as SpecificTxAction)

        when: "calling function"
        List<Model> res = dao.function()
        then:
        res.size() == 1
    }
}
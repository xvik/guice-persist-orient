package ru.vyarus.guice.persist.orient.repository.command.function

import com.orientechnologies.orient.core.sql.OCommandSQL
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.repository.core.MethodExecutionException
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.util.remoteext.UseRemote

/**
 * @author Vyacheslav Rusakov
 * @since 18.10.2017
 */
@UseRemote
class FunctionRemoteTest extends FunctionRecognitionTest {

    // remove this test and enable root class test if bug would be fixed
    def "Check function calls"() {
        context.doInTransaction({ db ->
            db.save(new Model(name: 'John', nick: 'Doe'))
            db.command(new OCommandSQL("CREATE FUNCTION function1 \"select from Model\" LANGUAGE SQL ")).execute();
        } as SpecificTxAction)

        when: "calling function"
        List<Model> res = dao.function()
        // todo this is orient bug in legacy api
        then:
        def ex = thrown(MethodExecutionException)
        ex.printStackTrace()
    }
}

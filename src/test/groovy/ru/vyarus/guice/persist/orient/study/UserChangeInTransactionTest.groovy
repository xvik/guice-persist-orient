package ru.vyarus.guice.persist.orient.study

import com.orientechnologies.orient.core.db.object.ODatabaseObject
import com.orientechnologies.orient.core.exception.OSecurityAccessException
import com.orientechnologies.orient.core.metadata.security.OUser
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.PersistentContext
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.PackageSchemeModule
import spock.guice.UseModules

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov 
 * @since 23.02.2015
 */
@UseModules(PackageSchemeModule)
class UserChangeInTransactionTest extends AbstractTest {

    @Inject
    PersistentContext<ODatabaseObject> context

    def "Change user inside transaction"() {

        context.doInTransaction({ db ->
            db.getMetadata().getSecurity().createUser('test', 'test', 'reader')
            db.save(new Model(name: 'name', nick: 'nick'))
        } as SpecificTxAction)

        when: "trying to override user inside transaction"
        OUser admin;
        context.doInTransaction({ db ->
            admin = db.getMetadata().getSecurity().getUser('admin')
            OUser test = db.getMetadata().getSecurity().getUser('test')
            db.setUser(test)

            Model model = db.browseClass(Model).iterator().next()
            model.name = 'changed'
            db.save(model)
        } as SpecificTxAction)
        then: "reader can't modify records"
        thrown(OSecurityAccessException)


        when: "starting new transaction, user will be still incorrect"
        context.doInTransaction({ db ->
            // user is still incorrect (becuase pool connection was returned back to pool with wring user
            assert db.getUser().name == 'test'
            //changing user back
            db.setUser(admin)

            Model model = db.browseClass(Model).iterator().next()
            model.name = 'changed'
            db.save(model)
        } as SpecificTxAction)
        then: 'everything ok'
        true

        when: "admin saves data but before commit tx user changed"
        context.doInTransaction({ db ->
            assert db.getUser().name == 'admin'

            Model model = db.browseClass(Model).iterator().next()
            model.name = 'changed'
            db.save(model)

            OUser test = db.getMetadata().getSecurity().getUser('test')
            db.setUser(test)
        } as SpecificTxAction)
        then: 'everything ok'
        true
    }
}
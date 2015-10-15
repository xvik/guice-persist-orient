package ru.vyarus.guice.persist.orient.db.user

import com.orientechnologies.orient.core.exception.OSecurityAccessException
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.PersistException
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.PackageSchemeModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 03.03.2015
 */
@UseModules(PackageSchemeModule)
class TxUserOverrideTest extends AbstractTest {

    def "Check tx user change"() {

        context.doInTransaction({ db ->
            db.getMetadata().getSecurity().createUser('test', 'test', 'reader')
            db.save(new Model(name: 'name', nick: 'nick'))
        } as SpecificTxAction)

        when: "trying to override user inside transaction"
        context.doInTransaction({ db ->
            context.doWithUser('test', {
                Model model = db.browseClass(Model).iterator().next()
                model.name = 'changed'
                db.save(model)
            })
        } as SpecificTxAction)
        then: "reader can't modify records"
        thrown(OSecurityAccessException)


        when: "starting new transaction, user will be default"
        context.doInTransaction({ db ->
            assert db.getUser().name == 'admin'

            Model model = db.browseClass(Model).iterator().next()
            model.name = 'changed'
            db.save(model)
        } as SpecificTxAction)
        then: 'everything ok'
        true

        when: "changing user and verify user reset after callback"
        context.doInTransaction({ db ->
            assert db.getUser().name == 'admin'
            context.doWithUser('test', {
                assert db.getUser().name == 'test'
            })
            assert db.getUser().name == 'admin'
        } as SpecificTxAction)
        then: "all ok"
        true

        when: "changing user recursively"
        context.doInTransaction({ db ->
            context.doWithUser('test', {
                context.doWithUser('admin', {

                })
            })
        } as SpecificTxAction)
        then: "not allowed"
        thrown(IllegalStateException)

        when: "changing same user recursively"
        context.doInTransaction({ db ->
            context.doWithUser('test', {
                context.doWithUser('test', {

                })
            })
        } as SpecificTxAction)
        then: "allowed"
        true

        when: "not existing user provided"
        context.doInTransaction({ db ->
            context.doWithUser('blabla', {})
        } as SpecificTxAction)
        then: "error"
        thrown(IllegalStateException)

        when: "checked exception thrown"
        context.doInTransaction({ db ->
            context.doWithUser('test', {
                throw new IOException()
            })
        } as SpecificTxAction)
        then: "exception wrapped"
        thrown(UserActionException)

        when: "runtime checked exception thrown"
        context.doInTransaction({ db ->
            context.doWithUser('test', {
                throw new PersistException("test")
            })
        } as SpecificTxAction)
        then: "exception rethrown"
        thrown(PersistException)
    }

}
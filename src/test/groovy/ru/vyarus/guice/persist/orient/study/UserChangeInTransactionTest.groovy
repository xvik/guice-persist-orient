package ru.vyarus.guice.persist.orient.study

import com.google.common.collect.Lists
import com.orientechnologies.orient.core.exception.OSecurityAccessException
import com.orientechnologies.orient.core.metadata.security.OUser
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.PersistentContext
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.db.user.SpecificUserAction
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
    PersistentContext<OObjectDatabaseTx> context

    @Override
    void setupSecurity() {
        defaultSecurity()
    }

    def "Change user inside transaction"() {

        context.doInTransaction({ db ->
            db.getMetadata().getSecurity().createUser('test', 'test', 'reader')
            db.save(new Model(name: 'name', nick: 'nick'))
        } as SpecificTxAction)

        when: "trying to override user inside transaction"
        context.doInTransaction({ db ->
            OUser test = db.getMetadata().getSecurity().getUser('test')
            db.setUser(test)

            Model model = db.browseClass(Model).iterator().next()
            model.name = 'changed'
            db.save(model)
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
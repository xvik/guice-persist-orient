package ru.vyarus.guice.persist.orient.db.user

import com.google.common.collect.Lists
import com.orientechnologies.orient.core.exception.OSecurityAccessException
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.PersistException
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.PackageSchemeModule
import spock.guice.UseModules

import javax.inject.Inject

/**
 * Checks usage of specific users during runtime (different from configured during startup).
 *
 * @author Vyacheslav Rusakov 
 * @since 04.11.2014
 */
@UseModules(PackageSchemeModule)
class UserOverrideTest extends AbstractTest {

    @Inject
    UserManager userManager

    def "Test connecting with different user"() {

        context.doInTransaction({ db ->
            db.getMetadata().getSecurity().createUser('test', 'test', 'reader')
            db.save(new Model(name: 'name', nick: 'nick'))
        } as SpecificTxAction)

        when: "working with db within different user"
        List<Model> res =
                userManager.executeWithUser('test', 'test', {
                    context.doInTransaction({ db ->
                        Lists.newArrayList(db.browseClass(Model) as Iterable)
                    } as SpecificTxAction)
                } as SpecificUserAction)
        then: "all ok"
        res.size() == 1

        when: "trying to modify record with read-only user"
        userManager.executeWithUser('test', 'test', {
            context.doInTransaction({ db ->
                Model model = db.browseClass(Model)[0]
                model.name = 'changed'
                db.save(model)
            } as SpecificTxAction)
        } as SpecificUserAction)
        then: "edit not allowed"
        thrown(OSecurityAccessException)

        when: "trying to override user inside transaction"
        context.doInTransaction({ db ->
            userManager.executeWithUser('test', 'test', null)
        } as SpecificTxAction)
        then: "override not allowed"
        thrown(IllegalStateException)

        when: "trying to override overridden user"
        userManager.executeWithUser('test', 'test', {
            userManager.executeWithUser('test', 'test', null)
        } as SpecificUserAction)
        then: "override not allowed"
        thrown(IllegalStateException)

        when: "checked exception thrown"
        userManager.executeWithUser('test', 'test', {
            throw new IOException()
        } as SpecificUserAction)
        then: "exception wrapped"
        thrown(UserActionException)

        when: "checked runtime exception thrown"
        userManager.executeWithUser('test', 'test', {
            throw new PersistException("test")
        } as SpecificUserAction)
        then: "exception rethrown"
        thrown(PersistException)
    }
}
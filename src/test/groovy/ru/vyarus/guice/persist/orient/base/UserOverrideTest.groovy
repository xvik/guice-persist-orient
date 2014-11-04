package ru.vyarus.guice.persist.orient.base

import com.google.common.collect.Lists
import com.orientechnologies.orient.core.exception.OSecurityAccessException
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.db.user.SpecificUserAction
import ru.vyarus.guice.persist.orient.db.user.UserManager
import ru.vyarus.guice.persist.orient.support.Config
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

    //using different db for test because of aggressive cleanup
    def static normalUrl;
    static {
        normalUrl = Config.DB
        Config.DB = "memory:specificUserTest"
    }

    void cleanupSpec() {
        Config.DB = normalUrl
    }

    def "Test connecting with different user"() {

        template.doInTransaction({ db ->
            db.getMetadata().getSecurity().createUser('test', 'test', 'reader')
            db.save(new Model(name: 'name', nick: 'nick'))
        } as SpecificTxAction)

        when: "working with db within different user"
        List<Model> res = Lists.newArrayList(
                userManager.<Model> executeWithUser('test', 'test', {
                    template.doInTransaction({ db ->
                        db.browseClass(Model)
                    } as SpecificTxAction)
                } as SpecificUserAction) as Iterable)
        then: "all ok"
        res.size() == 1

        when: "trying to modify record with read-only user"
        userManager.executeWithUser('test', 'test', {
            template.doInTransaction({ db ->
                Model model = db.browseClass(Model)[0]
                model.name = 'changed'
                db.save(model)
            } as SpecificTxAction)
        } as SpecificUserAction)
        then: "edit not allowed"
        thrown(OSecurityAccessException)

        when: "trying to override user inside transaction"
        template.doInTransaction({ db ->
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
    }
}
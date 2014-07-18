package ru.vyarus.guice.persist.orient.base

import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.base.model.Model
import ru.vyarus.guice.persist.orient.base.model.ModelAuto
import ru.vyarus.guice.persist.orient.base.modules.SimpleModule
import ru.vyarus.guice.persist.orient.template.TransactionalAction
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 18.07.2014
 */
@UseModules(SimpleModule.class)
class DefaultModelTest extends AbstractTest {

    def "Check default startup"() {
        given: "schema initialized"
        final Collection<Class<?>> schemaEntries =
                template.doWithTransaction({ db ->
                    return db.entityManager.registeredEntities
                } as TransactionalAction<Collection<Class<?>>>)

        expect: "all model classes in package where loaded"
        schemaEntries.contains(Model);
        schemaEntries.contains(ModelAuto);
    }
}

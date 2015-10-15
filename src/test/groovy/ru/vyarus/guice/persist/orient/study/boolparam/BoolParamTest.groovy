package ru.vyarus.guice.persist.orient.study.boolparam

import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ObjectSchemeInitializer
import ru.vyarus.guice.persist.orient.db.transaction.template.TxAction
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import ru.vyarus.guice.persist.orient.util.transactional.TransactionalTest
import spock.guice.UseModules

import javax.inject.Inject

/**
 * https://github.com/xvik/guice-persist-orient/issues/6
 *
 * @author Vyacheslav Rusakov 
 * @since 27.05.2015
 */
@UseModules(RepositoryTestModule)
class BoolParamTest extends AbstractTest {

    @Inject
    UserRepository repository
    @Inject
    ObjectSchemeInitializer initializer;

    @Override
    void setup() {
        context.doWithoutTransaction({
            def db = context.getConnection()
            initializer.register(User)
            db.getMetadata().getSchema().synchronizeSchema()
        } as TxAction)
    }

    @Override
    void cleanup() {
        context.doWithoutTransaction({
            def db = context.getConnection()
            db.getEntityManager().deregisterEntityClass(User)
            db.getMetadata().getSchema().synchronizeSchema()
        } as TxAction)
    }

    @TransactionalTest
    def "Check boolean params"() {

        setup:
        repository.save(new User(username: 'test1', active: false))
        repository.save(new User(username: 'test2', active: true))

        when: 'query correct user by boolean parameter'
        User user = repository.findActiveByUsername('test2', true)
        then:'user found'
        user
        user.username == 'test2'

        when: 'query correct but inactive user'
        user = repository.findActiveByUsername('test1', true)
        then: 'user should not be found'
        !user

        when: 'query for inactive user'
        user = repository.findActiveByUsername('test1', false)
        then: 'user found'
        user
        user.username == 'test1'
    }
}
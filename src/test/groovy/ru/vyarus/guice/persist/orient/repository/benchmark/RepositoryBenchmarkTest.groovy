package ru.vyarus.guice.persist.orient.repository.benchmark

import com.google.common.base.Stopwatch
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

import javax.inject.Inject
import java.lang.reflect.Method

/**
 * Very very simple benchmark. Ofc not accurate, just to understand overhead.
 *
 * @author Vyacheslav Rusakov 
 * @since 28.10.2014
 */
@UseModules(RepositoryTestModule)
class RepositoryBenchmarkTest extends AbstractTest {

    @Inject
    BenchmarkDelegate delegateBean
    @Inject
    RepositoryBenchmark dao;

    def "Benchmark repositories"() {

        context.doInTransaction({ db ->
            10.times {
                db.save(new Model(name: "name$it", nick: "nick$it"))
            }
        } as SpecificTxAction)
        // worm up
        final Method method = BenchmarkDelegate.getMethod('findAll');
        100.times {
            delegateBean.findAll()
            method.invoke(delegateBean)
            dao.findAll()
            dao.findAllDelegate()
        }

        when:
        3.times {
            println "------------------------------- try $it"
            Stopwatch stopwatch = Stopwatch.createStarted()
            delegateBean.findAll()
            stopwatch.stop()
            println "Direct call: $stopwatch"

            stopwatch.reset().start()
            method.invoke(delegateBean)
            stopwatch.stop()
            println "Direct reflection call: $stopwatch"

            stopwatch.reset().start()
            dao.findAll()
            stopwatch.stop()
            println "Query repository method call: $stopwatch"

            stopwatch.reset().start()
            dao.findAllDelegate()
            stopwatch.stop()
            println "Delegate repository method call: $stopwatch"
        }
        then:
        true
    }
}
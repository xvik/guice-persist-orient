package ru.vyarus.guice.persist.orient.finder.benchmark

import com.google.common.base.Stopwatch
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.transaction.template.SpecificTxAction
import ru.vyarus.guice.persist.orient.support.finder.benchmark.BenchmarkDelegate
import ru.vyarus.guice.persist.orient.support.finder.benchmark.FinderBenchmark
import ru.vyarus.guice.persist.orient.support.model.Model
import ru.vyarus.guice.persist.orient.support.modules.FinderTestModule
import spock.guice.UseModules

import javax.inject.Inject
import java.lang.reflect.Method

/**
 * Very very simple benchmark. Ofc not accurate, just to understand overhead.
 *
 * @author Vyacheslav Rusakov 
 * @since 28.10.2014
 */
@UseModules(FinderTestModule)
class FinderBenchmarkTest extends AbstractTest {

    @Inject
    BenchmarkDelegate delegateBean
    @Inject
    FinderBenchmark finder;

    def "Benchmark finders"() {

        template.doInTransaction({ db ->
            10.times {
                db.save(new Model(name: "name$it", nick: "nick$it"))
            }
        } as SpecificTxAction)
        // worm up
        final Method method = BenchmarkDelegate.getMethod('findAll');
        100.times {
            delegateBean.findAll()
            method.invoke(delegateBean)
            finder.findAll()
            finder.findAllDelegate()
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
            finder.findAll()
            stopwatch.stop()
            println "Sql finder call: $stopwatch"

            stopwatch.reset().start()
            finder.findAllDelegate()
            stopwatch.stop()
            println "Delegate finder call: $stopwatch"
        }
        then:
        true
    }
}
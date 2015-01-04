package ru.vyarus.guice.persist.orient.base

import ru.vyarus.guice.persist.orient.finder.internal.AbstractFinderDefinitionTest
import ru.vyarus.guice.persist.orient.finder.internal.FinderDescriptor
import ru.vyarus.guice.persist.orient.finder.internal.FinderDescriptorFactory
import ru.vyarus.guice.persist.orient.support.finder.InterfaceFinder
import ru.vyarus.guice.persist.orient.support.modules.TestFinderModule
import ru.vyarus.java.generics.resolver.context.GenericsInfoFactory
import spock.guice.UseModules
import spock.lang.Shared

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * @author Vyacheslav Rusakov 
 * @since 04.01.2015
 */
@UseModules(TestFinderModule)
class FindersCacheTest extends AbstractFinderDefinitionTest {

    @Shared
    ExecutorService executor

    void setupSpec() {
        executor = Executors.newFixedThreadPool(20)
    }

    void cleanupSpec() {
        executor.shutdown()
        // enable cache after test
        System.clearProperty(FinderDescriptorFactory.CACHE_PROPERTY)
        System.clearProperty(GenericsInfoFactory.CACHE_PROPERTY)
    }

    def "Check cache"() {

        when:
        FinderDescriptor desc = lookup(InterfaceFinder.getMethod("selectAll"))
        then:
        desc == lookup(InterfaceFinder.getMethod("selectAll"))
    }

    def "Check concurrency"() {

        when: "Call finder in 20 threads"
        List<Future<?>> executed = []
        int times = 20
        times.times({
            executed << executor.submit({
                lookup(InterfaceFinder.getMethod("selectAll"))
            })
        })
        // lock until finish
        executed.each({ it.get() })
        then: "Nothing fails"
        true
    }

    def "Check cache methods"() {

        when: "clear current cache state"
        def field = FinderDescriptorFactory.getDeclaredField("cache")
        field.setAccessible(true)
        Map cache = field.get(factory)

        def genericsField = GenericsInfoFactory.getDeclaredField("CACHE")
        genericsField.setAccessible(true)
        Map genericsCache = genericsField.get(null)

        then:
        !cache.isEmpty()
        !genericsCache.isEmpty()
        FinderDescriptorFactory.isCacheEnabled()
        GenericsInfoFactory.isCacheEnabled()
        factory.clearCache()
        FinderDescriptorFactory.isCacheEnabled()
        cache.isEmpty()
        genericsCache.isEmpty()

        when: "disabling cache"
        FinderDescriptorFactory.disableCache()
        then: "both finders and generics caches disabled"
        cache.isEmpty()
        genericsCache.isEmpty()
        !FinderDescriptorFactory.isCacheEnabled()
        !GenericsInfoFactory.isCacheEnabled()

        when: "creating descriptor with cache disabled"
        lookup(InterfaceFinder.getMethod("selectAll"))
        then:
        cache.isEmpty()
        genericsCache.isEmpty()
    }
}
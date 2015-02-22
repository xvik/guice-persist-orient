package ru.vyarus.guice.persist.orient.repository

import ru.vyarus.guice.persist.orient.repository.command.support.DbRecognitionCases
import ru.vyarus.guice.persist.orient.repository.core.AbstractRepositoryDefinitionTest
import ru.vyarus.guice.persist.orient.repository.core.MethodDescriptorFactory
import ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
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
@UseModules(RepositoryTestModule)
class RepositoryMethodDescriptorCacheTest extends AbstractRepositoryDefinitionTest {

    @Shared
    ExecutorService executor

    void setupSpec() {
        executor = Executors.newFixedThreadPool(20)
    }

    void cleanupSpec() {
        executor.shutdown()
        // enable cache after test
        System.clearProperty(MethodDescriptorFactory.CACHE_PROPERTY)
        System.clearProperty(GenericsInfoFactory.CACHE_PROPERTY)
    }

    def "Check cache"() {

        when:
        RepositoryMethodDescriptor desc = lookup(DbRecognitionCases.getMethod("selectAll"))
        then:
        desc == lookup(DbRecognitionCases.getMethod("selectAll"))
    }

    def "Check concurrency"() {

        when: "Call method in 20 threads"
        List<Future<?>> executed = []
        int times = 20
        times.times({
            executed << executor.submit({
                lookup(DbRecognitionCases.getMethod("selectAll"))
            })
        })
        // lock until finish
        executed.each({ it.get() })
        then: "Nothing fails"
        true
    }

    def "Check cache methods"() {

        when: "clear current cache state"
        def field = MethodDescriptorFactory.getDeclaredField("cache")
        field.setAccessible(true)
        Map cache = field.get(factory)

        def genericsField = GenericsInfoFactory.getDeclaredField("CACHE")
        genericsField.setAccessible(true)
        Map genericsCache = genericsField.get(null)

        then:
        !cache.isEmpty()
        !genericsCache.isEmpty()
        MethodDescriptorFactory.isCacheEnabled()
        GenericsInfoFactory.isCacheEnabled()
        factory.clearCache()
        MethodDescriptorFactory.isCacheEnabled()
        cache.isEmpty()
        genericsCache.isEmpty()

        when: "disabling cache"
        MethodDescriptorFactory.disableCache()
        then: "both descriptors and generics caches disabled"
        cache.isEmpty()
        genericsCache.isEmpty()
        !MethodDescriptorFactory.isCacheEnabled()
        !GenericsInfoFactory.isCacheEnabled()

        when: "creating descriptor with cache disabled"
        lookup(DbRecognitionCases.getMethod("selectAll"))
        then:
        cache.isEmpty()
        genericsCache.isEmpty()
    }
}
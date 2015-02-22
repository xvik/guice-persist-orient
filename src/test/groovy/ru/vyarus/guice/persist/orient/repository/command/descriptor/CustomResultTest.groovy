package ru.vyarus.guice.persist.orient.repository.command.descriptor

import com.orientechnologies.orient.core.record.impl.ODocument
import ru.vyarus.guice.persist.orient.repository.core.AbstractRepositoryDefinitionTest
import ru.vyarus.guice.persist.orient.repository.core.result.ResultType
import ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor
import ru.vyarus.guice.persist.orient.repository.command.support.CustomResultCases
import ru.vyarus.guice.persist.orient.support.modules.RepositoryTestModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 14.02.2015
 */
@UseModules(RepositoryTestModule)
class CustomResultTest extends AbstractRepositoryDefinitionTest {

    def "Check wrapped results"() {

        when: "count query"
        RepositoryMethodDescriptor desc = lookup(CustomResultCases.getMethod("getCount"))
        then: "plain document recognized"
        desc.result.entityType == ODocument
        desc.result.returnType == ResultType.PLAIN
        desc.result.expectType == ODocument

        when: "projection query"
        desc = lookup(CustomResultCases.getMethod("getNames"))
        then: "document collection recognized"
        desc.result.entityType == ODocument
        desc.result.returnType == ResultType.COLLECTION
        desc.result.expectType == List

        when: "projection query and array result"
        desc = lookup(CustomResultCases.getMethod("getNamesArray"))
        then: "document array recognized"
        desc.result.entityType == ODocument
        desc.result.returnType == ResultType.ARRAY
        desc.result.expectType == ODocument[]
    }
}
package ru.vyarus.guice.persist.orient.repository.core.result

import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.google.common.collect.Sets
import com.google.inject.Inject
import ru.vyarus.guice.persist.orient.AbstractTest
import ru.vyarus.guice.persist.orient.db.DbType
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.DefaultResultConverter
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.ResultConversionException
import ru.vyarus.guice.persist.orient.support.modules.DefaultModule
import spock.guice.UseModules

/**
 * @author Vyacheslav Rusakov 
 * @since 05.08.2014
 */
@UseModules(DefaultModule)
class ResultConverterTest extends AbstractTest {

    @Inject
    DefaultResultConverter converter

    def "Check list result conversion cases"() {

        when: "result is ArrayList and List expected"
        Object res = converter.convert(new ResultDescriptor(
                returnType: ResultType.COLLECTION,
                expectType: List,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                Lists.newArrayList(1, 2, 3))
        then: "no conversion"
        res instanceof ArrayList

        when: "result is ArrayList and Collection expected"
        res = converter.convert(new ResultDescriptor(
                returnType: ResultType.COLLECTION,
                expectType: Collection,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                Lists.newArrayList(1, 2, 3))
        then: "no conversion"
        res instanceof ArrayList

        when: "result is ArrayList and Iterable expected"
        res = converter.convert(new ResultDescriptor(
                returnType: ResultType.COLLECTION,
                expectType: Iterable,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                Lists.newArrayList(1, 2, 3))
        then: "no conversion"
        res instanceof ArrayList

        when: "result is ArrayList and Iterator expected"
        res = converter.convert(new ResultDescriptor(
                returnType: ResultType.COLLECTION,
                expectType: Iterator,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                Lists.newArrayList(1, 2, 3))
        then: "list iterator returned"
        res instanceof Iterator

        when: "result is ArrayList and Set expected"
        res = converter.convert(new ResultDescriptor(
                returnType: ResultType.COLLECTION,
                expectType: Set,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                Lists.newArrayList(1, 2, 3))
        then: "converted to hash set"
        res instanceof HashSet
    }

    def "Check set result conversion cases"() {

        when: "result is HashSet and List expected"
        Object res = converter.convert(new ResultDescriptor(
                returnType: ResultType.COLLECTION,
                expectType: List,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                Sets.newHashSet(1, 2, 3))
        then: "converted to ArrayList"
        res instanceof ArrayList

        when: "result is HashSet and Collection expected"
        res = converter.convert(new ResultDescriptor(
                returnType: ResultType.COLLECTION,
                expectType: Collection,
                entityType: Object,
                entityDbType: DbType.UNKNOWN),
                Sets.newHashSet(1, 2, 3))
        then: "no conversion"
        res instanceof HashSet

        when: "result is HashSet and Collection expected with projection check"
        res = converter.convert(new ResultDescriptor(
                returnType: ResultType.COLLECTION,
                expectType: Collection,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                Sets.newHashSet(1, 2, 3))
        then: "no conversion"
        res instanceof List

        when: "result is HashSet and Iterable expected"
        res = converter.convert(new ResultDescriptor(
                returnType: ResultType.COLLECTION,
                expectType: Iterable,
                entityType: Object,
                entityDbType: DbType.UNKNOWN),
                Sets.newHashSet(1, 2, 3))
        then: "no conversion"
        res instanceof HashSet

        when: "result is HashSet and Iterator expected"
        res = converter.convert(new ResultDescriptor(
                returnType: ResultType.COLLECTION,
                expectType: Iterator,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                Sets.newHashSet(1, 2, 3))
        then: "set iterator returned"
        res instanceof Iterator

        when: "result is HashSet and Set expected"
        res = converter.convert(new ResultDescriptor(
                returnType: ResultType.COLLECTION,
                expectType: Set,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                Sets.newHashSet(1, 2, 3))
        then: "no conversion"
        res instanceof Set
    }

    def "Check iterator result conversion cases"() {

        when: "result is Iterator and List expected"
        Object res = converter.convert(new ResultDescriptor(
                returnType: ResultType.COLLECTION,
                expectType: List,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                Sets.newHashSet(1, 2, 3).iterator())
        then: "converted to ArrayList"
        res instanceof ArrayList

        when: "result is Iterator and Collection expected"
        res = converter.convert(new ResultDescriptor(
                returnType: ResultType.COLLECTION,
                expectType: Collection,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                Sets.newHashSet(1, 2, 3).iterator())
        then: "converted to ArrayList"
        res instanceof ArrayList

        when: "result is Iterator and Iterable expected"
        res = converter.convert(new ResultDescriptor(
                returnType: ResultType.COLLECTION,
                expectType: Iterable,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                Sets.newHashSet(1, 2, 3).iterator())
        then: "converted to ArrayList"
        res instanceof ArrayList

        when: "result is Iterator and Iterator expected"
        res = converter.convert(new ResultDescriptor(
                returnType: ResultType.COLLECTION,
                expectType: Iterator,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                Sets.newHashSet(1, 2, 3).iterator())
        then: "no conversion"
        res instanceof Iterator

        when: "result is Iterator and Set expected"
        res = converter.convert(new ResultDescriptor(
                returnType: ResultType.COLLECTION,
                expectType: Set,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                Sets.newHashSet(1, 2, 3).iterator())
        then: "converted to HashSet"
        res instanceof HashSet
    }

    def "Check array conversion"() {

        when: "result is ArrayList and Array expected"
        Object res = converter.convert(new ResultDescriptor(
                returnType: ResultType.ARRAY,
                expectType: Integer[],
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                Lists.newArrayList(1, 2, 3))
        then: "converted to array"
        res instanceof Integer[]

        when: "result is HashSet and Array expected"
        res = converter.convert(new ResultDescriptor(
                returnType: ResultType.ARRAY,
                expectType: Integer[],
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                Sets.newHashSet(1, 2, 3))
        then: "converted to array"
        res instanceof Integer[]

        when: "result is Iterator and Array expected"
        res = converter.convert(new ResultDescriptor(
                returnType: ResultType.ARRAY,
                expectType: Integer[],
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                Sets.newHashSet(1, 2, 3).iterator())
        then: "converted to array"
        res instanceof Integer[]

        when: "result is array and Array expected"
        res = converter.convert(new ResultDescriptor(
                returnType: ResultType.ARRAY,
                expectType: Integer[],
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                [1, 2, 3])
        then: "no conversion"
        res instanceof Integer[]
    }

    def "Check plain cases"() {

        when: "result is ArrayList and plain expected"
        Object res = converter.convert(new ResultDescriptor(
                returnType: ResultType.PLAIN,
                expectType: Integer,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                Lists.newArrayList(1, 2, 3))
        then: "first element taken"
        res instanceof Integer
        res == 1

        when: "result is HashSet and plain expected"
        res = converter.convert(new ResultDescriptor(
                returnType: ResultType.PLAIN,
                expectType: Integer,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                Sets.newHashSet(1, 2, 3))
        then: "first element taken"
        res instanceof Integer
        res == 1

        when: "result is Iterator and plain expected"
        res = converter.convert(new ResultDescriptor(
                returnType: ResultType.PLAIN,
                expectType: Integer,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                Sets.newHashSet(1, 2, 3).iterator())
        then: "first element taken"
        res instanceof Integer
        res == 1

        when: "result is plain and plain expected"
        res = converter.convert(new ResultDescriptor(
                returnType: ResultType.PLAIN,
                expectType: Integer,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                1)
        then: "no conversion"
        res instanceof Integer
        res == 1
    }

    def "Check optionals"() {

        when: "result is guava optional"
        def res = converter.convert(new ResultDescriptor(
                returnType: ResultType.PLAIN,
                expectType: com.google.common.base.Optional,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                1)
        then: "no conversion"
        res instanceof com.google.common.base.Optional
        res.get() == 1
    }

    def "Check collection substitution"() {

        when: "result is ArrayList and LinkedList expected"
        Object res = converter.convert(new ResultDescriptor(
                returnType: ResultType.COLLECTION,
                expectType: LinkedList,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                Lists.newArrayList(1, 2, 3))
        then: "converted to LinkedList"
        res instanceof LinkedList
        res.size() == 3

        when: "result is HashSet and LinkedList expected"
        res = converter.convert(new ResultDescriptor(
                returnType: ResultType.COLLECTION,
                expectType: LinkedList,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                Sets.newHashSet(1, 2, 3))
        then: "converted to LinkedList"
        res instanceof LinkedList
        res.size() == 3

        when: "result is Iterator and LinkedList expected"
        res = converter.convert(new ResultDescriptor(
                returnType: ResultType.COLLECTION,
                expectType: LinkedList,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                Sets.newHashSet(1, 2, 3))
        then: "converted to LinkedList"
        res instanceof LinkedList
        res.size() == 3
    }

    def "Check primitives"() {

        when: "result is Integer and int expected"
        Object res = converter.convert(new ResultDescriptor(
                returnType: ResultType.PLAIN,
                expectType: Integer,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                1)
        then: "no conversion"
        res instanceof Integer
        res == 1

        when: "result is Integer and void expected"
        res = converter.convert(new ResultDescriptor(
                returnType: ResultType.VOID,
                expectType: void,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                1)
        then: "null returned, even if actual result passed"
        res == null

        when: "result is int and Long expected"
        res = converter.convert(new ResultDescriptor(
                returnType: ResultType.PLAIN,
                expectType: Long,
                entityType: Long,
                entityDbType: DbType.UNKNOWN),
                1 as int)
        then: "int converted to long"
        res == 1

        when: "result is long and Integer expected"
        res = converter.convert(new ResultDescriptor(
                returnType: ResultType.PLAIN,
                expectType: Integer,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                1 as long)
        then: "int converted to integer"
        res == 1
    }

    def "Check conversion fail"() {

        when: "result is string and integer expected"
        converter.convert(new ResultDescriptor(
                returnType: ResultType.PLAIN,
                expectType: Integer,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                'string')
        then: "fail"
        thrown(ResultConversionException)

        when: "result is list and map expected"
        converter.convert(new ResultDescriptor(
                returnType: ResultType.COLLECTION,
                expectType: Integer,
                entityType: Integer,
                entityDbType: DbType.UNKNOWN),
                Maps.newHashMap())
        then: "fail"
        thrown(ResultConversionException)
    }
}
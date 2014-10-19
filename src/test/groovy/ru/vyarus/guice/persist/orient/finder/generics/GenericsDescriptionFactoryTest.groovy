package ru.vyarus.guice.persist.orient.finder.generics

import com.orientechnologies.orient.core.record.impl.ODocument
import ru.vyarus.guice.persist.orient.finder.internal.generics.FinderGenericsFactory
import ru.vyarus.guice.persist.orient.finder.internal.generics.GenericsDescriptor
import ru.vyarus.guice.persist.orient.support.finder.generics.*
import ru.vyarus.guice.persist.orient.support.model.Model
import spock.lang.Specification

import java.lang.reflect.ParameterizedType

/**
 * @author Vyacheslav Rusakov 
 * @since 16.10.2014
 */
class GenericsDescriptionFactoryTest extends Specification {

    def "Check generics resolution"() {

        when: "analyzing finders hierarchy"
        FinderGenericsFactory factory = new FinderGenericsFactory()
        GenericsDescriptor desc = factory.create(Root)
        then: "correct generic values resolved"
        desc.types.size() == 7
        desc.types[Base1] == ['T': Model]
        desc.types[Base2] == ['K': Model, 'P': ODocument]
        desc.types[Lvl2Base1] == ['I': Model]
        desc.types[Lvl2Base2] == ['J': Model]
        desc.types[Lvl2Base3] == ['R': Model]
        desc.types[ComplexGenerics]['T'] == Model
        desc.types[ComplexGenerics]['K'] instanceof ParameterizedType
        ((ParameterizedType)desc.types[ComplexGenerics]['K']).getRawType() == List
        desc.types[ComplexGenerics2]['T'] == Model[]

        when: "analyzing bean finders hierarchy"
        desc = factory.create(BeanRoot)
        then: "correct generic values resolved"
        desc.types.size() == 3
        desc.types[BeanBase] == ['T': Model]
        desc.types[Lvl2BeanBase] == ['I': Model]
        desc.types[Lvl2Base1] == ['I': Model]
    }
}

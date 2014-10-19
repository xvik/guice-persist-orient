package ru.vyarus.guice.persist.orient.support.finder.generics

import com.orientechnologies.orient.core.record.impl.ODocument
import ru.vyarus.guice.persist.orient.support.model.Model

/**
 * complex interface hierarchy to check generic types resolution
 *
 * @author Vyacheslav Rusakov 
 * @since 16.10.2014
 */
public interface Root extends Base1<Model>, Base2<Model, ODocument>,
        ComplexGenerics<Model, List<Model>>, ComplexGenerics2<Model[]> {

}
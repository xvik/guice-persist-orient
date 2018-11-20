package ru.vyarus.guice.persist.orient.transaction.support

import com.google.common.collect.Lists
import com.google.inject.Provider
import com.google.inject.persist.Transactional
import com.orientechnologies.orient.core.db.object.ODatabaseObject
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import ru.vyarus.guice.persist.orient.support.model.VertexModel

import javax.inject.Inject

/**
 * @author Vyacheslav Rusakov 
 * @since 28.07.2014
 */
@javax.inject.Singleton
class ComplexModificationService {

    // @formatter:off
    @Inject
    Provider<ODatabaseObject> objectProvider
    @Inject
    Provider<OrientGraph> txGraphProvider
    // @formatter:on

    @Transactional
    List<Vertex> selectWithGraph() {
        OrientGraph db = txGraphProvider.get();
        Lists.newArrayList(db.getVerticesOfClass(VertexModel.class.simpleName))
    }
}

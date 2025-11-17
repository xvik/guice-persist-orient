package ru.vyarus.guice.persist.orient.study.objectvertex

import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.vertex.VertexType

import javax.persistence.Id
import javax.persistence.Version

/**
 * @author Vyacheslav Rusakov 
 * @since 13.06.2015
 */
@VertexType
class ObjectVertex {
    @Id
    String id;
    @Version
    Long version;
    String foo
}

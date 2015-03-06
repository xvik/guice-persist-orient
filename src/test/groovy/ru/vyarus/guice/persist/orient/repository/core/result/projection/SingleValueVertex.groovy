package ru.vyarus.guice.persist.orient.repository.core.result.projection

import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.vertex.VertexType

/**
 * Checks projection wil not occur on single element vertex.
 *
 * @author Vyacheslav Rusakov 
 * @since 26.11.2014
 */
@VertexType
class SingleValueVertex {
    String name;
}

package ru.vyarus.guice.persist.orient.support.model

import ru.vyarus.guice.persist.orient.db.scheme.annotation.VertexType

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

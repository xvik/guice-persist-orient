package ru.vyarus.guice.persist.orient.base.model

import ru.vyarus.guice.persist.orient.model.VersionedEntity

/**
 * @author Vyacheslav Rusakov 
 * @since 18.07.2014
 */
class Model extends VersionedEntity {
    String name
    String nick
}

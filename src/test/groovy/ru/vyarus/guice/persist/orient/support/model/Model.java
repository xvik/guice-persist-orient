package ru.vyarus.guice.persist.orient.support.model

import ru.vyarus.guice.persist.orient.model.VersionedEntity

/**
 * Model bean used for schema init from package
 * @author Vyacheslav Rusakov 
 * @since 18.07.2014
 */
class Model extends VersionedEntity {
    String name
    String nick
    int cnt
}

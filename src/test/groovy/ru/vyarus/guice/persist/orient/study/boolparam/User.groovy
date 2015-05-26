package ru.vyarus.guice.persist.orient.study.boolparam

import ru.vyarus.guice.persist.orient.model.VersionedEntity

/**
 * @author Vyacheslav Rusakov
 * @since 27.05.2015
 */
class User extends VersionedEntity {

    String username
    boolean active
}

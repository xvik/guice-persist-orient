package ru.vyarus.guice.persist.orient.db.scheme.customtype.support.model

import ru.vyarus.guice.persist.orient.db.scheme.annotation.Persistent
import ru.vyarus.guice.persist.orient.model.VersionedEntity

/**
 * @author Vyacheslav Rusakov
 * @since 30.03.2017
 */
@Persistent
class User extends VersionedEntity {
    String name
    // custom type
    SecurityRole role
}

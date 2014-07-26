package ru.vyarus.guice.persist.orient.base.model

import ru.vyarus.guice.persist.orient.model.VersionedEntity
import ru.vyarus.guice.persist.orient.db.scheme.autoscan.Persistent

/**
 * @author Vyacheslav Rusakov 
 * @since 18.07.2014
 */
@Persistent
class ModelAuto extends VersionedEntity {
    String nick
    int age
}

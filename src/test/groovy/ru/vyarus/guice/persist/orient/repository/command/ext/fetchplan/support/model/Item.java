package ru.vyarus.guice.persist.orient.repository.command.ext.fetchplan.support.model

import ru.vyarus.guice.persist.orient.db.scheme.annotation.Persistent

import javax.persistence.Version

/**
 * @author Vyacheslav Rusakov 
 * @since 24.02.2015
 */
@Persistent
class Item {

    String name
    Person person
    @Version
    Long version
}

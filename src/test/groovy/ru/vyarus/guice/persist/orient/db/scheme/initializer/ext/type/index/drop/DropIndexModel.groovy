package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index.drop

/**
 * @author Vyacheslav Rusakov 
 * @since 09.03.2015
 */
@DropIndexes(["test1", "notExisting"])
class DropIndexModel {

    String foo
}

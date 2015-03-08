package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.readonly

/**
 * @author Vyacheslav Rusakov 
 * @since 09.03.2015
 */
class ReadonlyModel {

    @Readonly
    String immutable

    @Readonly(false)
    String mutable
}

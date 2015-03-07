package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.rename

/**
 * @author Vyacheslav Rusakov 
 * @since 07.03.2015
 */
class RenamePropertyModel {

    @RenamePropertyFrom("foo")
    String bar;
}

package ru.vyarus.guice.persist.orient.db.scheme.initializer.core.ext.support

/**
 * @author Vyacheslav Rusakov 
 * @since 06.03.2015
 */
class ExtendedModel2 extends ExtendedModel{

    String boo
    @FieldExt
    int other
}

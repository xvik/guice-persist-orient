package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.mandatory

/**
 * @author Vyacheslav Rusakov 
 * @since 09.03.2015
 */
class MandatoryModel {

    @Mandatory
    String required

    @Mandatory(false)
    String any
}

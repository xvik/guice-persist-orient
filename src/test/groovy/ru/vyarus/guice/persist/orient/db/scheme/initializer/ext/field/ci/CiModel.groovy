package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.ci

/**
 * @author Vyacheslav Rusakov 
 * @since 09.06.2015
 */
class CiModel {

    @CaseInsensitive
    String ci

    @CaseInsensitive(false)
    String nonci
}

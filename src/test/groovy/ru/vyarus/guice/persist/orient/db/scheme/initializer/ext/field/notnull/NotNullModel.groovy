package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.notnull
/**
 * @author Vyacheslav Rusakov 
 * @since 09.03.2015
 */
class NotNullModel {

    @ONotNull
    String notnull

    @ONotNull(false)
    String nullable
}

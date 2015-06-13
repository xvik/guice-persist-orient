package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index

import com.orientechnologies.orient.core.metadata.schema.OClass

/**
 * @author Vyacheslav Rusakov 
 * @since 09.03.2015
 */
class IndexModel {

    @Index(OClass.INDEX_TYPE.NOTUNIQUE)
    String foo

    @Index(value = OClass.INDEX_TYPE.FULLTEXT, name = "customName")
    String bar

    @Index(value = OClass.INDEX_TYPE.NOTUNIQUE, name = "nulls", ignoreNullValues = false)
    String nulls
}

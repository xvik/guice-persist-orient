package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index

import com.orientechnologies.orient.core.metadata.schema.OClass

/**
 * @author Vyacheslav Rusakov 
 * @since 09.03.2015
 */
class MultipleIndexesModel {

    @Index.List([
            @Index(value = OClass.INDEX_TYPE.NOTUNIQUE, name = "test1"),
            @Index(value = OClass.INDEX_TYPE.FULLTEXT, name = "test2")
    ])
    String foo
}

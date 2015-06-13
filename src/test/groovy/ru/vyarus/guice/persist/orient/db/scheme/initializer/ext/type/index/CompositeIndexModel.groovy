package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index

import com.orientechnologies.orient.core.metadata.schema.OClass

/**
 * @author Vyacheslav Rusakov 
 * @since 09.03.2015
 */
@CompositeIndex(name = "test",
        type = OClass.INDEX_TYPE.NOTUNIQUE,
        fields = ["foo", "bar"],
        ignoreNullValues = false)
class CompositeIndexModel {

    String foo
    String bar
}

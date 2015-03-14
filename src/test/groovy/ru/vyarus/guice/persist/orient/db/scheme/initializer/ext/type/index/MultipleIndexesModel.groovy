package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index

import com.orientechnologies.orient.core.metadata.schema.OClass

/**
 * @author Vyacheslav Rusakov 
 * @since 09.03.2015
 */
@CompositeIndex.List([
        @CompositeIndex(name = "test", type = OClass.INDEX_TYPE.NOTUNIQUE, fields = ["foo", "bar"]),
        @CompositeIndex(name = "test2", type = OClass.INDEX_TYPE.DICTIONARY, fields = ["foo", "bar"])
])
class MultipleIndexesModel {
    String foo
    String bar
}

package ru.vyarus.guice.persist.orient.study.index.fieldsorder

import com.orientechnologies.orient.core.metadata.schema.OClass
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index.CompositeIndex
import ru.vyarus.guice.persist.orient.model.VersionedEntity

/**
 * This is the same as FOTest but null values are not ignored.
 * This leads to interesting behaviour: now orient use composite index for searches by only one field
 * (this is not actually important, but very interesting fact :) )
 *
 * @author Vyacheslav Rusakov
 * @since 10.01.2018
 */
@CompositeIndex(name = "test2",
        fields = ["foo", "bar"],
        type = OClass.INDEX_TYPE.NOTUNIQUE,
        ignoreNullValues = false)
class FOTest2 extends VersionedEntity {
    String foo
    String bar
}

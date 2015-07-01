package ru.vyarus.guice.persist.orient.study.index.fieldsorder

import com.orientechnologies.orient.core.metadata.schema.OClass
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index.CompositeIndex
import ru.vyarus.guice.persist.orient.model.VersionedEntity

/**
 * @author Vyacheslav Rusakov 
 * @since 01.07.2015
 */
@CompositeIndex(name = "test",
        fields = ["foo", "bar"],
        type = OClass.INDEX_TYPE.NOTUNIQUE)
class FOTest extends VersionedEntity{
    String foo
    String bar
}

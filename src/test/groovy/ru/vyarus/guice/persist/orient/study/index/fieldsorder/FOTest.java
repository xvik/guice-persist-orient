package ru.vyarus.guice.persist.orient.study.index.fieldsorder;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.index.CompositeIndex;
import ru.vyarus.guice.persist.orient.model.VersionedEntity;

/**
 * @author Vyacheslav Rusakov
 * @since 01.07.2015
 */
@CompositeIndex(name = "test",
        fields = {"foo", "bar"},
        type = OClass.INDEX_TYPE.NOTUNIQUE)
public class FOTest extends VersionedEntity {
    private String foo;
    private String bar;

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public String getBar() {
        return bar;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }
}

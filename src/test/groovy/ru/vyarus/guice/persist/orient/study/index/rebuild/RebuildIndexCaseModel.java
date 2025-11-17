package ru.vyarus.guice.persist.orient.study.index.rebuild;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index.Index;

/**
 * @author Vyacheslav Rusakov
 * @since 10.01.2018
 */
public class RebuildIndexCaseModel {

    @Index(OClass.INDEX_TYPE.NOTUNIQUE)
    private String foo;

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }
}

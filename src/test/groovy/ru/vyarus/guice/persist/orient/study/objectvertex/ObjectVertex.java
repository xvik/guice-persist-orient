package ru.vyarus.guice.persist.orient.study.objectvertex;

import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.vertex.VertexType;

import javax.persistence.Id;
import javax.persistence.Version;

/**
 * @author Vyacheslav Rusakov
 * @since 13.06.2015
 */
@VertexType
public class ObjectVertex {

    @Id
    private String id;
    @Version
    private Long version;
    private String foo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }
}

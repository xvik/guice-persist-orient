package ru.vyarus.guice.persist.orient.support.model;

import ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.vertex.VertexType;

import javax.persistence.Id;
import javax.persistence.Version;

/**
 * Model should be registered in graph compatible way (extend V), meaning vertexes of this type could be created through graph api.
 * Generally model has no difference with not annotated model and could be used for all connections.
 * <p>
 * Drawback: because object entity mapper will reproduce hierarchy, VersionedEntity can't be used as base class
 * (if both entities for edge and vertexes extends it, base entity can extend only one type (V or E))
 *
 * @author Vyacheslav Rusakov
 * @since 03.08.2014
 */
@VertexType
public class VertexModel {

    private String name;
    private String nick;
    @Id
    private String id;
    @Version
    private Long version;

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}

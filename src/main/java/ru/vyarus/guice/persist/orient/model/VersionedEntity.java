package ru.vyarus.guice.persist.orient.model;

import javax.persistence.Id;
import javax.persistence.Version;

/**
 * All object entities must declare version property for optimistic transactions support.
 * Entities may subclass this class or simply declare property the same way.
 * <p>
 * If you use entity just for scheme mapping, then version property is not required. It's required only to retrieve
 * entity with object connection within OPTIMISTIC transaction.
 * <p>
 * Don't use for entities annotated with
 * {@link ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.edge.EdgeType} or
 * {@link ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.vertex.VertexType}, because each class can
 * have only one superclass in scheme, so if bae class will extend V, edge entities can't extend from it and
 * the opposite. Moreover, there is a big chance that some simple class will be registered before annotated one and
 * logic will not be able to modify it.
 *
 * @author Vyacheslav Rusakov
 * @since 19.07.2014
 */
public abstract class VersionedEntity {
    @Id
    private String id;
    @Version
    private Long version;

    /**
     * @return orient entity id or null if entity not stored (id's in orient are physical location in cluster
     * and not just a number like in relational database, but anyway id is unique and may be used the same way
     * as in relational databases)
     */
    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    /**
     * @return object version or null if entity not stored (used by orient for optimistic locking)
     */
    public Long getVersion() {
        return version;
    }

    public void setVersion(final Long version) {
        this.version = version;
    }
}

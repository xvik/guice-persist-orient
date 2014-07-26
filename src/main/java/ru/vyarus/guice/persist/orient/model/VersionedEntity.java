package ru.vyarus.guice.persist.orient.model;

import javax.persistence.Id;
import javax.persistence.Version;

/**
 * All entities must declare version property for optimistic transactions support.
 * Entities may subclass this class or simply declare property the same way.
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
     * @return orient entity id or null if entity not stored (id's in orient are physical location in cluster and not just a number like in
     * relational database, but anyway id is unique and may be used the same way as in relational databases)
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return object version or null if entity not stored (used by orient for optimistic locking)
     */
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}

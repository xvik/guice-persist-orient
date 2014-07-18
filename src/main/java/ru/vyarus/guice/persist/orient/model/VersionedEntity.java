package ru.vyarus.guice.persist.orient.model;

import javax.persistence.Version;

/**
 * All entities must declare version property for optimistic transactions support.
 * Entities may subclass this class or simply declare property the same way.
 *
 * @author Vyacheslav Rusakov
 * @since 19.07.2014
 */
public abstract class VersionedEntity {
    @Version
    private Long version;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}

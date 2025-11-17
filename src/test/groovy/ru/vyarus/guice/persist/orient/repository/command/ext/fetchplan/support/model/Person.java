package ru.vyarus.guice.persist.orient.repository.command.ext.fetchplan.support.model;

import ru.vyarus.guice.persist.orient.db.scheme.annotation.Persistent;

import javax.persistence.Version;

/**
 * @author Vyacheslav Rusakov
 * @since 24.02.2015
 */
@Persistent
public class Person {

    private String name;
    @Version
    private Long version;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}

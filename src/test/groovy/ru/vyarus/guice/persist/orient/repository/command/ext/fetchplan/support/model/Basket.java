package ru.vyarus.guice.persist.orient.repository.command.ext.fetchplan.support.model;

import ru.vyarus.guice.persist.orient.db.scheme.annotation.Persistent;

import javax.persistence.Version;
import java.util.Set;

/**
 * @author Vyacheslav Rusakov
 * @since 24.02.2015
 */
@Persistent
public class Basket {

    private String name;
    private Set<Item> items;
    @Version
    private Long version;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}

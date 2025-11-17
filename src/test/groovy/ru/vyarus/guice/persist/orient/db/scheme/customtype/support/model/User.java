package ru.vyarus.guice.persist.orient.db.scheme.customtype.support.model;

import ru.vyarus.guice.persist.orient.db.scheme.annotation.Persistent;
import ru.vyarus.guice.persist.orient.model.VersionedEntity;

/**
 * @author Vyacheslav Rusakov
 * @since 30.03.2017
 */
@Persistent
public class User extends VersionedEntity {
    private String name;
    // custom type
    private SecurityRole role;
    // custom non-enum type
    private CustomClass type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SecurityRole getRole() {
        return role;
    }

    public void setRole(SecurityRole role) {
        this.role = role;
    }

    public CustomClass getType() {
        return type;
    }

    public void setType(CustomClass type) {
        this.type = type;
    }
}

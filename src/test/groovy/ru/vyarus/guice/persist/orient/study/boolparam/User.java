package ru.vyarus.guice.persist.orient.study.boolparam;

import ru.vyarus.guice.persist.orient.model.VersionedEntity;

/**
 * @author Vyacheslav Rusakov
 * @since 27.05.2015
 */
public class User extends VersionedEntity {
    private String username;
    private boolean active;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean getActive() {
        return active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

package ru.vyarus.guice.persist.orient.db.scheme.customtype.support.model;

/**
 * @author Vyacheslav Rusakov
 * @since 30.03.2017
 */
public enum SecurityRole {
    ADMIN("administrador"), LOGIN("login");

    private SecurityRole(String id) {
        this.id = id;
    }

    public static SecurityRole getByName(String name) {
        if (ADMIN.name().equals(name)) {
            return ADMIN;
        } else if (LOGIN.name().equals(name)) {
            return LOGIN;
        }

        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
}

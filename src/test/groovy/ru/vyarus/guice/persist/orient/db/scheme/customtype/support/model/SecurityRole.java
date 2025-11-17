package ru.vyarus.guice.persist.orient.db.scheme.customtype.support.model

/**
 * @author Vyacheslav Rusakov
 * @since 30.03.2017
 */
enum SecurityRole {
    ADMIN('administrador'),
    LOGIN('login')

    String    id

    private SecurityRole(String id) {
        this.id = id;
    }

    static SecurityRole getByName(String name) {
        if (ADMIN.name().equals(name)) {
            return ADMIN;
        } else if (LOGIN.name().equals(name)) {
            return LOGIN;
        }
        return null;
    }
}

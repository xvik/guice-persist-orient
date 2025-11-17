package ru.vyarus.guice.persist.orient.support.model;

import ru.vyarus.guice.persist.orient.model.VersionedEntity;

/**
 * Model bean used for schema init from package
 *
 * @author Vyacheslav Rusakov
 * @since 18.07.2014
 */
public class Model extends VersionedEntity {

    private String name;
    private String nick;
    private int cnt;

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

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }
}

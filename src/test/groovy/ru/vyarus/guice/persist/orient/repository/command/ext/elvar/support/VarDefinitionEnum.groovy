package ru.vyarus.guice.persist.orient.repository.command.ext.elvar.support;

/**
 * Placeholder definition with enum.
 *
 * @author Vyacheslav Rusakov
 * @since 22.09.2014
 */
public enum VarDefinitionEnum {
    NAME("name"),
    NICK("nick")

    private String field;

    VarDefinitionEnum(String field) {
        this.field = field
    }

    @Override
    String toString() {
        return field;
    }
}

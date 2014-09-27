package ru.vyarus.guice.persist.orient.support.finder;

/**
 * Placeholder definition with enum.
 *
 * @author Vyacheslav Rusakov
 * @since 22.09.2014
 */
public enum PlaceholdersEnum {
    NAME("name"),
    NICK("nick")

    private String field;

    PlaceholdersEnum(String field) {
        this.field = field
    }

    @Override
    String toString() {
        return field;
    }
}

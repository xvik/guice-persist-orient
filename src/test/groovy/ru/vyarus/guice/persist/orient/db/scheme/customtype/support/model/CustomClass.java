package ru.vyarus.guice.persist.orient.db.scheme.customtype.support.model;

/**
 * Custom non-enum manually serialized class
 *
 * @author Vyacheslav Rusakov
 * @since 16.11.2025
 */
public class CustomClass {
    private String value;

    public CustomClass() {
    }

    public CustomClass(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

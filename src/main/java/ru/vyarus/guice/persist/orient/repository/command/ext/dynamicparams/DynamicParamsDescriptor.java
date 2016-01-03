package ru.vyarus.guice.persist.orient.repository.command.ext.dynamicparams;

/**
 * Dynamic parameters descriptor object.
 *
 * @author Vyacheslav Rusakov
 * @since 27.02.2015
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class DynamicParamsDescriptor {

    /**
     * Type of dynamic parameters (named or positional).
     */
    public boolean named;

    /**
     * Parameter position.
     */
    public int position;

    public DynamicParamsDescriptor(final boolean named, final int position) {
        this.named = named;
        this.position = position;
    }
}

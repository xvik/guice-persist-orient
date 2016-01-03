package ru.vyarus.guice.persist.orient.repository.command.script;

import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor;

/**
 * Script method specific descriptor.
 *
 * @author Vyacheslav Rusakov
 * @since 25.02.2015
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class ScriptCommandMethodDescriptor extends CommandMethodDescriptor {

    /**
     * Script language.
     */
    public String language;
}

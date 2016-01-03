package ru.vyarus.guice.persist.orient.repository.command.core.spi;

import ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.core.el.ElDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.core.param.ParamsDescriptor;

/**
 * Command method extensions descriptor.
 *
 * @author Vyacheslav Rusakov
 * @since 03.02.2015
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class CommandMethodDescriptor extends RepositoryMethodDescriptor<CommandExtension> {

    /**
     * Query string, set by extensions. It may be query, function or any other extension specific string.
     * String could contain variables (${var}). By default generic names supported. Other variables could be
     * set by extension.
     */
    public String command;

    /**
     * Query parameters descriptor.
     */
    public ParamsDescriptor params;

    /**
     * Query el variables descriptor.
     */
    public ElDescriptor el;
}

package ru.vyarus.guice.persist.orient.finder.command;

import com.google.inject.ImplementedBy;
import com.orientechnologies.orient.core.command.OCommandRequest;

/**
 * @author Vyacheslav Rusakov
 * @since 02.08.2014
 */
@ImplementedBy(DefaultCommandBuilder.class)
public interface CommandBuilder {

    OCommandRequest buildCommand(SqlCommandDesc desc);
}

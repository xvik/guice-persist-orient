package ru.vyarus.guice.persist.orient.repository.command.ext.var;

import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParam;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks parameter as command variable. In contrast to el variables, which are applied to command string
 * before passing command to orient
 * (e.g {@link ru.vyarus.guice.persist.orient.repository.command.ext.elvar.ElVar}), these vars are
 * used directly in command execution.
 * <p>
 * Defined variable could be referenced in query as $name.
 *
 * @author Vyacheslav Rusakov
 * @see <a href="https://orientdb.org/docs/3.1.x/sql/SQL-Query.html#let-block">docs</a>
 * @since 25.02.2015
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
@MethodParam(VarParamExtension.class)
@SuppressWarnings("checkstyle:IllegalIdentifierName")
public @interface Var {

    /**
     * @return variable name
     */
    String value();
}

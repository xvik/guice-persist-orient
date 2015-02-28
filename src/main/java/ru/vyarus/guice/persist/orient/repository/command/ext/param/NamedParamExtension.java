package ru.vyarus.guice.persist.orient.repository.command.ext.param;

import com.google.common.base.Strings;
import ru.vyarus.guice.persist.orient.repository.command.core.param.CommandParamsContext;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParamExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamInfo;

import javax.inject.Singleton;
import java.util.List;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;

/**
 * {@link Param} parameter annotation extension.
 *
 * @author Vyacheslav Rusakov
 * @since 06.02.2015
 */
@Singleton
public class NamedParamExtension implements
        MethodParamExtension<CommandMethodDescriptor, CommandParamsContext, Param> {

    @Override
    public void processParameters(final CommandMethodDescriptor descriptor,
                                  final CommandParamsContext context, final List<ParamInfo<Param>> paramsInfo) {
        for (ParamInfo<Param> paramInfo : paramsInfo) {
            final String name = Strings.emptyToNull(paramInfo.annotation.value());
            check(name != null, "Named parameter requires not empty name");
            context.addNamedParam(name, paramInfo);
        }
    }
}

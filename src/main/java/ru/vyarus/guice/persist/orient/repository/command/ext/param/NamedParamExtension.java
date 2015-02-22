package ru.vyarus.guice.persist.orient.repository.command.ext.param;

import com.google.common.base.Strings;
import ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParamExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamInfo;
import ru.vyarus.guice.persist.orient.repository.command.core.param.QueryParamsContext;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor;

import javax.inject.Singleton;
import java.util.List;

/**
 * {@link Param} parameter annotation extension.
 *
 * @author Vyacheslav Rusakov
 * @since 06.02.2015
 */
@Singleton
public class NamedParamExtension implements
        MethodParamExtension<CommandMethodDescriptor, QueryParamsContext, Param> {

    @Override
    public void processParameters(final CommandMethodDescriptor descriptor,
                                  final QueryParamsContext context, final List<ParamInfo<Param>> paramsInfo) {
        for (ParamInfo<Param> paramInfo : paramsInfo) {
            final String name = Strings.emptyToNull(paramInfo.annotation.value());
            MethodDefinitionException.check(name != null,
                    "Named parameter requires not empty name on position %s", paramInfo.position);
            context.addNamedParam(name, paramInfo);
        }
    }
}

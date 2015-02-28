package ru.vyarus.guice.persist.orient.repository.command.ext.var;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.command.OCommandRequest;
import ru.vyarus.guice.persist.orient.repository.command.core.param.CommandParamsContext;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandExtension;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParamExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamInfo;

import javax.inject.Singleton;
import java.util.List;
import java.util.Map;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;

/**
 * {@link Var} param extension.
 *
 * @author Vyacheslav Rusakov
 * @since 25.02.2015
 */
@Singleton
public class VarParamExtension implements
        CommandExtension<CommandMethodDescriptor>,
        MethodParamExtension<CommandMethodDescriptor, CommandParamsContext, Var> {

    public static final String KEY = VarParamExtension.class.getName();

    @Override
    public void processParameters(final CommandMethodDescriptor descriptor, final CommandParamsContext context,
                                  final List<ParamInfo<Var>> paramsInfo) {
        final Map<String, Integer> varsMapping = Maps.newHashMap();
        for (ParamInfo<Var> param : paramsInfo) {
            final String name = Strings.emptyToNull(param.annotation.value());
            check(name != null, "Variable name required");
            check(!varsMapping.containsKey(name), "Duplicate variable with name '%s'", name);
            varsMapping.put(name, param.position);
        }
        descriptor.extDescriptors.put(KEY, varsMapping);
    }

    @Override
    public void amendCommandDescriptor(final SqlCommandDescriptor sql, final CommandMethodDescriptor descriptor,
                                       final Object instance, final Object... arguments) {
        // not needed
    }

    @Override
    public void amendCommand(final OCommandRequest query, final CommandMethodDescriptor descriptor,
                             final Object instance, final Object... arguments) {
        @SuppressWarnings("unchecked")
        final Map<String, Integer> vars = (Map<String, Integer>) descriptor.extDescriptors.get(KEY);
        final OCommandContext context = query.getContext();
        for (Map.Entry<String, Integer> entry : vars.entrySet()) {
            context.setVariable(entry.getKey(), arguments[entry.getValue()]);
        }
    }
}

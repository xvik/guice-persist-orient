package ru.vyarus.guice.persist.orient.repository.command.ext.placeholder;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.orientechnologies.orient.core.command.OCommandRequest;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParamExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamInfo;
import ru.vyarus.guice.persist.orient.repository.command.core.param.QueryParamsContext;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandExtension;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor;

import javax.inject.Singleton;
import java.util.List;
import java.util.Map;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;

/**
 * Placeholder parameters extension (query el variables defined through method parameters).
 * Placeholders defined with {@link Placeholder} annotation.
 * <p>Enum and String parameters supported as placeholders.</p>
 * <p>String placeholders should have possible values declarations for security (with
 * {@link PlaceholderValues}), otherwise warning showed.</p>
 *
 * @author Vyacheslav Rusakov
 * @since 03.02.2015
 */
@Singleton
public class PlaceholderParamExtension implements
        CommandExtension<CommandMethodDescriptor>,
        MethodParamExtension<CommandMethodDescriptor, QueryParamsContext, Placeholder> {

    public static final String KEY = PlaceholderParamExtension.class.getName();

    @Override
    public void processParameters(final CommandMethodDescriptor descriptor,
                                  final QueryParamsContext context,
                                  final List<ParamInfo<Placeholder>> paramInfos) {
        final PlaceholderDescriptor placeholders = PlaceholderAnalyzer.analyzeDeclarations(
                context.getDescriptorContext().method, descriptor.el);
        for (ParamInfo<Placeholder> paramInfo : paramInfos) {
            check(descriptor.el != null, "Placeholder parameter used while query did "
                    + "not contain placeholders");
            final String placeholderName = paramInfo.annotation.value();
            PlaceholderAnalyzer.bindPlaceholder(placeholderName, paramInfo.position, placeholders,
                    context.getDescriptorContext().method, placeholders.values.get(placeholderName));
            context.addDynamicElVarValue(placeholderName);
        }
        descriptor.extDescriptors.put(KEY, placeholders);
    }

    @Override
    public void amendCommandDescriptor(final SqlCommandDescriptor sql, final CommandMethodDescriptor descriptor,
                                       final Object instance, final Object... arguments) {
        final PlaceholderDescriptor placeholders = (PlaceholderDescriptor) descriptor.extDescriptors.get(KEY);
        if (placeholders.parametersIndex != null) {
            sql.elVars.putAll(
                    getPlaceholderParams(placeholders.parametersIndex, placeholders.values, arguments)
            );
        }

    }

    @Override
    public void amendCommand(final OCommandRequest query, final CommandMethodDescriptor descriptor,
                             final Object instance, final Object... arguments) {
        // not needed
    }

    private static Map<String, String> getPlaceholderParams(final Map<String, Integer> positions,
                                                            final Multimap<String, String> defaults,
                                                            final Object... arguments) {
        final Map<String, String> res = Maps.newHashMap();
        for (Map.Entry<String, Integer> entry : positions.entrySet()) {
            final String name = entry.getKey();
            final Object value = arguments[entry.getValue()];
            // safeguard from accident null passing
            Preconditions.checkArgument(value != null, "Placeholder '%s' value is null. "
                    + "Use explicit empty string if you need empty replacement for placeholder.", name);
            final String strValue = value.toString();
            // check value with defaults. don't print warning because one warning is enough (during method analysis)
            if (!value.getClass().isEnum() && defaults.containsKey(name)) {
                Preconditions.checkArgument(defaults.get(name).contains(strValue),
                        "Illegal value for placeholder '%s': '%s'", name, strValue);
            }
            res.put(name, strValue);
        }
        return res;
    }
}

package ru.vyarus.guice.persist.orient.repository.command.ext.elvar;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Primitives;
import com.orientechnologies.orient.core.command.OCommandRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.repository.command.core.param.CommandParamsContext;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandExtension;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParamExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamInfo;
import ru.vyarus.guice.persist.orient.repository.core.util.RepositoryUtils;

import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;
import static ru.vyarus.guice.persist.orient.repository.core.MethodExecutionException.checkExec;

/**
 * {@link ElVar} parameter extension.
 *
 * @author Vyacheslav Rusakov
 * @since 03.02.2015
 */
@Singleton
public class ElVarParamExtension implements
        CommandExtension<CommandMethodDescriptor>,
        MethodParamExtension<CommandMethodDescriptor, CommandParamsContext, ElVar> {

    public static final String KEY = ElVarParamExtension.class.getName();
    private static final Logger LOGGER = LoggerFactory.getLogger(ElVarParamExtension.class);
    private static final Class<?>[] SAFE_TYPES = {Enum.class, Number.class, Character.class, Class.class};

    @Override
    public void processParameters(final CommandMethodDescriptor descriptor,
                                  final CommandParamsContext context,
                                  final List<ParamInfo<ElVar>> paramsInfo) {
        check(descriptor.el != null, "El var parameter used while command did not contain vars");
        final ElVarDescriptor elvars = new ElVarDescriptor();
        for (ParamInfo<ElVar> param : paramsInfo) {
            final String name = param.annotation.value();
            // duplicate check
            context.addDynamicElVarValue(name);
            bind(elvars, param, context.getDescriptorContext().method);
        }
        descriptor.extDescriptors.put(KEY, elvars);
    }

    @Override
    public void amendCommandDescriptor(final SqlCommandDescriptor sql, final CommandMethodDescriptor descriptor,
                                       final Object instance, final Object... arguments) {
        final ElVarDescriptor elvars = (ElVarDescriptor) descriptor.extDescriptors.get(KEY);
        sql.elVars.putAll(
                getVarValues(elvars.parametersIndex, elvars.values, Converters.DEFAULT, arguments)
        );
        sql.elVars.putAll(
                getVarValues(elvars.classParametersIndex, elvars.values, Converters.CLASS, arguments)
        );

    }

    @Override
    public void amendCommand(final OCommandRequest query, final CommandMethodDescriptor descriptor,
                             final Object instance, final Object... arguments) {
        // not needed
    }

    private void bind(final ElVarDescriptor elvars, final ParamInfo<ElVar> param, final Method method) {
        final String name = param.annotation.value();
        final boolean safe = param.annotation.safe() || isSafeType(param.type);
        final String[] allowed = param.annotation.allowedValues();
        if (!safe && allowed.length == 0) {
            LOGGER.warn("No default values registered for method {} variable parameter {}. Either use safe "
                            + "types (enum, number, primitives etc) or define possible values in annotation. "
                            + "If you sure that parameter is secured from injection, set safe flag to remove "
                            + "this warning.",
                    RepositoryUtils.methodToString(method), name);
        }
        if (allowed.length > 0) {
            elvars.values.putAll(name, Arrays.asList(allowed));
        }
        if (param.type.equals(Class.class)) {
            elvars.classParametersIndex.put(name, param.position);
        } else {
            elvars.parametersIndex.put(name, param.position);
        }
    }

    private boolean isSafeType(final Class<?> type) {
        boolean res = type.isPrimitive() || Primitives.isWrapperType(type);
        if (!res) {
            for (Class<?> safe : SAFE_TYPES) {
                if (safe.isAssignableFrom(type)) {
                    res = true;
                    break;
                }
            }
        }
        return res;
    }

    private Map<String, String> getVarValues(final Map<String, Integer> positions,
                                             final Multimap<String, String> defaults,
                                             final Converters.ValueConverter converter,
                                             final Object... arguments) {
        final Map<String, String> res = Maps.newHashMap();
        for (Map.Entry<String, Integer> entry : positions.entrySet()) {
            final String name = entry.getKey();
            final Object value = arguments[entry.getValue()];
            // use empty string for nulls
            @SuppressWarnings("unchecked")
            final String strValue = Strings.nullToEmpty(converter.convert(value));
            // check value with defaults
            if (defaults.containsKey(name)) {
                checkExec(defaults.get(name).contains(strValue),
                        "Illegal value for variable '%s': '%s'", name, strValue);
            }
            res.put(name, strValue);
        }
        return res;
    }
}

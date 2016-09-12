package ru.vyarus.guice.persist.orient.repository.command.ext.ridelvar;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.record.impl.ODocument;
import ru.vyarus.guice.persist.orient.db.util.RidUtils;
import ru.vyarus.guice.persist.orient.repository.command.core.param.CommandParamsContext;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandExtension;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.MethodExecutionException;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParamExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamInfo;

import javax.inject.Singleton;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;
import static ru.vyarus.guice.persist.orient.repository.core.MethodExecutionException.checkExec;

/**
 * {@link RidElVar} param extension.
 *
 * @author Vyacheslav Rusakov
 * @since 02.06.2015
 */
@Singleton
public class RidElVarParamExtension implements
        CommandExtension<CommandMethodDescriptor>,
        MethodParamExtension<CommandMethodDescriptor, CommandParamsContext, RidElVar> {

    public static final String KEY = RidElVarParamExtension.class.getName();

    @Override
    public void processParameters(final CommandMethodDescriptor descriptor, final CommandParamsContext context,
                                  final List<ParamInfo<RidElVar>> paramsInfo) {
        final Map<String, Integer> varsMapping = Maps.newHashMap();
        for (ParamInfo<RidElVar> param : paramsInfo) {
            final String name = Strings.emptyToNull(param.annotation.value());
            check(name != null, "Rid variable name required");
            // duplicate check
            context.addDynamicElVarValue(name);
            varsMapping.put(name, param.position);
        }
        descriptor.extDescriptors.put(KEY, varsMapping);
    }

    @Override
    public void amendCommandDescriptor(final SqlCommandDescriptor sql, final CommandMethodDescriptor descriptor,
                                       final Object instance, final Object... arguments) {
        @SuppressWarnings("unchecked")
        final Map<String, Integer> vars = (Map<String, Integer>) descriptor.extDescriptors.get(KEY);
        for (Map.Entry<String, Integer> entry : vars.entrySet()) {
            final String key = entry.getKey();
            final Object value = arguments[entry.getValue()];
            try {
                sql.elVars.put(key, convert(value));
            } catch (Exception e) {
                throw new MethodExecutionException(String.format("Invalid rid el variable '%s'", key), e);
            }
        }
    }

    @Override
    public void amendCommand(final OCommandRequest query, final CommandMethodDescriptor descriptor,
                             final Object instance, final Object... arguments) {
        // not used
    }

    private String convert(final Object value) {
        checkExec(value != null, "Not null value required");
        final String res;
        if (value instanceof ODocument) {
            // ODocument is iterable, so need to check it first
            res = RidUtils.getRid(value);
        } else if (value instanceof Iterable) {
            res = convertCollection(((Iterable) value).iterator());
        } else if (value instanceof Iterator) {
            res = convertCollection((Iterator) value);
        } else if (value.getClass().isArray()) {
            res = convertArray(value);
        } else {
            res = RidUtils.getRid(value);
        }
        return res;
    }

    private String convertArray(final Object array) {
        final List<Object> res = Lists.newArrayList();
        for (int i = 0; i < Array.getLength(array); i++) {
            res.add(Array.get(array, i));
        }
        return convertCollection(res.iterator());
    }

    private String convertCollection(final Iterator iterator) {
        final StringBuilder builder = new StringBuilder("[");
        while (iterator.hasNext()) {
            builder.append(RidUtils.getRid(iterator.next()));
            if (iterator.hasNext()) {
                builder.append(',');
            }
        }
        builder.append(']');
        return builder.toString();
    }
}

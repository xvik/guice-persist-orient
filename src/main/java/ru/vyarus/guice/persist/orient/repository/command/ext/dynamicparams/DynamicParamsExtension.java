package ru.vyarus.guice.persist.orient.repository.command.ext.dynamicparams;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.orientechnologies.orient.core.command.OCommandRequest;
import ru.vyarus.guice.persist.orient.repository.command.core.param.CommandParamsContext;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandExtension;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParamExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamInfo;
import ru.vyarus.guice.persist.orient.db.util.Order;

import javax.inject.Singleton;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;
import static ru.vyarus.guice.persist.orient.repository.core.MethodExecutionException.checkExec;

/**
 * {@link DynamicParams} parameter extension.
 *
 * @author Vyacheslav Rusakov
 * @since 27.02.2015
 */
@Singleton
// must be executed after other possible extensions to correctly validate compatibility
@Order(200)
public class DynamicParamsExtension implements
        CommandExtension<CommandMethodDescriptor>,
        MethodParamExtension<CommandMethodDescriptor, CommandParamsContext, DynamicParams> {

    public static final String KEY = DynamicParamsExtension.class.getName();

    @Override
    public void processParameters(final CommandMethodDescriptor descriptor, final CommandParamsContext context,
                                  final List<ParamInfo<DynamicParams>> paramsInfo) {
        check(paramsInfo.size() == 1, "Duplicate @%s definition", DynamicParams.class.getSimpleName());
        final ParamInfo<DynamicParams> param = paramsInfo.get(0);
        checkParamCorrectness(param.type);
        descriptor.extDescriptors.put(KEY, new DynamicParamsDescriptor(
                Map.class.isAssignableFrom(param.type), param.position));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void amendCommandDescriptor(final SqlCommandDescriptor sql, final CommandMethodDescriptor descriptor,
                                       final Object instance, final Object... arguments) {
        final DynamicParamsDescriptor desc = (DynamicParamsDescriptor) descriptor.extDescriptors.get(KEY);
        final Object value = arguments[desc.position];
        // null allowed - no parameters
        if (value == null) {
            return;
        }
        if (desc.named) {
            checkExec(sql.useNamedParams || sql.params.length == 0, "Can't apply named dynamic params, "
                    + "because positional params already used");
            sql.params = null;
            sql.namedParams = composeNamed((Map) value, sql.namedParams);
        } else {
            checkExec(!sql.useNamedParams || sql.namedParams.isEmpty(), "Can't apply positional dynamic params, "
                    + "because named params already used");
            sql.namedParams = null;
            sql.params = composePositional(value, sql.params);
        }
        sql.useNamedParams = desc.named;
    }

    @Override
    public void amendCommand(final OCommandRequest query, final CommandMethodDescriptor descriptor,
                             final Object instance, final Object... arguments) {
        // not required
    }

    private void checkParamCorrectness(final Class<?> type) {
        // array - position params (including vararg)
        // map - named parameters
        final boolean isRecognizedType = type.isArray() || Map.class.isAssignableFrom(type);
        if (!isRecognizedType) {
            // parameters position must be preserved, so list is the only acceptable type
            check(List.class.isAssignableFrom(type),
                    "Type %s can't be used for dynamic parameters. Use array, List or Map",
                    type.getName());
        }
    }

    private Map<String, Object> composeNamed(final Map<?, ?> value, final Map<String, Object> existing) {
        final Map<String, Object> res = existing == null ? Maps.<String, Object>newHashMap() : existing;
        for (Map.Entry entry : value.entrySet()) {
            final Object key = entry.getKey();
            check(key != null, "Incorrect named dynamic parameters: name can't be null");
            final String name = Strings.emptyToNull(key.toString().trim());
            check(name != null, "Incorrect named dynamic parameters: name can't be empty");
            check(!res.containsKey(name), "Incorrect named dynamic parameters: duplicate name '%s'", name);
            res.put(name, entry.getValue());
        }
        return res;
    }

    @SuppressWarnings({"unchecked", "PMD.UseVarargs"})
    private Object[] composePositional(final Object value, final Object[] existing) {
        final List res = Lists.newArrayList();
        if (existing != null) {
            res.addAll(Arrays.asList(existing));
        }
        if (value.getClass().isArray()) {
            for (int i = 0; i < Array.getLength(value); i++) {
                res.add(Array.get(value, i));
            }
        } else {
            res.addAll((List) value);
        }
        return res.isEmpty() ? new Object[0] : res.toArray();
    }
}

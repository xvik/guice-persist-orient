package ru.vyarus.guice.persist.orient.repository.command.ext.fetchplan;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.fetch.OFetchHelper;
import ru.vyarus.guice.persist.orient.db.util.Order;
import ru.vyarus.guice.persist.orient.repository.command.core.param.CommandParamsContext;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandExtension;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParamExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamInfo;

import javax.inject.Singleton;
import java.util.List;

/**
 * {@link FetchPlan} parameter annotation extension.
 *
 * @author Vyacheslav Rusakov
 * @since 23.02.2015
 */
@Singleton
// executed before default extensions, because it modifies query string
@Order(-11)
public class FetchPlanParamExtension implements
        MethodParamExtension<CommandMethodDescriptor, CommandParamsContext, FetchPlan>,
        CommandExtension<CommandMethodDescriptor> {

    public static final String KEY = FetchPlanParamExtension.class.getName();

    @Override
    public void processParameters(final CommandMethodDescriptor descriptor,
                                  final CommandParamsContext context, final List<ParamInfo<FetchPlan>> paramsInfo) {
        MethodDefinitionException.check(paramsInfo.size() == 1, "Duplicate fetch plan parameter");
        final ParamInfo<FetchPlan> param = paramsInfo.get(0);
        MethodDefinitionException.check(param.type.equals(String.class),
                "Fetch plan parameter must be String");
        final String defPlan = Strings.emptyToNull(param.annotation.value());
        OFetchHelper.checkFetchPlanValid(defPlan);
        descriptor.extDescriptors.put(KEY, new FetchPlanDescriptor(defPlan, param.position));
    }

    @Override
    public void amendCommandDescriptor(final SqlCommandDescriptor sql, final CommandMethodDescriptor descriptor,
                                       final Object instance, final Object... arguments) {
        final FetchPlanDescriptor plan = (FetchPlanDescriptor) descriptor.extDescriptors.get(KEY);
        final String value = Strings.emptyToNull((String) arguments[plan.position]);
        final String fetchPlan = value != null ? value : plan.defPlan;
        if (fetchPlan == null) {
            return;
        }
        final String query = sql.command;
        MethodDefinitionException.check(query.toLowerCase().startsWith("select"),
                "@FetchPlan may be used only for select queries");
        sql.command = query + " FETCHPLAN " + fetchPlan;
    }

    @Override
    public void amendCommand(final OCommandRequest query, final CommandMethodDescriptor descriptor,
                             final Object instance, final Object... arguments) {
        // not needed
    }
}

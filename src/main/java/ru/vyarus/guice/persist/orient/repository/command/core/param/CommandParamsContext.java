package ru.vyarus.guice.persist.orient.repository.command.core.param;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import ru.vyarus.guice.persist.orient.repository.command.core.el.ElDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.core.el.ElUtils;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.spi.DescriptorContext;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamInfo;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamsContext;

import java.util.List;
import java.util.Map;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;

/**
 * Query repository method parameters context.
 * Support ordinal and named parameters. All parameters not handled with extensions are treated ordinal
 * (positional). Named parameters parsed with extensions (default is
 * {@link ru.vyarus.guice.persist.orient.repository.command.ext.param.Param}, but any custom could be used).
 * <p>
 * Query string could contain el variables (${name}). By default declaring type generic names recognized.
 * Other variables could be provided by extensions (e.g.
 * {@link ru.vyarus.guice.persist.orient.repository.command.ext.elvar.ElVar}). Extension must declare
 * that placeholder recognized and owned by extension. There are two types of variables: static, which are resolved
 * during method descriptor creation and dynamic, which require parameter values to compute value.
 *
 * @author Vyacheslav Rusakov
 * @since 04.02.2015
 */
public class CommandParamsContext extends ParamsContext<CommandMethodDescriptor> {
    private final Map<String, ParamInfo> named = Maps.newHashMap();
    private final Map<String, String> staticElValues = Maps.newHashMap();
    private final List<String> dynamicElValues = Lists.newArrayList();

    public CommandParamsContext(final DescriptorContext descriptorContext) {
        super(descriptorContext);
    }

    /**
     * Register parameter as named.
     *
     * @param name      parameter name
     * @param paramInfo parameter object
     */
    public void addNamedParam(final String name, final ParamInfo paramInfo) {
        check(!named.containsKey(name),
                "Duplicate parameter %s declaration at position %s", name, paramInfo.position);
        named.put(name, paramInfo);
    }

    /**
     * Register static query el variable value.
     *
     * @param name  variable name
     * @param value variable value
     */
    public void addStaticElVarValue(final String name, final String value) {
        check(!staticElValues.containsKey(name),
                "Duplicate el variable %s value declaration: %s (original: %s)", name, value,
                staticElValues.get(name));
        check(!dynamicElValues.contains(name),
                "El variable %s can't be registered as static, because dynamic declaration already defined",
                name);
        staticElValues.put(name, value);
    }

    /**
     * Register dynamic query el variable value. Variable will be completely handled by extension.
     * Registration is required to validated not handled variables.
     *
     * @param name variable name
     */
    public void addDynamicElVarValue(final String name) {
        check(!dynamicElValues.contains(name),
                "Duplicate dynamic el variable %s declaration", name);
        check(!staticElValues.containsKey(name),
                "El variable %s can't be registered as dynamic, because static declaration already defined",
                name);
        dynamicElValues.add(name);
    }

    @Override
    public void process(final CommandMethodDescriptor descriptor) {
        processEl(descriptor);

        final ParamsDescriptor desc = new ParamsDescriptor();
        check(getOrdinals().isEmpty() || named.isEmpty(),
                "Ordinal and named parameters can't be used together");
        if (named.isEmpty()) {
            desc.parametersIndex = Lists.transform(getOrdinals(), PARAM_INDEX_FUNCTION)
                    .toArray(new Integer[getOrdinals().size()]);
        } else {
            desc.useNamedParameters = true;
            desc.namedParametersIndex = Maps.transformValues(named, PARAM_INDEX_FUNCTION);
        }
        descriptor.params = desc;
    }

    private void processEl(final CommandMethodDescriptor descriptor) {
        final ElDescriptor el = descriptor.el;
        if (el != null) {
            el.handledVars.addAll(dynamicElValues);
            el.directValues.putAll(staticElValues);
            final List<String> vars = Lists.newArrayList();
            vars.addAll(el.handledVars);
            vars.addAll(el.directValues.keySet());
            try {
                ElUtils.validate(descriptor.command, vars);
            } catch (Exception ex) {
                check(false, ex.getMessage());
            }
        } else {
            // not the same as in previous branch, because el context could contain predefined vars
            final List<String> vars = Lists.newArrayList();
            vars.addAll(dynamicElValues);
            vars.addAll(staticElValues.keySet());
            check(vars.isEmpty(), "El vars declared, while command '%s' doesn't contain variables: %s",
                    descriptor.command, Joiner.on(", ").join(vars));
        }
    }
}

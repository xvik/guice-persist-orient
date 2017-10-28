package ru.vyarus.guice.persist.orient.repository.command.core;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.orientechnologies.orient.core.command.OCommandRequest;
import ru.vyarus.guice.persist.orient.repository.command.core.el.ElAnalyzer;
import ru.vyarus.guice.persist.orient.repository.command.core.el.ElUtils;
import ru.vyarus.guice.persist.orient.repository.command.core.param.CommandParamsContext;
import ru.vyarus.guice.persist.orient.repository.command.core.param.ParamsDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandExtension;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.ext.SpiService;
import ru.vyarus.guice.persist.orient.repository.core.spi.DescriptorContext;
import ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.spi.method.RepositoryMethodExtension;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Base class for query extensions.
 * Query execution is two phase: prepare execution descriptor and then prepare orient command and execute.
 * <p>
 * Query natively supports el variables. By default, generic names could be used. If other variables required,
 * special amend extensions should be written to populate context.
 * <p>
 * Extensions must implement {@link ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandExtension}.
 *
 * @param <T> descriptor type
 * @param <A> method annotation type
 * @author Vyacheslav Rusakov
 * @since 07.02.2015
 */
public abstract class AbstractCommandExtension<T extends CommandMethodDescriptor,
        A extends Annotation> implements RepositoryMethodExtension<T, A> {

    private final SpiService spiService;

    public AbstractCommandExtension(final SpiService spiService) {
        this.spiService = spiService;
    }

    /**
     * Analyze query string for el variables and creates el descriptor.
     *
     * @param descriptor repository method descriptor
     * @param context    repository method context
     */
    protected void analyzeElVars(final T descriptor, final DescriptorContext context) {
        descriptor.el = ElAnalyzer.analyzeQuery(context.generics, descriptor.command);
    }

    /**
     * Analyze method parameters, search for extensions and prepare parameters context.
     *
     * @param descriptor repository method descriptor
     * @param context    repository method context
     */
    protected void analyzeParameters(final T descriptor, final DescriptorContext context) {
        final CommandParamsContext paramsContext = new CommandParamsContext(context);
        spiService.process(descriptor, paramsContext);
    }

    @Override
    public Object execute(final T descriptor, final Object repositoryInstance,
                          final Object... arguments) throws Throwable {
        final SqlCommandDescriptor desc;
        final OCommandRequest query;
        try {
            desc = createQueryDescriptor(descriptor, arguments);
            amendCommandDescriptor(desc, descriptor, repositoryInstance, arguments);

            query = createQueryCommand(descriptor, desc);
            amendCommand(query, descriptor, repositoryInstance, arguments);
        } catch (Exception ex) {
            throw new CommandMethodException(String.format("Failed to prepare command '%s' execution",
                    descriptor.command), ex);
        }

        return executeCommand(descriptor, desc, query);
    }

    protected SqlCommandDescriptor createQueryDescriptor(final T descriptor, final Object... arguments) {
        final SqlCommandDescriptor desc = new SqlCommandDescriptor();
        desc.command = descriptor.command;
        final ParamsDescriptor params = descriptor.params;
        desc.useNamedParams = params.useNamedParameters;
        if (desc.useNamedParams) {
            desc.namedParams = prepareNamedParams(params.namedParametersIndex, arguments);
        } else {
            desc.params = prepareOrdinalParams(params.parametersIndex, arguments);
        }
        if (descriptor.el != null) {
            desc.elVars = Maps.newHashMap();
            desc.elVars.putAll(descriptor.el.directValues);
        }
        return desc;
    }

    /**
     * Actual command object depends on query type and have to be chosen by exact extension.
     *
     * @param descriptor repository method descriptor
     * @param desc       query descriptor
     * @return query command request object
     */
    protected abstract OCommandRequest createQueryCommand(T descriptor, SqlCommandDescriptor desc);

    private Object[] prepareOrdinalParams(final Integer[] positions, final Object... arguments) {
        final Object[] res = new Object[positions.length];
        for (int i = 0; i < positions.length; i++) {
            res[i] = arguments[positions[i]];
        }
        return res;
    }

    private Map<String, Object> prepareNamedParams(final Map<String, Integer> positions, final Object... arguments) {
        final Map<String, Object> res = Maps.newHashMap();
        for (Map.Entry<String, Integer> entry : positions.entrySet()) {
            res.put(entry.getKey(), arguments[entry.getValue()]);
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    private void amendCommandDescriptor(final SqlCommandDescriptor desc, final T descriptor,
                                        final Object instance, final Object... arguments) {
        for (CommandExtension ext : descriptor.amendExtensions) {
            ext.amendCommandDescriptor(desc, descriptor, instance, arguments);
        }
        // extensions could fill variables
        if (descriptor.el != null) {
            desc.command = ElUtils.replace(desc.command, desc.elVars);
        }
    }

    @SuppressWarnings("unchecked")
    private void amendCommand(final OCommandRequest query, final T descriptor,
                              final Object instance, final Object... arguments) {
        for (CommandExtension ext : descriptor.amendExtensions) {
            ext.amendCommand(query, descriptor, instance, arguments);
        }
    }

    private Object executeCommand(final RepositoryMethodDescriptor descriptor,
                                  final SqlCommandDescriptor desc, final OCommandRequest query) {
        try {
            final OCommandRequest cmd = descriptor.executor.wrapCommand(query);
            final Object result;
            if (desc.useNamedParams) {
                result = desc.namedParams.size() > 0
                        ? cmd.execute(desc.namedParams) : cmd.execute();
            } else {
                result = desc.params.length > 0
                        ? cmd.execute(desc.params) : cmd.execute();
            }
            return result;
        } catch (Throwable th) {
            final Joiner joiner = Joiner.on(",");
            final String params = desc.useNamedParams
                    ? joiner.withKeyValueSeparator("=").join(desc.namedParams)
                    : joiner.join(desc.params);
            throw new CommandMethodException(String.format("Failed to execute command '%s' with parameters: %s",
                    desc.command, params), th);
        }
    }
}

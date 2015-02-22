package ru.vyarus.guice.persist.orient.repository.command.function;

import com.google.common.base.Strings;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.command.script.OCommandFunction;
import ru.vyarus.guice.persist.orient.repository.core.ext.SpiService;
import ru.vyarus.guice.persist.orient.repository.core.spi.DescriptorContext;
import ru.vyarus.guice.persist.orient.repository.command.core.AbstractCommandExtension;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Function method extension. Defined by {@link Function} annotation.
 *
 * @author Vyacheslav Rusakov
 * @since 02.02.2015
 */
@Singleton
public class FunctionMethodExtension extends AbstractCommandExtension<CommandMethodDescriptor, Function> {

    @Inject
    public FunctionMethodExtension(final SpiService spiService) {
        super(spiService);
    }

    @Override
    public CommandMethodDescriptor createDescriptor(final DescriptorContext context, final Function annotation) {
        final CommandMethodDescriptor descriptor = new CommandMethodDescriptor();
        descriptor.command = Strings.emptyToNull(annotation.value());
        descriptor.connectionHint = annotation.connection();
        descriptor.returnCollectionHint = annotation.returnAs();

        analyzeElVars(descriptor, context);
        analyzeParameters(descriptor, context);
        return descriptor;
    }

    @Override
    protected OCommandRequest createQueryCommand(final CommandMethodDescriptor descriptor,
                                                 final SqlCommandDescriptor desc) {
        return new OCommandFunction(desc.command);
    }
}

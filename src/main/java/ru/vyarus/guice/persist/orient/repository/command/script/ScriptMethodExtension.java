package ru.vyarus.guice.persist.orient.repository.command.script;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.command.script.OCommandScript;
import ru.vyarus.guice.persist.orient.repository.command.core.AbstractCommandExtension;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.ext.SpiService;
import ru.vyarus.guice.persist.orient.repository.core.spi.DescriptorContext;

import javax.inject.Singleton;

/**
 * {@link Script} method extension.
 *
 * @author Vyacheslav Rusakov
 * @since 25.02.2015
 */
@Singleton
public class ScriptMethodExtension extends AbstractCommandExtension<ScriptCommandMethodDescriptor, Script> {

    @Inject
    public ScriptMethodExtension(final SpiService spiService) {
        super(spiService);
    }

    @Override
    public ScriptCommandMethodDescriptor createDescriptor(final DescriptorContext context, final Script annotation) {
        final ScriptCommandMethodDescriptor descriptor = new ScriptCommandMethodDescriptor();
        descriptor.command = annotation.value();
        descriptor.language = annotation.language();
        descriptor.connectionHint = annotation.connection();
        descriptor.returnCollectionHint = annotation.returnAs();

        analyzeElVars(descriptor, context);
        analyzeParameters(descriptor, context);
        return descriptor;
    }

    @Override
    protected OCommandRequest createQueryCommand(final ScriptCommandMethodDescriptor descriptor,
                                                 final SqlCommandDescriptor desc) {
        return new OCommandScript(descriptor.language, desc.command);
    }
}

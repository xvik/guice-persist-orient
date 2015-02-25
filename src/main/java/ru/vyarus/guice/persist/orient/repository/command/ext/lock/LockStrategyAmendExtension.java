package ru.vyarus.guice.persist.orient.repository.command.ext.lock;

import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.command.OCommandRequestAbstract;
import com.orientechnologies.orient.core.storage.OStorage;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandExtension;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.CommandMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.command.core.spi.SqlCommandDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException;
import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethodExtension;

import javax.inject.Singleton;

/**
 * {@link LockStrategy} amend annotation extension.
 *
 * @author Vyacheslav Rusakov
 * @since 24.02.2015
 */
@Singleton
public class LockStrategyAmendExtension implements AmendMethodExtension<CommandMethodDescriptor, LockStrategy>,
        CommandExtension<CommandMethodDescriptor> {

    public static final String KEY = LockStrategyAmendExtension.class.getName();

    @Override
    public void handleAnnotation(final CommandMethodDescriptor descriptor, final LockStrategy annotation) {
        descriptor.extDescriptors.put(KEY, annotation.value());
    }

    @Override
    public void amendCommandDescriptor(final SqlCommandDescriptor sql, final CommandMethodDescriptor descriptor,
                                       final Object instance, final Object... arguments) {
        // not needed
    }

    @Override
    public void amendCommand(final OCommandRequest query, final CommandMethodDescriptor descriptor,
                             final Object instance, final Object... arguments) {
        // exception, to force user remove annotation
        // it may cause problem if annotation applied on type and single method not support it,
        // but still it's better to know then be sure that lock is applied
        MethodDefinitionException.check(query instanceof OCommandRequestAbstract,
                "@LockStrategy can't be applied to query, because command object %s doesn't support it",
                query.getClass().getName());
        final OStorage.LOCKING_STRATEGY strategy = (OStorage.LOCKING_STRATEGY) descriptor.extDescriptors.get(KEY);
        ((OCommandRequestAbstract) query).setLockStrategy(strategy);
    }
}

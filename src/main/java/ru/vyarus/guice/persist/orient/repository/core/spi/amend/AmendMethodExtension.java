package ru.vyarus.guice.persist.orient.repository.core.spi.amend;

import ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor;

import java.lang.annotation.Annotation;

/**
 * Extension used when method behaviour must be changed (e.g. to set query timeout or log query sql).
 * <p>
 * Amend extensions are driven by annotations annotated with
 * {@link ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethod}.
 * <p>
 * For example, if query and parameters need to be logged for some query extension method. To add logging,
 * new amend annotation extension should be defined (e.g. @LogQueries). Annotation extension must implement
 * amend extension interface.
 * <p>
 * If implementation also implements
 * {@link ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendExecutionExtension} it will be
 * automatically registered.
 * <p>
 * Extension instance is obtained from guice context. Prefer using singleton scope for performance.
 * If you will use other scope, note that resolved extension instance is assigned to descriptor and will be
 * used for all future method calls. So non singleton scope make sense only if you need different
 * instances in each method.
 * <p>
 * Use {@link ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor#extDescriptors} to
 * store extension specific data to use it execution extension part (and leave extension instance stateless).
 * <p>
 * Extensions executed in order of annotations resolution, but you may use
 * {@link ru.vyarus.guice.persist.orient.db.util.Order} annotation if extension also implements
 * {@link ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendExecutionExtension} to affect execution
 * order.
 * <p>
 * If extension defined on type, and it's incompatible with currently analyzed method (by required descriptor),
 * then extension will not be used for this method. This is useful when, most methods in class support extension
 * and only few doesn't.
 *
 * @param <T> required descriptor type
 * @param <A> extension annotation type
 * @author Vyacheslav Rusakov
 * @see ru.vyarus.guice.persist.orient.repository.core.ext.service.AmendExtensionsService
 * @since 07.02.2015
 */
public interface AmendMethodExtension<T extends RepositoryMethodDescriptor, A extends Annotation> {

    /**
     * Called just after method parameters processing.
     * <p>
     * Use {@link ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException} for usage specific
     * errors.
     *
     * @param descriptor repository method descriptor
     * @param annotation amend annotation
     */
    void handleAnnotation(T descriptor, A annotation);
}

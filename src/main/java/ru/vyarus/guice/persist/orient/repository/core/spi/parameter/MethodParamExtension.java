package ru.vyarus.guice.persist.orient.repository.core.spi.parameter;

import ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Extension used to handle parameter value in special way (e.g. pagination, specially computed value etc.)
 * <p>
 * Parameter extensions are driven by annotation, annotated with
 * {@link ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParam}.
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
 *  Extensions executed in order of parameters resolution (left to right), but you may use
 * {@link ru.vyarus.guice.persist.orient.db.util.Order} annotation if extension also implements
 * {@link ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendExecutionExtension} to affect execution
 * order.
 *
 * @param <T> descriptor type
 * @param <P> parameters context type
 * @param <A> parameter annotation type
 * @author Vyacheslav Rusakov
 * @since 03.02.2015
 */
public interface MethodParamExtension<T extends RepositoryMethodDescriptor, P extends ParamsContext,
        A extends Annotation> {

    /**
     * Called to process all annotated method parameters at once.
     * <p>
     * Use {@link ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException} for usage specific
     * errors.
     *
     * @param descriptor repository method descriptor
     * @param context    params context
     * @param paramsInfo     found annotated params
     */
    void processParameters(T descriptor, P context, List<ParamInfo<A>> paramsInfo);
}

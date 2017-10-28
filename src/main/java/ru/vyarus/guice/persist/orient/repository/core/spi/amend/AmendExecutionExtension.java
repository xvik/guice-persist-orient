package ru.vyarus.guice.persist.orient.repository.core.spi.amend;

/**
 * Amend extensions modifies (or extends) repository method behaviour.
 * Method extensions may be completely different, so base interface did not contain anything.
 * It is used just in general extension detection mechanism (handled by
 * {@link ru.vyarus.guice.persist.orient.repository.core.ext.service.AmendExtensionsService}).
 * <p>
 * For example of amend extension see @Size and @Limit parameter annotations: core query extension is
 * not aware of  pagination, but provides custom amend interface, which allows pagination extensions to
 * modify query and command object.
 * <p>
 * Amend extension can't be registered directly, but it's more like method extension api.
 * To register such extension custom amend annotation extension or parameter extension must be registered and
 * if extension will implement amend execution interface it will be automatically registered.
 * <p>
 * Use {@link ru.vyarus.guice.persist.orient.db.util.Order} annotation to order extensions.
 * <p>
 * Execution extensions type is checked during descriptor creation and incompatible extensions are filtered out
 * (check using specific extension type defined in repository method descriptor generic).
 * <p>
 * Use {@link ru.vyarus.guice.persist.orient.repository.core.MethodExecutionException} for usage specific
 * errors.
 *
 * @author Vyacheslav Rusakov
 * @see ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethodExtension
 * @see ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParamExtension
 * @since 05.02.2015
 */
public interface AmendExecutionExtension {
}

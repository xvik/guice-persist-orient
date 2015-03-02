package ru.vyarus.guice.persist.orient.repository.core.ext.util;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.repository.core.spi.RepositoryMethodDescriptor;
import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendExecutionExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethod;
import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethodExtension;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.MethodParamExtension;
import ru.vyarus.guice.persist.orient.repository.core.util.RepositoryUtils;
import ru.vyarus.java.generics.resolver.GenericsResolver;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.List;

import static ru.vyarus.guice.persist.orient.repository.core.MethodDefinitionException.check;

/**
 * Extensions compatibility utilities. Most compatibility checks are done by descriptor, used during method
 * processing and generic descriptor type, defined in extension.
 *
 * @author Vyacheslav Rusakov
 * @since 22.02.2015
 */
public final class ExtCompatibilityUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtCompatibilityUtils.class);

    private ExtCompatibilityUtils() {
    }

    /**
     * Checks that parameter extension is compatible with descriptor and throws error if extension incompatible.
     *
     * @param descriptorType method descriptor type
     * @param paramExtType   parameter extension type
     */
    public static void checkParamExtensionCompatibility(
            final Class<? extends RepositoryMethodDescriptor> descriptorType,
            final Class<? extends MethodParamExtension> paramExtType) {
        check(isCompatible(paramExtType, MethodParamExtension.class, descriptorType),
                "Param extension %s is incompatible with descriptor %s", paramExtType.getSimpleName(),
                descriptorType.getSimpleName());
    }

    /**
     * Checks that amend extensions strictly compatible with descriptor object.
     * Used for amend annotations defined on method.
     *
     * @param descriptorType method descriptor type
     * @param extensions     amend extension annotations
     */
    public static void checkAmendExtensionsCompatibility(
            final Class<? extends RepositoryMethodDescriptor> descriptorType,
            final List<Annotation> extensions) {
        final List<Class<? extends AmendMethodExtension>> exts =
                Lists.transform(extensions, new Function<Annotation, Class<? extends AmendMethodExtension>>() {
                    @Nonnull
                    @Override
                    public Class<? extends AmendMethodExtension> apply(@Nonnull final Annotation input) {
                        return input.annotationType().getAnnotation(AmendMethod.class).value();
                    }
                });
        for (Class<? extends AmendMethodExtension> ext : exts) {
            check(isCompatible(ext, AmendMethodExtension.class, descriptorType),
                    "Amend extension %s is incompatible with descriptor %s", ext.getSimpleName(),
                    descriptorType.getSimpleName());
        }
    }

    /**
     * General convention: extensions declare descriptor with generic T.
     *
     * @param extensionClass extension class
     * @param extensionType  extension type (interface to resolve generic on)
     * @param descriptorType repository method descriptor type
     * @return true if extension is compatible with descriptor, false otherwise
     */
    public static boolean isCompatible(final Class<?> extensionClass, final Class<?> extensionType,
                                       final Class<? extends RepositoryMethodDescriptor> descriptorType) {
        final Class<?> compatibleDescriptor = GenericsResolver.resolve(extensionClass)
                .type(extensionType).generic("T");
        return compatibleDescriptor.isAssignableFrom(descriptorType);
    }

    /**
     * Checks resolved amend extensions compatibility with method specific extension type.
     * Extension may be universal and support some methods and doesn't support other.
     *
     * @param extensions     extensions to check
     * @param descriptorType repository method descriptor type
     * @return filtered extensions list (safe to use by method extension)
     */
    public static List<AmendExecutionExtension> filterCompatibleExtensions(
            final List<AmendExecutionExtension> extensions,
            final Class<? extends RepositoryMethodDescriptor> descriptorType) {
        @SuppressWarnings("unchecked")
        final Class<? extends AmendExecutionExtension> supportedExtension =
                (Class<? extends AmendExecutionExtension>) GenericsResolver.resolve(descriptorType)
                        .type(RepositoryMethodDescriptor.class).generic("E");
        return Lists.newArrayList(Iterables.filter(extensions, new Predicate<AmendExecutionExtension>() {
            @Override
            public boolean apply(@Nonnull final AmendExecutionExtension ext) {
                final Class<?> rawExtType = RepositoryUtils.resolveRepositoryClass(ext);
                final boolean compatible = supportedExtension.isAssignableFrom(rawExtType);
                if (!compatible) {
                    LOGGER.debug("Extension {} ignored, because it doesn't implement required extension "
                            + "interface {}", rawExtType.getSimpleName(), supportedExtension.getSimpleName());
                }
                return compatible;
            }
        }));
    }
}

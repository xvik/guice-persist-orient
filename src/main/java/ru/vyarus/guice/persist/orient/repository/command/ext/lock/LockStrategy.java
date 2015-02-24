package ru.vyarus.guice.persist.orient.repository.command.ext.lock;

import com.orientechnologies.orient.core.storage.OStorage;
import ru.vyarus.guice.persist.orient.repository.core.spi.amend.AmendMethod;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Lock amend extension annotation. Used to specify locking strategy
 * (see {@link com.orientechnologies.orient.core.command.OCommandRequestAbstract#setLockStrategy(
 *com.orientechnologies.orient.core.storage.OStorage.LOCKING_STRATEGY)}).
 * <p>Could be applied directly on method or on type (to apply for all methods).
 * If applied in both places, method annotation used).</p>
 *
 * @author Vyacheslav Rusakov
 * @since 24.02.2015
 */
@Documented
@Target({METHOD, TYPE})
@Retention(RUNTIME)
@AmendMethod(LockStrategyAmendExtension.class)
public @interface LockStrategy {

    /**
     * @return locking strategy to use
     */
    OStorage.LOCKING_STRATEGY value();
}

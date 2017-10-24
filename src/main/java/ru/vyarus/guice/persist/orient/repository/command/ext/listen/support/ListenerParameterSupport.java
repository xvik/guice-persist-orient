package ru.vyarus.guice.persist.orient.repository.command.ext.listen.support;

import com.google.inject.Injector;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.command.OCommandResultListener;
import ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen;
import ru.vyarus.guice.persist.orient.repository.core.spi.parameter.ParamInfo;

import java.lang.annotation.Annotation;

/**
 * Listener parameter handler (should be implemented separately per method extension).
 *
 * @author Vyacheslav Rusakov
 * @since 29.09.2017
 */
public interface ListenerParameterSupport {

    /**
     * @param extension method extension annotation
     * @return true if listener support recognized matching extension
     */
    boolean accept(Class<? extends Annotation> extension);

    /**
     * Check listener parameter correctness.
     *
     * @param query      query string
     * @param param      annotated listener parameter
     * @param returnType method return type
     */
    void checkParameter(String query, ParamInfo<Listen> param, Class<?> returnType);

    /**
     * Checks listener compatibility with command object and wraps listener if required.
     *
     * @param command          command object
     * @param listener         listener instance (passed in annotated parameter)
     * @param injector         injector instance
     * @param conversionTarget target conversion type or null if no conversion required
     * @return processed listener to apply to command
     */
    OCommandResultListener processListener(OCommandRequest command,
                                           Object listener,
                                           Injector injector,
                                           Class<?> conversionTarget);
}

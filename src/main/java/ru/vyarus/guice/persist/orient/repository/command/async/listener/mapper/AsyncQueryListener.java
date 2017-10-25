package ru.vyarus.guice.persist.orient.repository.command.async.listener.mapper;

import ru.vyarus.guice.persist.orient.repository.command.ext.listen.support.RequiresRecordConversion;

/**
 * Special {@link ru.vyarus.guice.persist.orient.repository.command.async.AsyncQuery} result listener
 * intended to be used instead of raw {@link com.orientechnologies.orient.core.command.OCommandResultListener})
 * (with {@link ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen}) to automatically
 * apply the same result conversion as applied to the result of repository method (including conversion of document
 * to object or vertex).
 * <p>
 * Generic value declares target conversion type. If possible, conversion type would be resolved from actual
 * listener instance, so base class may be defined in query and exact entity used by listener (e.g.
 * {@code @Listen AsyncQueryListener<VersionedEntity>} and actual listener could be
 * {@code MyListener implements AsyncQueryListener<Model>}. If it is not possible to resolve generic on listener
 * instance, then parameter generic will be used.
 * <p>
 * Note that {@link ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen} starts external transaction
 * ({@link ru.vyarus.guice.persist.orient.db.transaction.TxConfig#external()}) to make guice aware of listener
 * connection object (bound to thread). This means that connection could be obtained as usual inside the listener.
 * <p>
 * NOTE: result conversion will not apply custom converter extensions
 * (like {@link ru.vyarus.guice.persist.orient.repository.core.ext.service.result.ext.detach.DetachResult}) because
 * extensions rely heavily on method definition and in some cases completely not applicable in case of listener.
 * Only default {@link ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.ResultConverter}
 * is applied.
 * <p>
 * By default async query is blocking, so listener will be called at the same thread and control released only after
 * processing (see {@link ru.vyarus.guice.persist.orient.repository.command.async.AsyncQuery#blocking()} to switch
 * to non blocking).
 * <p>
 * To use guice injections inside listener, simply use guice to produce your listener. For example,
 * {@code Provider<MyListener> listenerProvider} will always return new listener instance (assuming prototype scope)
 * with filled injections.
 *
 * @param <T> type of converted object (target conversion type)
 * @author Vyacheslav Rusakov
 * @since 14.10.2017
 */
public interface AsyncQueryListener<T> extends RequiresRecordConversion<T> {

    /**
     * Called for each result.
     *
     * @param result result object
     * @return true to continue query, false to stop
     */
    boolean onResult(T result);

    /**
     * Called at the end of processing.
     */
    void onEnd();
}

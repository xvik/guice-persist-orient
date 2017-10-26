package ru.vyarus.guice.persist.orient.repository.command.live.listener.mapper;

import ru.vyarus.guice.persist.orient.repository.command.ext.listen.support.RequiresRecordConversion;
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.RecordConverter;

/**
 * Special {@link ru.vyarus.guice.persist.orient.repository.command.live.LiveQuery} result listener
 * intended to be used instead of raw {@link com.orientechnologies.orient.core.sql.query.OLiveResultListener}
 * (with {@link ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen}) to automatically
 * apply the same result conversion as applied to the result of repository method (including conversion of document
 * to object or vertex).
 * <p>
 * Generic value declares target conversion type. If possible, conversion type would be resolved from actual
 * listener instance, so base class may be defined in query and exact entity used by listener (e.g.
 * {@code @Listen LiveQueryListener<VersionedEntity>} and actual listener could be
 * {@code MyListener implements LiveQueryListener<Model>}. If it is not possible to resolve generic on listener
 * instance, then parameter generic will be used.
 * <p>
 * Live query always return object (it is the essence of live query). So even if you select just field
 * (select name from Model) still complete object will be returned (so something like
 * {@code LiveQueryListener<String>} will never work - always use complete entities).
 * <p>
 * NOTE: result conversion will not apply custom converter extensions
 * (like {@link ru.vyarus.guice.persist.orient.repository.core.ext.service.result.ext.detach.DetachResult}) because
 * extensions rely heavily on method definition and in some cases completely not applicable in case of listener.
 * Only default {@link ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.ResultConverter}
 * is applied.
 * <p>
 * Note that {@link ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen} starts external transaction
 * ({@link ru.vyarus.guice.persist.orient.db.transaction.TxConfig#external()}) to make guice aware of listener
 * connection object (bound to thread). This means that connection could be obtained as usual inside the listener.
 * <p>
 * To use guice injections inside listener, simply use guice to produce your listener. For example,
 * {@code Provider<MyListener> listenerProvider} will always return new listener instance (assuming prototype scope)
 * with filled injections. Any bean (service) may also be used as listener ({@code subscribe(this)}), but it is not
 * always applicable.
 *
 * @param <T> type of converted object (target conversion type)
 * @author Vyacheslav Rusakov
 * @see RecordConverter
 * @since 03.10.2017
 */
public interface LiveQueryListener<T> extends RequiresRecordConversion<T> {

    /**
     * Notifies about subscribed query results changes.
     * Raw orient result (most likely Document) would be converted to target type T.
     * <p>
     * Call is performed under external transaction, so thread bound connection is available to guice.
     *
     * @param token     live subscription token
     * @param operation operation type
     * @param result    converted result
     * @throws Exception on result handling errors
     * @see ru.vyarus.guice.persist.orient.repository.command.live.listener.TransactionalLiveAdapter
     */
    void onLiveResult(int token, RecordOperation operation, T result) throws Exception;

    /**
     * Called in case of error.
     *
     * @param token live subscription token
     */
    void onError(int token);

    /**
     * Called to notify listener unsubscription.
     *
     * @param token live subscription token
     */
    void onUnsubscribe(int token);
}

package ru.vyarus.guice.persist.orient.repository.command.live.mapper;

import ru.vyarus.guice.persist.orient.repository.command.ext.listen.support.RequiresRecordConversion;
import ru.vyarus.guice.persist.orient.repository.core.ext.service.result.converter.RecordConverter;

/**
 * Special live result listener (like {@link com.orientechnologies.orient.core.sql.query.OLiveResultListener}),
 * which could be used in repository method
 * (with {@link ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen}) to automatically convert
 * raw document to object or graph type and apply result converter (mimic usual repository method return
 * type behaviour).
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
 * Object and vertex conversion requires transaction. By default listener is wrapped with transaction
 * (see {@link ru.vyarus.guice.persist.orient.repository.command.ext.listen.Listen#transactional()}). If transaction
 * will be switched off, type conversion will not be performed.
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
     * IMPORTANT: result conversion to object or graph type will be performed only if there is an ongoing
     * transaction.
     *
     * @param token     live subscription token
     * @param operation operation type
     * @param result    converted result
     * @throws Exception on result handling errors
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

package ru.vyarus.guice.persist.orient.repository.command.ext.listen.support;

/**
 * Common interface for custom listener types with conversion (
 * {@link ru.vyarus.guice.persist.orient.repository.command.async.listener.mapper.AsyncQueryListener} and
 * {@link ru.vyarus.guice.persist.orient.repository.command.live.listener.mapper.LiveQueryListener}) used to resolve
 * expected type from parameter. It is useful when target conversion type is impossible to resolve from listener
 * instance (which can contain more concrete type - it may be important for proper object hierarchies support).
 * <p>
 * For example, in this case type of parameter is impossible to resolve:
 * <pre>{@code
 *  class MyListener<T> extend AsyncQueryListener<T> { ... }
 *  MyListener<Model> lst = new MyListener<Model>();
 *  repository.query(lst);
 * }</pre>
 * Here generic is unreachable (there is no type which specifies it). In this case generic could only be resolved
 * from parameter declaration:
 * {@code @AsyncQuery("select from...") query(@Listen AsyncQueryListener<Model> listener)}.
 *
 * @author Vyacheslav Rusakov
 * @param <T> converted type
 * @since 16.10.2017
 */
public interface RequiresRecordConversion<T> {
}

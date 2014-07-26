package ru.vyarus.guice.persist.orient.db.transaction;

import com.orientechnologies.orient.core.tx.OTransaction;

import java.lang.annotation.*;

/**
 * <p>Defines orient transaction type to use within current transaction.
 * Must be used together with guice @Transactional annotation.
 * May be defined on type or method, but not necessary near @Transactional annotation. E.g. @Transactional could be
 * set on type and @TxType on single bean method, which will change transaction type only for this method.
 * </p>
 * To switch of transaction within unit of work (defined by @Transactional annotation) use
 * {@code OTransaction.TXTYPE.NOTX}
 * <p>Additional annotation was chosen in order to not introduce new annotation for transaction and re-use guice-ersist one.
 * Moreover, transaction type definition should be a rear case (because if its not you should change default transaction
 * type in module and use @TxType annotation just for rear cases</p>
 *
 * @author Vyacheslav Rusakov
 * @since 25.07.2014
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TxType {
    OTransaction.TXTYPE value();
}

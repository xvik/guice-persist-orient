package ru.vyarus.guice.persist.orient.template;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

public interface TransactionalAction<T> {

    T execute(OObjectDatabaseTx db) throws Throwable;
}

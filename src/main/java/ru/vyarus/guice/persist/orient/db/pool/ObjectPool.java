package ru.vyarus.guice.persist.orient.db.pool;

import com.orientechnologies.orient.core.db.ODatabasePoolBase;
import com.orientechnologies.orient.object.db.OObjectDatabasePool;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.db.transaction.TransactionManager;
import ru.vyarus.guice.persist.orient.db.user.UserManager;

import javax.inject.Inject;

/**
 * Object connection pool.
 *
 * @author Vyacheslav Rusakov
 * @since 24.07.2014
 */
public class ObjectPool extends AbstractPool<OObjectDatabaseTx> {

    @Inject
    public ObjectPool(final TransactionManager transactionManager, final UserManager userManager) {
        super(transactionManager, userManager);
    }


    @Override
    protected ODatabasePoolBase createPool() {
        return new OObjectDatabasePool();
    }

    @Override
    public DbType getType() {
        return DbType.OBJECT;
    }
}

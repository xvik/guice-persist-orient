package ru.vyarus.guice.persist.orient.db.pool.object;

import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.object.db.OCommandSQLPojoWrapper;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Fixed version of {@link com.orientechnologies.orient.object.db.OCommandSQLPojoWrapper} used in
 * {@link OObjectDatabaseTx#command(com.orientechnologies.orient.core.command.OCommandRequest)}. This is a
 * deprecated api and so would not be fixed by orient team. But, as current repositories implementation still works
 * with separate connection types, have to fix this myself (the problem is that
 * {@link OCommandSQLPojoWrapper#execute(java.lang.Object...) does not expect anything except
 * {@link com.orientechnologies.orient.core.record.impl.ODocument}}, but in some cases
 * {@link com.orientechnologies.orient.core.sql.executor.OResultInternal} could return. As a result, original class
 * causes {@link ClassCastException} in case of {@link java.util.Collection} result and there is no way to correct it
 * except re-writing entire wrapper.)
 *
 * @author Vyacheslav Rusakov
 * @since 28.06.2021
 */
public class OCommandSQLPojoWrapperFixed extends OCommandSQLPojoWrapper {

    private final OCommandRequest command;
    private final OObjectDatabaseTxFixed database;

    public OCommandSQLPojoWrapperFixed(final OObjectDatabaseTxFixed iDatabase,
                                       final OCommandRequest iCommand) {
        super(iDatabase, iCommand);
        command = iCommand;
        database = iDatabase;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <RET> RET execute(final Object... iArgs) {
        database.convertParameters(iArgs);

        Object result = command.execute(iArgs);

        if (result instanceof Collection<?>) {
            final List<Object> resultPojo = new ArrayList<>();

            Object obj;
            final Collection coll = (Collection) result;
            for (Object res : coll) {
                if (res == null) {
                    // orient bug, but they would not fix it
                    throw new IllegalStateException("Orient returned null instead of result item: that means result "
                            + "must be present (" + coll.size() + " items, problem on item " + (resultPojo.size())
                            + "), but it was incorrect. This is a known issue of the DEPRECATED commands "
                            + "api (with remote api). Nothing I can do with it, just warn...");
                }

                if (res instanceof ODocument) {
                    final ODocument doc = (ODocument) res;
                    // GET THE ASSOCIATED DOCUMENT
                    if (doc.getClassName() == null) {
                        obj = doc;
                    } else {
                        // CONVERT THE DOCUMENT INSIDE THE LIST
                        obj = database.getUserObjectByRecord(doc, getFetchPlan(), true);
                    }
                } else {
                    // OResultInternal case
                    obj = database.getUserObjectByRecord(((OResult) res).getIdentity().get(), getFetchPlan(), true);
                }

                resultPojo.add(obj);
            }
            result = resultPojo;

        } else if (result instanceof ODocument) {
            if (((ODocument) result).getClassName() != null) {
                // CONVERT THE SINGLE DOCUMENT
                result = database.getUserObjectByRecord((ODocument) result, getFetchPlan(), true);
            }
        } else if (result instanceof OResult) {
            // OResultInternal case
            result = database.getUserObjectByRecord(((OResult) result).getIdentity().get(), getFetchPlan(), true);
        }

        return (RET) result;
    }
}

package ru.vyarus.guice.persist.orient.db.pool.object;

import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

/**
 * Fixed version of {@link OObjectDatabaseTx}: corrects some cases of result conversions (bugs which will not be fixed
 * in the deprecated api).
 * <p>
 * SHOULD BE REMOVED with new repositories implementation (not using deprecated apis).
 *
 * @author Vyacheslav Rusakov
 * @since 28.06.2021
 */
public class OObjectDatabaseTxFixed extends OObjectDatabaseTx {

    public OObjectDatabaseTxFixed(String iURL) {
        super(iURL);
    }

    public OObjectDatabaseTxFixed(ODatabaseDocumentInternal iDatabase) {
        super(iDatabase);
    }

    @Override
    public <RET extends OCommandRequest> RET command(OCommandRequest iCommand) {
        return (RET) new OCommandSQLPojoWrapperFixed(this, underlying.command(iCommand));
    }

    // fixing access required for fixed wrapper command
    @Override
    public void convertParameters(Object... iArgs) {
        super.convertParameters(iArgs);
    }
}

package ru.vyarus.guice.persist.orient.db.pool.object;

import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.exception.ORecordNotFoundException;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Fixed version of {@link OObjectDatabaseTx}: corrects some cases of result conversions (bugs which will not be fixed
 * in the deprecated api).
 * <p>
 * SHOULD BE REMOVED with new repositories implementation (not using deprecated apis).
 *
 * @author Vyacheslav Rusakov
 * @since 28.06.2021
 */
@SuppressFBWarnings("HE_INHERITS_EQUALS_USE_HASHCODE")
@SuppressWarnings("unchecked")
public class OObjectDatabaseTxFixed extends OObjectDatabaseTx {

    public OObjectDatabaseTxFixed(final String iURL) {
        super(iURL);
    }

    public OObjectDatabaseTxFixed(final ODatabaseDocumentInternal iDatabase) {
        super(iDatabase);
    }

    @Override
    public <RET extends OCommandRequest> RET command(final OCommandRequest iCommand) {
        return (RET) new OCommandSQLPojoWrapperFixed(this, underlying.command(iCommand));
    }

    @Override
    // changing access, required for fixed wrapper command
    @SuppressWarnings("PMD.UselessOverridingMethod")
    public void convertParameters(final Object... iArgs) {
        super.convertParameters(iArgs);
    }

    @Override
    public <RET> RET load(final ORID iRecordId, final String iFetchPlan, final boolean iIgnoreCache) {
        // fixing behaviour after record deletion under ongoing transaction (to unify with legacy behaviour)
        try {
           return super.load(iRecordId, iFetchPlan, iIgnoreCache);
        } catch (ORecordNotFoundException ex) {
            return null;
        }
    }
}

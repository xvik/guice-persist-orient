package ru.vyarus.guice.persist.orient.db.pool.object;

import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import edu.umd.cs.findbugs.annotations.SuppressWarnings;

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
public class OObjectDatabaseTxFixed extends OObjectDatabaseTx {

    public OObjectDatabaseTxFixed(final String iURL) {
        super(iURL);
    }

    public OObjectDatabaseTxFixed(final ODatabaseDocumentInternal iDatabase) {
        super(iDatabase);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <RET extends OCommandRequest> RET command(final OCommandRequest iCommand) {
        return (RET) new OCommandSQLPojoWrapperFixed(this, underlying.command(iCommand));
    }

    @Override
    // changing access, required for fixed wrapper command
    @SuppressWarnings("PMD.UselessOverridingMethod")
    public void convertParameters(final Object... iArgs) {
        super.convertParameters(iArgs);
    }
}

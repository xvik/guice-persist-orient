package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.type.edge;

import com.google.common.base.Preconditions;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.guice.persist.orient.db.DatabaseManager;
import ru.vyarus.guice.persist.orient.db.DbType;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.SchemeDescriptor;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi.type.TypeExtension;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.util.SchemeUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * {@link EdgeType} scheme type extension.
 *
 * @author Vyacheslav Rusakov
 * @since 04.03.2015
 */
@Singleton
public class EdgeTypeExtension implements TypeExtension<EdgeType> {
    private final Logger logger = LoggerFactory.getLogger(EdgeTypeExtension.class);

    private final DatabaseManager databaseManager;

    @Inject
    public EdgeTypeExtension(final DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void beforeRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                   final EdgeType annotation) {
        final String schemeClass = descriptor.schemeClass;
        Preconditions.checkState(databaseManager.isTypeSupported(DbType.GRAPH),
                "Model class %s can't be registered as graph type, because no graph support available",
                schemeClass);
        SchemeUtils.assignSuperclass(db, descriptor.modelClass, "E", logger);
    }

    @Override
    public void afterRegistration(final ODatabaseObject db, final SchemeDescriptor descriptor,
                                  final EdgeType annotation) {
        // not needed
    }
}

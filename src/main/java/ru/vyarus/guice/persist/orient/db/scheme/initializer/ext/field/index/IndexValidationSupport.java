package ru.vyarus.guice.persist.orient.db.scheme.initializer.ext.field.index;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.slf4j.Logger;
import ru.vyarus.guice.persist.orient.db.scheme.initializer.core.util.SchemeUtils;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static ru.vyarus.guice.persist.orient.db.scheme.SchemeInitializationException.check;

/**
 * Helper class for index scheme extensions to check and compare existing index with defined signature.
 *
 * @author Vyacheslav Rusakov
 * @since 15.06.2015
 */
public class IndexValidationSupport {

    private final OIndex index;
    private final Logger logger;

    public IndexValidationSupport(final OIndex index, final Logger logger) {
        this.index = index;
        this.logger = logger;
    }

    /**
     * @return managed index instance
     */
    public OIndex getIndex() {
        return index;
    }

    /**
     * Checks that current index type is equal to one of provided types.
     * If not, exception thrown. Type check is important for fulltext and lucene indexes because,
     * most likely, if existing index type is different from that specific index type then index name was set
     * by mistake and to avoid replacing existing index error should be thrown (programmer mistake).
     *
     * @param types allowed index types
     */
    public void checkTypeCompatible(final OClass.INDEX_TYPE... types) {
        final Set<String> allowed = Sets.newHashSet(Iterables.transform(Arrays.asList(types),
                new Function<OClass.INDEX_TYPE, String>() {
                    @Nonnull
                    @Override
                    public String apply(@Nonnull final OClass.INDEX_TYPE input) {
                        return input.name();
                    }
                }));
        check(allowed.contains(index.getType()),
                "Existing index '%s' (class '%s') type '%s' is incompatible with '%s'. "
                        + "Either drop existing index or rename index.",
                index.getName(), index.getDefinition().getClassName(), index.getType(), Joiner.on(',').join(allowed));
    }

    /**
     * Checks if existing index consists of exactly the same fields. If not, error thrown to indicate
     * probable programmer error.
     *
     * @param fields new signature index fields
     */
    public void checkFieldsCompatible(final String... fields) {
        final Set<String> indexFields = Sets.newHashSet(index.getDefinition().getFields());
        final Joiner joiner = Joiner.on(",");
        check(indexFields.equals(Sets.newHashSet(fields)),
                "Existing index '%s' (class '%s') fields '%s' are different from '%s'.",
                index.getName(), index.getDefinition().getClassName(), joiner.join(indexFields), joiner.join(fields));
    }

    /**
     * Drops index.
     *
     * @param db database object
     */
    public void dropIndex(final ODatabaseObject db) {
        final String name = index.getName();
        logger.info("Dropping existing index '{}' (class '{}'), because of definition mismatch",
                name, index.getDefinition().getClassName());
        SchemeUtils.dropIndex(db, name);
    }

    /**
     * Compares current index configuration with new signature.
     * Used to decide if index should be dropped and re-created or its completely equal to new signature.
     *
     * @param signs original index signs to compare
     * @return matcher object to specify new signature signs (order is important!)
     */
    @SuppressWarnings("PMD.LinguisticNaming")
    public IndexMatchVerifier isIndexSigns(final Object... signs) {
        final List<Object> idxsigns = Lists.newArrayList();
        idxsigns.add(index.getType());
        idxsigns.addAll(Arrays.asList(signs));
        return new IndexMatchVerifier(idxsigns);
    }

    /**
     * Helper class to simplify index signatures check.
     */
    public static class IndexMatchVerifier {
        private final List<Object> indexSigns;

        public IndexMatchVerifier(final List<Object> signs) {
            this.indexSigns = signs;
        }

        /**
         * Checks current index signs with new signature signs.
         *
         * @param type  index type
         * @param signs signs to check
         * @return true if indexes are equal, false otherwise
         */
        public boolean matchRequiredSigns(final OClass.INDEX_TYPE type, final Object... signs) {
            final List<Object> reqsigns = Lists.newArrayList();
            reqsigns.add(type.toString());
            reqsigns.addAll(Arrays.asList(signs));
            Preconditions.checkState(indexSigns.size() == reqsigns.size(), "Incorrect signs count for comparison");
            boolean res = true;
            for (int i = 0; i < indexSigns.size(); i++) {
                if (!Objects.equal(indexSigns.get(i), reqsigns.get(i))) {
                    res = false;
                    break;
                }
            }
            return res;
        }
    }
}

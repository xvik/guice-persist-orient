package ru.vyarus.guice.persist.orient.repository.command.ext.fetchplan;

/**
 * Command fetch plan descriptor object.
 *
 * @author Vyacheslav Rusakov
 * @since 24.02.2015
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
public class FetchPlanDescriptor {

    /**
     * Default fetch plan (defined in annotation).
     */
    public String defPlan;

    /**
     * Fetch plan parameter position.
     */
    public int position;

    public FetchPlanDescriptor(final String defPlan, final int position) {
        this.defPlan = defPlan;
        this.position = position;
    }
}

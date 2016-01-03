package ru.vyarus.guice.persist.orient.db.scheme.initializer.core.spi;

import com.google.common.collect.Maps;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;
import java.util.Map;

/**
 * Model initialization descriptor.
 *
 * @author Vyacheslav Rusakov
 * @since 04.03.2015
 */
@SuppressWarnings("checkstyle:VisibilityModifier")
@SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
public class SchemeDescriptor {

    /**
     * Model class.
     */
    public Class<?> modelClass;

    /**
     * String representation of orient scheme class name (modelClass.simpleName).
     */
    public String schemeClass;

    /**
     * Model class hierarchy types (ignoring interfaces, only types that will be mapped to orient schema).
     */
    public List<Class<?>> modelHierarchy;

    /**
     * Model hierarchy root class.
     */
    public Class<?> modelRootClass;

    /**
     * True if model doesn't exist in scheme.
     * May be modified by extension, e.g. in case of rename extension.
     */
    public boolean initialRegistration;

    /**
     * True when orient registration performed for model.
     */
    public boolean registered;

    /**
     * Shared storage for extensions. May be used to exchange between extensions or to store some
     * data between before/after extension methods (if singleton extension used).
     */
    public Map<String, Object> ext = Maps.newHashMap();
}

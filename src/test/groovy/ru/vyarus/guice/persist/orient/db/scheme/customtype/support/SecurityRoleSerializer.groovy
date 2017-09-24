package ru.vyarus.guice.persist.orient.db.scheme.customtype.support

import com.orientechnologies.orient.core.serialization.serializer.object.OObjectSerializer
import ru.vyarus.guice.persist.orient.db.scheme.customtype.support.model.SecurityRole

/**
 * Serializer is a guice bean ONLY to access it's state from test.
 *
 * @author Vyacheslav Rusakov
 * @since 30.03.2017
 */
class SecurityRoleSerializer implements OObjectSerializer<SecurityRole, String> {

    boolean serializeUsed
    boolean unserializeUsed

    Object serializeFieldValue(Class<?> type, SecurityRole role) {
        serializeUsed = true
        return role.name()
    }

    Object unserializeFieldValue(Class<?> type, String str) {
        unserializeUsed = true
        return SecurityRole.getByName(str)
    }
}

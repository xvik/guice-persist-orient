package ru.vyarus.guice.persist.orient.db.scheme.customtype.support;

import com.orientechnologies.orient.core.serialization.serializer.object.OObjectSerializer;
import ru.vyarus.guice.persist.orient.db.scheme.customtype.support.model.CustomClass;

/**
 * @author Vyacheslav Rusakov
 * @since 16.11.2025
 */
public class CustomClassSerializer implements OObjectSerializer<CustomClass, String> {

    @Override
    public Object serializeFieldValue(Class<?> iClass, CustomClass iFieldValue) {
        return iFieldValue.getValue();
    }

    @Override
    public Object unserializeFieldValue(Class<?> iClass, String iFieldValue) {
        return new CustomClass(iFieldValue);
    }
}

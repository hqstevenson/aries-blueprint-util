package com.pronoia.aries.blueprint.util.metadata;

import java.util.Map;

import org.apache.aries.blueprint.mutable.MutableBeanMetadata;
import org.osgi.service.blueprint.reflect.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class BeanMetadataUtil {
    static final Logger LOG = LoggerFactory.getLogger(BeanMetadataUtil.class);

    BeanMetadataUtil() {
    }

    /**
     * @param beanMetadata
     * @param value
     * @param argumentNumber zero-based
     */
    public static void addArgument(MutableBeanMetadata beanMetadata, Object value, int argumentNumber) {
        if (value instanceof Metadata) {
            beanMetadata.addArgument((Metadata) value, value.getClass().getName(), argumentNumber);
        } else {
            beanMetadata.addArgument(ValueMetadataUtil.create(value), value.getClass().getName(), argumentNumber);
        }
    }

    /**
     * @param beanMetadata
     * @param value
     * @param argumentNumber zero-based
     */
    public static void addArgument(MutableBeanMetadata beanMetadata, Object value, String valueType, int argumentNumber) {
        if (value instanceof Metadata) {
            beanMetadata.addArgument((Metadata) value, valueType, argumentNumber);
        } else {
            beanMetadata.addArgument(ValueMetadataUtil.create(value), valueType, argumentNumber);
        }
    }

    /**
     * @param beanMetadata
     * @param value
     * @param argumentNumber zero-based
     */
    public static void addArgument(MutableBeanMetadata beanMetadata, Object value, Class valueType, int argumentNumber) {
        if (value instanceof Metadata) {
            beanMetadata.addArgument((Metadata) value, valueType.getName(), argumentNumber);
        } else {
            beanMetadata.addArgument(ValueMetadataUtil.create(value), valueType.getName(), argumentNumber);
        }
    }

    /**
     * @param beanMetadata
     * @param values
     * @param <T>
     */
    public static <T> void addArguments(MutableBeanMetadata beanMetadata, T... values) {
        if (values != null && values.length > 0) {
            for (int argumentIndex = 0; argumentIndex < values.length; ++argumentIndex) {
                addArgument(beanMetadata, values[argumentIndex], argumentIndex);
            }
        }
    }

    public static <T> void addProperty(MutableBeanMetadata beanMetadata, String propertyName, T value) {
        if (value instanceof Metadata) {
            beanMetadata.addProperty(propertyName, (Metadata) value);
        } else {
            beanMetadata.addProperty(propertyName, ValueMetadataUtil.create(value));
        }
    }

    public static void addProperties(MutableBeanMetadata beanMetadata, Map<String, Object> properties) {
        if (properties != null && !properties.isEmpty()) {
            for (Map.Entry<String, Object> property : properties.entrySet()) {
                Object value = property.getValue();
                if (value instanceof Metadata) {
                    beanMetadata.addProperty(property.getKey(), (Metadata) value);
                } else {
                    beanMetadata.addProperty(property.getKey(), ValueMetadataUtil.create(value));
                }
            }
        }
    }

}

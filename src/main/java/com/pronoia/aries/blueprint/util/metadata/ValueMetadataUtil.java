package com.pronoia.aries.blueprint.util.metadata;

import org.apache.aries.blueprint.mutable.MutableValueMetadata;
import org.apache.aries.blueprint.reflect.MetadataUtil;
import org.osgi.service.blueprint.reflect.ValueMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class providing helper-methods for createing Blueprint reflect.
 */
public class ValueMetadataUtil {
    static final Logger LOG = LoggerFactory.getLogger(ValueMetadataUtil.class);

    ValueMetadataUtil() {
    }

    /**
     * Create an Aries-specific mutable instance of the standard ValueMetadata interface.
     *
     * @param value the value of the reflect
     *
     * @return a new reflect instance
     */
    public static <T> ValueMetadata create(T value) {
        MutableValueMetadata metadata = MetadataUtil.createMetadata(MutableValueMetadata.class);
        metadata.setType(value.getClass().getName());
        metadata.setStringValue(value.toString());
        return metadata;
    }

    /**
     * Create an Aries-specific mutable instance of the standard ValueMetadata interface.
     *
     * @param value the value of the reflect
     * @param type  the type of the value
     *
     * @return a new reflect instance
     */
    public static ValueMetadata create(Class type, String value) {
        MutableValueMetadata metadata = MetadataUtil.createMetadata(MutableValueMetadata.class);
        metadata.setStringValue(value);
        metadata.setType(type.getName());
        return metadata;
    }

}
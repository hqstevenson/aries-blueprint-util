package com.pronoia.aries.blueprint.util.metadata;

import org.apache.aries.blueprint.mutable.MutablePassThroughMetadata;
import org.apache.aries.blueprint.reflect.MetadataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class providing helper-methods for createing Blueprint reflect.
 */
public class PassThroughMetadataUtil {
    static final Logger LOG = LoggerFactory.getLogger(PassThroughMetadataUtil.class);

    PassThroughMetadataUtil() {
    }

    /**
     * Create an Aries-specific mutable instance of the standard ValueMetadata interface.
     *
     * @return a new reflect instance
     */
    public static MutablePassThroughMetadata create() {
        MutablePassThroughMetadata metadata = MetadataUtil.createMetadata(MutablePassThroughMetadata.class);
        return metadata;
    }

    /**
     * Create an Aries-specific mutable instance of the standard ValueMetadata interface.
     *
     * @param value the value of the reflect
     *
     * @return a new reflect instance
     */
    public static MutablePassThroughMetadata create(Object value) {
        MutablePassThroughMetadata metadata = create();
        metadata.setObject(value);
        return metadata;
    }

}
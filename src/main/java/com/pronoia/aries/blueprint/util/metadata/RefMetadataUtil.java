package com.pronoia.aries.blueprint.util.metadata;

import org.apache.aries.blueprint.mutable.MutableRefMetadata;
import org.apache.aries.blueprint.reflect.MetadataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class providing helper-methods for createing Blueprint reflect.
 */
public class RefMetadataUtil {
    static final Logger LOG = LoggerFactory.getLogger(RefMetadataUtil.class);

    private RefMetadataUtil() {
    }

    /**
     * Create an Aries-specific mutable instance of the standard RefMetadata interface.
     *
     * @return a new reflect instance
     */
    public static MutableRefMetadata create() {
        MutableRefMetadata metadata = MetadataUtil.createMetadata(MutableRefMetadata.class);

        return metadata;
    }

    /**
     * Create an Aries-specific mutable instance of the standard RefMetadata interface.
     *
     * @param componentId the id of the referenced component
     *
     * @return a new reflect instance
     */
    public static MutableRefMetadata create(String componentId) {
        MutableRefMetadata metadata = create();

        metadata.setComponentId(componentId);

        return metadata;
    }

}
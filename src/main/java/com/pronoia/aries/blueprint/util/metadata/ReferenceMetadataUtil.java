package com.pronoia.aries.blueprint.util.metadata;

import org.apache.aries.blueprint.mutable.MutableReferenceMetadata;
import org.apache.aries.blueprint.reflect.MetadataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class providing helper-methods for createing Blueprint reflect.
 */
public class ReferenceMetadataUtil {
    static final Logger LOG = LoggerFactory.getLogger(ReferenceMetadataUtil.class);

    private ReferenceMetadataUtil() {
    }

    /**
     * Create an Aries-specific mutable instance of the standard ReferenceMetadata interface.
     *
     * @return a new reflect instance
     */
    public static MutableReferenceMetadata create() {
        MutableReferenceMetadata metadata = MetadataUtil.createMetadata(MutableReferenceMetadata.class);

        return metadata;
    }

    /**
     * Create an Aries-specific mutable instance of the standard ReferenceMetadata interface.
     *
     * @param serviceInterface the interface for the requested service
     *
     * @return a new reflect instance
     */
    public static MutableReferenceMetadata create(Class serviceInterface) {
        MutableReferenceMetadata metadata = create();

        metadata.setInterface(serviceInterface.getName());

        return metadata;
    }

    /**
     * Create an Aries-specific mutable instance of the standard ReferenceMetadata interface.
     *
     * @param serviceInterface the interface for the requested service
     * @param filter           the filter for the request service
     *
     * @return a new reflect instance
     */
    public static MutableReferenceMetadata create(Class serviceInterface, String filter) {
        MutableReferenceMetadata metadata = create(serviceInterface);

        if (filter != null && !filter.isEmpty()) {
            metadata.setFilter(filter);
        }

        return metadata;
    }
}
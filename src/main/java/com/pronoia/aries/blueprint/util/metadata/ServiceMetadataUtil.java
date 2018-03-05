package com.pronoia.aries.blueprint.util.metadata;

import java.util.Map;

import org.apache.aries.blueprint.mutable.MutableServiceMetadata;
import org.apache.aries.blueprint.reflect.MetadataUtil;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class providing helper-methods for createing Blueprint reflect.
 */
public class ServiceMetadataUtil {
    static final Logger LOG = LoggerFactory.getLogger(ServiceMetadataUtil.class);

    private ServiceMetadataUtil() {
    }

    /**
     * Create an Aries-specific mutable instance of the standard ServiceMetadata interface.
     *
     * @return a new reflect instance
     */
    public static MutableServiceMetadata create() {
        MutableServiceMetadata metadata = MetadataUtil.createMetadata(MutableServiceMetadata.class);

        metadata.setActivation(ComponentMetadata.ACTIVATION_LAZY);

        return metadata;
    }

    /**
     * Create an Aries-specific mutable instance of the standard ServiceMetadata interface.
     *
     * @param serviceInterfaces the interfaces for the service
     *
     * @return a new reflect instance
     */
    public static MutableServiceMetadata create(Class... serviceInterfaces) {
        MutableServiceMetadata metadata = create();

        if (serviceInterfaces != null && serviceInterfaces.length > 0) {
            for (Class serviceInterface : serviceInterfaces) {
                metadata.addInterface(serviceInterface.getName());
            }
        }

        return metadata;
    }

    /**
     * Create an Aries-specific mutable instance of the standard ServiceMetadata interface.
     *
     * @param serviceInterfaces the interfaces for the service
     *
     * @return a new reflect instance
     */
    public static MutableServiceMetadata create(String id, Class... serviceInterfaces) {
        MutableServiceMetadata metadata = create();
        metadata.setId(id);

        if (serviceInterfaces != null && serviceInterfaces.length > 0) {
            for (Class serviceInterface : serviceInterfaces) {
                metadata.addInterface(serviceInterface.getName());
            }
        }

        return metadata;
    }

    /**
     * Create an Aries-specific mutable instance of the standard ServiceMetadata interface.
     *
     * @param serviceMetadata      the MutableServiceMetadata for the service
     * @param serviceProperty      the service property to add
     * @param servicePropertyValue the service property value to add
     */
    public static void addServiceProperty(MutableServiceMetadata serviceMetadata, String serviceProperty, String servicePropertyValue) {
        if (serviceMetadata == null) {
            LOG.warn("Ignoring call to null addServiceProperties - MutableServiceMetadata argument is null");
        } else if (serviceProperty == null || serviceProperty.isEmpty()) {
            LOG.warn("Ignoring call to null addServiceProperties - service property name argument is null");
        } else if (servicePropertyValue == null || servicePropertyValue.isEmpty()) {
            LOG.warn("Ignoring call to null addServiceProperties - service property value argument is null");
        } else {
            serviceMetadata.addServiceProperty(ValueMetadataUtil.create(serviceProperty), ValueMetadataUtil.create(servicePropertyValue));
        }
    }

    /**
     * Create an Aries-specific mutable instance of the standard ServiceMetadata interface.
     *
     * @param serviceMetadata   the MutableServiceMetadata for the service
     * @param serviceProperties the service properties to add
     */
    public static void addServiceProperties(MutableServiceMetadata serviceMetadata, Map<String, String> serviceProperties) {
        for (Map.Entry<String, String> serviceProperty : serviceProperties.entrySet()) {
            serviceMetadata.addServiceProperty(ValueMetadataUtil.create(serviceProperty.getKey()), ValueMetadataUtil.create(serviceProperty.getValue()));
        }
    }
}
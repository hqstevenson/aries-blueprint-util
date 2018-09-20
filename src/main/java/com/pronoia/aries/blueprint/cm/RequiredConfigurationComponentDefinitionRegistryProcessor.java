package com.pronoia.aries.blueprint.cm;

import com.pronoia.aries.blueprint.cm.RequiredConfigurationListener;
import com.pronoia.aries.blueprint.cm.RequiredPersistentId;
import com.pronoia.aries.blueprint.util.reflect.ReferenceMetadataUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.aries.blueprint.ComponentDefinitionRegistry;
import org.apache.aries.blueprint.ComponentDefinitionRegistryProcessor;
import org.apache.aries.blueprint.mutable.MutableReferenceMetadata;
import org.apache.aries.blueprint.reflect.BeanMetadataImpl;
import org.osgi.service.blueprint.reflect.BeanProperty;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.Metadata;
import org.osgi.service.blueprint.reflect.ValueMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Blueprint component definition registry processor for the Aries-specific ComponentDefinitionRegistry.
 */
public class RequiredConfigurationComponentDefinitionRegistryProcessor implements ComponentDefinitionRegistryProcessor {
    static final AtomicInteger listenerCounter = new AtomicInteger(1);

    Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Process the Aries-specific Blueprint component definition registry, looking for property-placeholder elements.
     *
     * @param componentDefinitionRegistry the Aries Blueprint component definition registry
     */
    @Override
    public void process(ComponentDefinitionRegistry componentDefinitionRegistry) {
        log.info("Beginning processing of {}", ComponentDefinitionRegistry.class.getSimpleName());

        List<MutableReferenceMetadata> mutableReferenceMetadataList = new LinkedList<>();

        for (String definitionName : componentDefinitionRegistry.getComponentDefinitionNames()) {
            ComponentMetadata componentDefinition = componentDefinitionRegistry.getComponentDefinition(definitionName);
            log.debug("Processing definition of component '{}': {}", definitionName, componentDefinition);
            if (componentDefinition instanceof BeanMetadataImpl) {
                BeanMetadataImpl beanMetadata = (BeanMetadataImpl) componentDefinition;
                if (isPropertyPlaceholder(beanMetadata)) {
                    for (BeanProperty beanProperty : beanMetadata.getProperties()) {
                        if (beanProperty.getName().equals("persistentId")) {
                            Metadata beanPropertyValueMetadata = beanProperty.getValue();
                            if (beanPropertyValueMetadata instanceof ValueMetadata) {
                                final String referenceId = String.format("required-configuration-listener-%d", listenerCounter.getAndIncrement());
                                final String persistentId = ((ValueMetadata) beanPropertyValueMetadata).getStringValue();
                                final String filter = String.format("(%s=%s)", RequiredConfigurationListener.CONFIGURATION_SERVICE_PROPERTY, persistentId);

                                MutableReferenceMetadata mutableReferenceMetadata = ReferenceMetadataUtil.create(RequiredPersistentId.class, filter);

                                mutableReferenceMetadata.setId(referenceId);

                                mutableReferenceMetadataList.add(mutableReferenceMetadata);
                            }
                        }
                    }
                }
            }
        }

        for (MutableReferenceMetadata mutableReferenceMetadata : mutableReferenceMetadataList) {
            log.info("Registering reference '{}' with filter '{}'", mutableReferenceMetadata.getId(), mutableReferenceMetadata.getFilter());
            componentDefinitionRegistry.registerComponentDefinition(mutableReferenceMetadata);
        }
    }


    /**
     * Determine if the metadata is for a property placeholder.
     *
     * NOTE:  If the element has 'persistentId', placeholderPrefix and placeholderSuffix properties, it is assumed to be a property-placeholder element.  This detection mechanism could be improved.
     *
     * @param beanMetadata the metadata to inspect
     *
     * @return true if the metadata is for a property placeholder; false otherwise
     */
    boolean isPropertyPlaceholder(BeanMetadataImpl beanMetadata) {
        boolean hasPersistentId = false;
        boolean hasPlaceholderPrefix = false;
        boolean hasPlaceholderSuffix = false;

        for (BeanProperty beanProperty : beanMetadata.getProperties()) {
            switch (beanProperty.getName()) {
            case "persistentId":
                hasPersistentId = true;
                break;
            case "placeholderPrefix":
                hasPlaceholderPrefix = true;
                break;
            case "placeholderSuffix":
                hasPlaceholderSuffix = true;
                break;
            default:
                // Not an investigated property
                break;
            }
        }

        return (hasPersistentId && hasPlaceholderPrefix && hasPlaceholderSuffix);
    }
}

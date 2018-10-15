package com.pronoia.aries.blueprint.cm.internal.element;

import com.pronoia.aries.blueprint.cm.RequiredConfigurationListener;
import com.pronoia.aries.blueprint.cm.RequiredPersistentId;
import com.pronoia.aries.blueprint.cm.internal.metadata.RequiredConfigurationComponentDefinitionRegistryProcessorMetadata;
import com.pronoia.aries.blueprint.util.namespace.AbstractElementHandler;
import com.pronoia.aries.blueprint.util.namespace.AbstractNamespaceHandler;
import com.pronoia.aries.blueprint.util.parser.ElementParser;
import com.pronoia.aries.blueprint.util.reflect.ReferenceMetadataUtil;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.aries.blueprint.mutable.MutableReferenceMetadata;
import org.osgi.service.blueprint.reflect.Metadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RequiredPersistentIdElementHandler extends AbstractElementHandler {
    final AtomicInteger serviceReferenceCounter = new AtomicInteger(1);

    Logger log = LoggerFactory.getLogger(this.getClass());

    public RequiredPersistentIdElementHandler(AbstractNamespaceHandler namespaceHandler) {
        super(namespaceHandler, "required-persistent-id");
    }

    @Override
    public Metadata createMetadata(ElementParser handledElementParser) {
        Map<String, String> attributeValues = handledElementParser.getAttributeValueMap();

        if (attributeValues.containsKey("required-pid")) {
            final String requiredPid = attributeValues.get("required-pid");
            final String filter = String.format("(%s=%s)", RequiredConfigurationListener.CONFIGURATION_SERVICE_PROPERTY, requiredPid);

            MutableReferenceMetadata mutableReferenceMetadata = ReferenceMetadataUtil.create(RequiredPersistentId.class, filter);

            final String id = attributeValues.containsKey("id") ? attributeValues.get("id") : String.format("%s-%d", getElementName(), serviceReferenceCounter.getAndIncrement());
            mutableReferenceMetadata.setId(id);

            return mutableReferenceMetadata;
        } else {
            RequiredConfigurationComponentDefinitionRegistryProcessorMetadata metadata = new RequiredConfigurationComponentDefinitionRegistryProcessorMetadata();

            metadata.addProperties(attributeValues, true);

            return metadata;
        }
    }
}

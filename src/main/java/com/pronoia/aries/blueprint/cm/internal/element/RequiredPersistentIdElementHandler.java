package com.pronoia.aries.blueprint.cm.internal.element;

import com.pronoia.aries.blueprint.cm.internal.metadata.RequiredConfigurationComponentDefinitionRegistryProcessorMetadata;
import com.pronoia.aries.blueprint.util.namespace.AbstractElementHandler;
import com.pronoia.aries.blueprint.util.namespace.AbstractNamespaceHandler;
import com.pronoia.aries.blueprint.util.parser.ElementParser;

import java.util.Map;

import org.osgi.service.blueprint.reflect.Metadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RequiredPersistentIdElementHandler extends AbstractElementHandler {
    Logger log = LoggerFactory.getLogger(this.getClass());

    public RequiredPersistentIdElementHandler(AbstractNamespaceHandler namespaceHandler) {
        super(namespaceHandler, "required-persistent-id");
    }

    @Override
    public Metadata createMetadata(ElementParser handledElementParser) {
        RequiredConfigurationComponentDefinitionRegistryProcessorMetadata metadata = new RequiredConfigurationComponentDefinitionRegistryProcessorMetadata();

        Map<String, String> attributeValues = handledElementParser.getAttributeValueMap();
        metadata.addProperties(attributeValues, true);

        return metadata;
    }
}

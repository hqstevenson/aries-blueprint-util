package com.pronoia.aries.blueprint.cm.internal.element;

import com.pronoia.aries.blueprint.cm.RequiredConfigurationComponentDefinitionRegistryProcessor;
import com.pronoia.aries.blueprint.cm.internal.metadata.RequiredConfigurationListenerMetadata;
import com.pronoia.aries.blueprint.util.namespace.AbstractElementHandler;
import com.pronoia.aries.blueprint.util.namespace.AbstractNamespaceHandler;
import com.pronoia.aries.blueprint.util.parser.ElementParser;
import com.pronoia.aries.blueprint.util.reflect.SingletonBeanMetadataUtil;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.aries.blueprint.mutable.MutableBeanMetadata;
import org.osgi.service.blueprint.reflect.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RequiredConfigurationListenerElementHandler extends AbstractElementHandler {
    Logger log = LoggerFactory.getLogger(this.getClass());

    public RequiredConfigurationListenerElementHandler(AbstractNamespaceHandler namespaceHandler) {
        super(namespaceHandler, "required-configuration-listener");
    }

    @Override
    public Metadata createMetadata(ElementParser handledElementParser) {
        RequiredConfigurationListenerMetadata metadata = new RequiredConfigurationListenerMetadata();

        Map<String, String> attributeValues = handledElementParser.getAttributeValueMap();
        metadata.addProperties(attributeValues, true);

        List<ElementParser> whitelistElements = handledElementParser.getElements("whitelist-pattern");
        if (whitelistElements != null && !whitelistElements.isEmpty()) {
            List<String> whitelists = new LinkedList<>();
            for ( ElementParser whitelistElement : whitelistElements) {
                whitelists.add(whitelistElement.getValue());
            }
            metadata.setWhitelists(whitelists);
        }

        List<ElementParser> blacklistElements = handledElementParser.getElements("blacklist-pattern");
        if (blacklistElements != null && !blacklistElements.isEmpty()) {
            List<String> blacklists = new LinkedList<>();
            for ( ElementParser blacklistElement : blacklistElements) {
                blacklists.add(blacklistElement.getValue());
            }
            metadata.setBlacklists(blacklists);
        }

        return metadata;
    }
}

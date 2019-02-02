package com.pronoia.aries.blueprint.cm.internal.metadata;

import com.pronoia.aries.blueprint.cm.RequiredConfigurationComponentDefinitionRegistryProcessor;
import com.pronoia.aries.blueprint.util.metadata.AbstractBeanMetadata;
import com.pronoia.aries.blueprint.util.metadata.AbstractSingletonBeanMetadata;
import com.pronoia.aries.blueprint.util.reflect.ReferenceMetadataUtil;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.service.blueprint.reflect.Metadata;


public class RequiredConfigurationComponentDefinitionRegistryProcessorMetadata extends AbstractBeanMetadata {
    static AtomicInteger instanceCounter = new AtomicInteger(1);

    public RequiredConfigurationComponentDefinitionRegistryProcessorMetadata() {
        super(RequiredConfigurationComponentDefinitionRegistryProcessor.class);
        setId(String.format("required-persistent-%d", instanceCounter.getAndIncrement()));
        setProcessor(true);
    }

    @Override
    public String translatePropertyName(String name) {
        return null;
    }

    @Override
    public Metadata createPropertyMetadata(String propertyName, String propertyValue) {
        if (propertyName == null || propertyName.isEmpty()) {
            String message = String.format("createPropertyMetadata(propertyName[%s], propertyValue[%s]) - propertyName argument cannot be null or empty", propertyName, propertyValue);
            throw new IllegalArgumentException(message);
        }

        String message = String.format("createPropertyMetadata(propertyName[%s], propertyValue[%s]) - unsupported propertyName", propertyName, propertyValue);
        throw new IllegalArgumentException(message);
    }

    @Override
    public void addProperties(Map<String, String> properties, boolean logIgnoredProperties) {
        super.addProperties(properties, logIgnoredProperties);
    }
}

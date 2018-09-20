package com.pronoia.aries.blueprint.cm.internal.metadata;

import com.pronoia.aries.blueprint.cm.RequiredConfigurationListener;
import com.pronoia.aries.blueprint.util.metadata.AbstractSingletonBeanMetadata;
import com.pronoia.aries.blueprint.util.reflect.RefMetadataUtil;
import com.pronoia.aries.blueprint.util.reflect.SetMetadataUtil;
import com.pronoia.aries.blueprint.util.reflect.ValueMetadataUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.aries.blueprint.mutable.MutableBeanArgument;
import org.apache.aries.blueprint.reflect.BeanArgumentImpl;
import org.osgi.service.blueprint.reflect.Metadata;


public class RequiredConfigurationListenerMetadata extends AbstractSingletonBeanMetadata {
    static AtomicInteger instanceCounter = new AtomicInteger(1);

    public RequiredConfigurationListenerMetadata() {
        super(RequiredConfigurationListener.class);
        setActivation(ACTIVATION_EAGER);
        setInitMethod("initialize");
        setDestroyMethod("destroy");
        setId(String.format("required-configuration-processor-%d", instanceCounter.getAndIncrement()));

        MutableBeanArgument bundleContextArgument = new BeanArgumentImpl();
        bundleContextArgument.setIndex(0);
        bundleContextArgument.setValue(RefMetadataUtil.create("blueprintBundleContext"));

        this.addArgument(bundleContextArgument);
    }

    @Override
    public String translatePropertyName(String name) {
        return null;
    }

    @Override
    public Metadata createPropertyMetadata(String propertyName, String propertyValue) {
        throw null;
    }

    @Override
    public void addProperties(Map<String, String> properties, boolean logIgnoredProperties) {
        super.addProperties(properties, logIgnoredProperties);
        addProperty("requiredConfigurationListenerId", ValueMetadataUtil.create(getId()));
    }

    public void setWhitelists(List<String> whitelists) {
        if (whitelists != null && !whitelists.isEmpty()) {
            this.addProperty("persistentIdWhitelists", SetMetadataUtil.create(whitelists));
        }
    }

    public void setBlacklists(List<String> blacklists) {
        if (blacklists != null && !blacklists.isEmpty()) {
            this.addProperty("persistentIdBlacklists", SetMetadataUtil.create(blacklists));
        }
    }
}

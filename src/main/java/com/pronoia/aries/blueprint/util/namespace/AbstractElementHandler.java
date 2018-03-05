package com.pronoia.aries.blueprint.util.namespace;

import com.pronoia.aries.blueprint.ElementHandler;
import com.pronoia.aries.blueprint.util.metadata.BeanMetadataUtil;
import com.pronoia.aries.blueprint.util.metadata.RefMetadataUtil;

import org.apache.aries.blueprint.mutable.MutableBeanMetadata;

/**
 *
 */
public abstract class AbstractElementHandler implements ElementHandler {
    final String elementTagName;

    public AbstractElementHandler(String elementTagName) {
        if (elementTagName == null || elementTagName.isEmpty()) {
            throw new IllegalArgumentException(String.format("AbstractElementHandler(elementTagName[%s]) - the elementTagName argument cannot be null or empty", elementTagName));
        }

        this.elementTagName = elementTagName;
    }

    @Override
    public String getElementName() {
        return elementTagName;
    }

    protected void addBlueprintArguments(MutableBeanMetadata beanMetadata) {
        BeanMetadataUtil.addArgument(beanMetadata, RefMetadataUtil.create("blueprintBundleContext"), "org.osgi.framework.BundleContext", 0);
        BeanMetadataUtil.addArgument(beanMetadata, RefMetadataUtil.create("blueprintContainer"), "org.osgi.service.blueprint.container.BlueprintContainer", 1);
    }

}

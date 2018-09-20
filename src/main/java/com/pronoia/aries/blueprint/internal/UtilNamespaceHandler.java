package com.pronoia.aries.blueprint.internal;

import com.pronoia.aries.blueprint.cm.internal.element.RequiredConfigurationListenerElementHandler;
import com.pronoia.aries.blueprint.cm.internal.element.RequiredPersistentIdElementHandler;
import com.pronoia.aries.blueprint.util.namespace.AbstractNamespaceHandler;

public class UtilNamespaceHandler  extends AbstractNamespaceHandler {
    public UtilNamespaceHandler() {
        addElementHandler(new RequiredPersistentIdElementHandler(this));
        addElementHandler(new RequiredConfigurationListenerElementHandler(this));
    }

    @Override
    protected String getSchemaResourcePath() {
        return "/META-INF/schema/blueprint-util.xsd";
    }

    @Override
    protected String getSchema() {
        return "urn:pronoia.com/schema/blueprint/util";
    }
}

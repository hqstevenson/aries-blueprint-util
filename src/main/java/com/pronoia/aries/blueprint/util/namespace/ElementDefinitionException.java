package com.pronoia.aries.blueprint.util.namespace;

import org.osgi.service.blueprint.container.ComponentDefinitionException;
import org.w3c.dom.Element;


public class ElementDefinitionException extends ComponentDefinitionException {
    public ElementDefinitionException(String explanation) {
        super(explanation);
    }

    public ElementDefinitionException(String explanation, Throwable cause) {
        super(explanation, cause);
    }
}

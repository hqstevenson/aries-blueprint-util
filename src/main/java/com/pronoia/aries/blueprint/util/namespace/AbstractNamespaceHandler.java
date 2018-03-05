package com.pronoia.aries.blueprint.util.namespace;

import com.pronoia.aries.blueprint.ElementHandler;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.aries.blueprint.NamespaceHandler;
import org.apache.aries.blueprint.ParserContext;
import org.osgi.service.blueprint.container.ComponentDefinitionException;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.Metadata;
import org.slf4j.MDC;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 *
 */
public abstract class AbstractNamespaceHandler implements NamespaceHandler {
    public static final String MDC_NAMESPACE = "blueprint.namespace";
    public static final String MDC_DOCUMENT_URI = "blueprint.document";
    public static final String MDC_ELEMENT = "blueprint.element";

    Map<String, ElementHandler> elementHandlers = new HashMap<>();

    /**
     * Derived classes must provide a way to initialize the elementHandlers map - a constructor is a decent way.
     */
    protected AbstractNamespaceHandler() {
    }

    protected abstract String getSchemaResourcePath();


    protected abstract String getSchema();


    @Override
    public URL getSchemaLocation(String schema) {
        if (getSchema().equals(schema)) {
            return getClass().getResource(getSchemaResourcePath());
        }

        return null;
    }

    @Override
    public Set<Class> getManagedClasses() {
        return null;
    }

    @Override
    public Metadata parse(Element element, ParserContext parserContext) {
        try {
            String schema = getSchema();
            if (schema != null && !schema.isEmpty()) {
                MDC.put(MDC_NAMESPACE, schema);
            }

            if (element == null) {
                String errorMessage = String.format("Illegal use of Namespace Handler %s - cannot parse null org.w3c.dom.Element", this.getClass().getName());
                throw new ComponentDefinitionException(errorMessage);
            }

            Document ownerDocument = element.getOwnerDocument();
            if (ownerDocument != null) {
                String documentURI = ownerDocument.getDocumentURI();
                if (documentURI != null && !documentURI.isEmpty()) {
                    MDC.put(MDC_DOCUMENT_URI, documentURI);
                }
            }

            MDC.put(MDC_ELEMENT, element.getTagName());

            Metadata metadata = null;

            String elementName = element.getLocalName();

            if (elementHandlers == null) {
                String errorMessage = String.format("Illegal state of namespace handler <%s> {schema = '%s' element='%s'}  - null element handler collection",
                    this.getClass().getName(), getSchema(), elementName);
                throw new ComponentDefinitionException(errorMessage);
            } else if (elementHandlers.isEmpty()) {
                String errorMessage = String.format("Illegal state of namespace handler <%s> {schema = '%s' element='%s'} - empty element handler collection",
                    this.getClass().getName(), getSchema(), elementName);
                throw new ComponentDefinitionException(errorMessage);
            } else if (!elementHandlers.containsKey(elementName)) {
                String errorMessage = String.format("Illegal use of namespace handler <%s> {schema = '%s' element='%s'} - handler not configured for element %s",
                    this.getClass().getName(), getSchema(), elementName, element);
                throw new ComponentDefinitionException(errorMessage);
            } else {
                ElementHandler serviceElementHandler = elementHandlers.get(elementName);
                metadata = serviceElementHandler.parseElement(element, parserContext);
                if (metadata == null) {
                    String errorMessage = String.format("%s.parseElement(Element, ParserContext) returned null for namespace handler <%s> {schema = '%s' element='%s'} for element %s",
                        serviceElementHandler.getClass().getName(), this.getClass().getName(), getSchema(), elementName, element);
                    throw new ComponentDefinitionException(errorMessage);
                }
            }

            return metadata;
        } finally {
            MDC.remove(MDC_NAMESPACE);
            MDC.remove(MDC_DOCUMENT_URI);
            MDC.remove(MDC_ELEMENT);
        }
    }

    @Override
    public ComponentMetadata decorate(Node node, ComponentMetadata componentMetadata, ParserContext parserContext) {
        return null;
    }

    protected Map<String, ElementHandler> getElementHandlers() {
        if (elementHandlers == null) {
            elementHandlers = new HashMap<>();
        }

        return elementHandlers;
    }

    protected void addElementHandler(ElementHandler handler) {
        if (handler != null) {
            getElementHandlers().put(handler.getElementName(), handler);
        }
    }

}

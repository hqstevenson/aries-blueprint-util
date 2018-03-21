/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pronoia.aries.blueprint.util.namespace;

import com.pronoia.aries.blueprint.ElementHandler;
import com.pronoia.aries.blueprint.util.parser.ElementParser;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    Map<String, ElementHandler> elementHandlers = new HashMap<>();

    /**
     * Derived classes must provide a way to initialize the elementHandlers map - a constructor is a decent way.
     */
    protected AbstractNamespaceHandler() {
    }

    protected abstract String getSchemaResourcePath();


    protected abstract String getSchema();

    public Map<String, String> getDefaultAttributes() {
        return new LinkedHashMap<>();
    }

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
        try (MDCHelper helper = new MDCHelper(element) ) {
            if (element == null) {
                String errorMessage = String.format("Illegal use of Namespace Handler %s - cannot parse null org.w3c.dom.Element", this.getClass().getName());
                throw new ComponentDefinitionException(errorMessage);
            }

            Metadata metadata = null;

            String elementName = element.getTagName();

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

    static class MDCHelper implements AutoCloseable {
        public static final String MDC_NAMESPACE = "blueprint.namespace";
        public static final String MDC_DOCUMENT_URI = "blueprint.document";
        public static final String MDC_ELEMENT = "blueprint.element";

        Map<String, String> originalContextMap;

        public MDCHelper(Element element) {
            originalContextMap = MDC.getCopyOfContextMap();

            if (element != null) {
                String schema = element.getNamespaceURI();
                if (schema != null && !schema.isEmpty()) {
                    MDC.put(MDC_NAMESPACE, schema);
                }

                Document document = element.getOwnerDocument();
                if (document != null) {
                    String documentURI = document.getDocumentURI();
                    if (documentURI == null) {
                        documentURI = "null";
                    }
                    MDC.put(MDC_DOCUMENT_URI, documentURI);
                }

                MDC.put(MDC_ELEMENT, element.getTagName());
            }
        }

        @Override
        public void close() {
            if (originalContextMap != null) {
                MDC.setContextMap(originalContextMap);
            }
        }
    }

}

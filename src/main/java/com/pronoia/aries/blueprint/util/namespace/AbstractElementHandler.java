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
import com.pronoia.aries.blueprint.util.reflect.BeanMetadataUtil;
import com.pronoia.aries.blueprint.util.reflect.RefMetadataUtil;
import com.pronoia.aries.blueprint.util.parser.ElementParser;

import org.apache.aries.blueprint.ParserContext;
import org.apache.aries.blueprint.mutable.MutableBeanMetadata;
import org.osgi.service.blueprint.reflect.Metadata;
import org.w3c.dom.Element;


/**
 *
 */
public abstract class AbstractElementHandler implements ElementHandler {
    final AbstractNamespaceHandler namespaceHandler;
    final String elementTagName;

    public AbstractElementHandler(AbstractNamespaceHandler namespaceHandler, String elementTagName) {
        if (namespaceHandler == null) {
            throw new IllegalArgumentException(String.format("AbstractElementHandler(namespaceHandler, elementTagName[%s]) - the namespaceHandler argument cannot be null", elementTagName));
        }

        if (elementTagName == null || elementTagName.isEmpty()) {
            throw new IllegalArgumentException(String.format("AbstractElementHandler(namespaceHandler, elementTagName[%s]) - the elementTagName argument cannot be null or empty", elementTagName));
        }

        this.namespaceHandler = namespaceHandler;
        this.elementTagName = elementTagName;
    }

    public abstract Metadata createMetadata(ElementParser handledElementParser);

    @Override
    public Metadata parseElement(Element handledElement, ParserContext parserContext) {
        return createMetadata(new ElementParser(handledElement));
    }

    @Override
    public String getElementName() {
        return elementTagName;
    }

    protected boolean hasNamespaceHandler() {
        return namespaceHandler != null;
    }

    protected AbstractNamespaceHandler getNamespaceHandler() {
        return namespaceHandler;
    }

    protected void addBlueprintArguments(MutableBeanMetadata beanMetadata) {
        BeanMetadataUtil.addArgument(beanMetadata, RefMetadataUtil.create("blueprintBundleContext"), "org.osgi.framework.BundleContext", 0);
        BeanMetadataUtil.addArgument(beanMetadata, RefMetadataUtil.create("blueprintContainer"), "org.osgi.service.blueprint.container.BlueprintContainer", 1);
    }


}

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

import com.pronoia.aries.blueprint.util.reflect.PrototypeBeanMetadataUtil;
import com.pronoia.aries.blueprint.util.parser.ElementParser;

import org.apache.aries.blueprint.mutable.MutableBeanMetadata;
import org.junit.Before;
import org.junit.Test;
// import org.osgi.framework.BundleContext;
// import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.service.blueprint.reflect.Metadata;
import org.osgi.service.blueprint.reflect.RefMetadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 * Tests for the  class.
 */
public class AbstractElementHandlerConfigurationTest {
    AbstractElementHandler instance;

    @Before
    public void setUp() throws Exception {
        instance = new ElementHandlerStub();
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testConstructor() throws Exception {
        try {
            new ElementHandlerStub(null);
            fail("Constructor should have thrown an exception");
        } catch (IllegalArgumentException expectedEx) {
            assertEquals("AbstractElementHandler(namespaceHandler, elementTagName[null]) - the elementTagName argument cannot be null or empty", expectedEx.getMessage());
        }

        try {
            new ElementHandlerStub("");
            fail("Constructor should have thrown an exception");
        } catch (IllegalArgumentException expectedEx) {
            assertEquals("AbstractElementHandler(namespaceHandler, elementTagName[]) - the elementTagName argument cannot be null or empty", expectedEx.getMessage());
        }
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetElementName() throws Exception {
        assertEquals("Unexpected default value", ElementHandlerStub.ELEMENT_NAME, instance.getElementName());

    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testAddBlueprintArguments() throws Exception {
        MutableBeanMetadata metadata = PrototypeBeanMetadataUtil.create();

        instance.addBlueprintArguments(metadata);

        assertEquals("Unexpected index for argument 0", 0, metadata.getArguments().get(0).getIndex());
        assertEquals("Unexpected valueType for argument 0", "org.osgi.framework.BundleContext", metadata.getArguments().get(0).getValueType());
        assertEquals("Unexpected component id for argument 0", "blueprintBundleContext", ((RefMetadata) metadata.getArguments().get(0).getValue()).getComponentId());

        assertEquals("Unexpected index for argument 1", 1, metadata.getArguments().get(1).getIndex());
        assertEquals("Unexpected valueType for argument 1", "org.osgi.service.blueprint.container.BlueprintContainer", metadata.getArguments().get(1).getValueType());
        assertEquals("Unexpected component id for argument 0", "blueprintContainer", ((RefMetadata) metadata.getArguments().get(1).getValue()).getComponentId());
    }

    static class NamespaceHandlerStub extends AbstractNamespaceHandler {
        protected NamespaceHandlerStub() {
        }

        @Override
        protected String getSchemaResourcePath() {
            return "";
        }

        @Override
        protected String getSchema() {
            return "";
        }
    }

    static class ElementHandlerStub extends AbstractElementHandler {
        public static final String ELEMENT_NAME = "fake-element";

        public ElementHandlerStub() {
            super(new NamespaceHandlerStub(), ELEMENT_NAME);
        }

        public ElementHandlerStub(String elementName) {
            super(new NamespaceHandlerStub(), elementName);
        }

        @Override
        public Metadata createMetadata(ElementParser elementParser) {
            return null;
        }
    }

}
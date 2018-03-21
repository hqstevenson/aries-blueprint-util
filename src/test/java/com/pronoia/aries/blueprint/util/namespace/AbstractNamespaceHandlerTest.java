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
import com.pronoia.aries.blueprint.util.reflect.PrototypeBeanMetadataUtil;
import com.pronoia.aries.blueprint.util.parser.ElementParser;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.osgi.service.blueprint.container.ComponentDefinitionException;
import org.osgi.service.blueprint.reflect.Metadata;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * Tests for the  class.
 */
public class AbstractNamespaceHandlerTest {
    static final String TEST_SCHEMA = "uri:com.pronoia.test/schema/blueprint/testing";
    static final String TEST_SCHEMA_PATH = "/META-INF/schema/simple-handler.xsd";

    Element element;

    AbstractNamespaceHandler instance;

    @Before
    public void setUp() throws Exception {
        instance = new NamespaceHandlerStub();

        File fXmlFile = new File("src/test/resources/OSGI-INF/blueprint/simple-handler-blueprint.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
        doc.getDocumentElement().normalize();

        element = (Element) doc.getDocumentElement().getElementsByTagName("simple-handler").item(0);
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetSchemaLocation() throws Exception {
        URL expected = null;
        assertEquals(expected, instance.getSchemaLocation("uri:com.pronoia.test/schema/blueprint/unsupported-by-element"));

        File schemaFile = new File("target/test-classes" + TEST_SCHEMA_PATH);
        assertTrue("Schema must exist", schemaFile.exists());

        expected = schemaFile.toURI().toURL();
        assertEquals(expected, instance.getSchemaLocation(TEST_SCHEMA));
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetManagedClasses() throws Exception {
        assertNull("Unexpected value", instance.getManagedClasses());
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testParse() throws Exception {
        try {
            instance.parse(null, null);
            fail("Should have thrown an exception");
        } catch (ComponentDefinitionException expectedEx) {
            final String expectedMessage = "Illegal use of Namespace Handler com.pronoia.aries.blueprint.util.namespace.AbstractNamespaceHandlerTest$NamespaceHandlerStub"
                                           + " - cannot parse null org.w3c.dom.Element";
            assertEquals(expectedMessage, expectedEx.getMessage());
        }

        try {
            instance.parse(element, null);
            fail("Should have thrown an exception");
        } catch (ComponentDefinitionException expectedEx) {
            final String expectedMessage = "Illegal state of namespace handler <com.pronoia.aries.blueprint.util.namespace.AbstractNamespaceHandlerTest$NamespaceHandlerStub>"
                                           + " {schema = 'uri:com.pronoia.test/schema/blueprint/testing' element='simple-handler'} - empty element handler collection";
            assertEquals(expectedMessage, expectedEx.getMessage());
        }

        ElementHandlerStub elementHandlerStub = new ElementHandlerStub(instance, "simple-handler");
        instance.addElementHandler(elementHandlerStub);

        instance.parse(element, null);

        assertEquals(1, elementHandlerStub.parseCount);

        elementHandlerStub.answer = null;

        try {
            instance.parse(element, null);
            fail("Should have thrown an exception");
        } catch (ComponentDefinitionException expectedEx) {
            final String expectedMessage = "com.pronoia.aries.blueprint.util.namespace.AbstractNamespaceHandlerTest$ElementHandlerStub.parseElement(Element, ParserContext)"
                                           + " returned null for namespace handler <com.pronoia.aries.blueprint.util.namespace.AbstractNamespaceHandlerTest$NamespaceHandlerStub>"
                                           + " {schema = 'uri:com.pronoia.test/schema/blueprint/testing' element='simple-handler'} for element [simple-handler: null]";
            assertEquals(expectedMessage, expectedEx.getMessage());
        }

        instance.elementHandlers = null;

        try {
            instance.parse(element, null);
            fail("Should have thrown an exception");
        } catch (ComponentDefinitionException expectedEx) {
            final String expectedMessage = "Illegal state of namespace handler <com.pronoia.aries.blueprint.util.namespace.AbstractNamespaceHandlerTest$NamespaceHandlerStub>"
                                           + " {schema = 'uri:com.pronoia.test/schema/blueprint/testing' element='simple-handler'}  - null element handler collection";
            assertEquals(expectedMessage, expectedEx.getMessage());
        }

        instance.elementHandlers = new HashMap<>();
        instance.addElementHandler(new ElementHandlerStub(instance, "bad-handler-name"));

        try {
            instance.parse(element, null);
            fail("Should have thrown an exception");
        } catch (ComponentDefinitionException expectedEx) {
            final String expectedMessage = "Illegal use of namespace handler <com.pronoia.aries.blueprint.util.namespace.AbstractNamespaceHandlerTest$NamespaceHandlerStub>"
                                           + " {schema = 'uri:com.pronoia.test/schema/blueprint/testing' element='simple-handler'} - handler not configured for element [simple-handler: null]";
            assertEquals(expectedMessage, expectedEx.getMessage());
        }
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testDecorate() throws Exception {
        assertNull("Unexpected value", instance.decorate(null, null, null));
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetElementHandlers() throws Exception {
        Map<String, ElementHandler> expected = new HashMap<>();

        assertEquals(expected, instance.getElementHandlers());

        instance.elementHandlers = null;
        assertEquals(expected, instance.getElementHandlers());

        ElementHandler one = new ElementHandlerStub(instance, "element-one");
        ElementHandler two = new ElementHandlerStub(instance, "element-two");

        expected.put(one.getElementName(), one);
        expected.put(two.getElementName(), two);

        instance.addElementHandler(one);
        instance.addElementHandler(two);
        assertEquals("Unexpected default value", expected, instance.getElementHandlers());
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testAddElementHandler() throws Exception {
        final String elementName = "stub-element-for-add";

        ElementHandler stub = new ElementHandlerStub(instance, elementName);

        instance.addElementHandler(stub);

        assertEquals(1, instance.elementHandlers.size());
        assertTrue(instance.elementHandlers.containsKey(elementName));
        assertSame(stub, instance.elementHandlers.get(elementName));
    }


    static class NamespaceHandlerStub extends AbstractNamespaceHandler {
        protected NamespaceHandlerStub() {
        }

        @Override
        protected String getSchemaResourcePath() {
            return TEST_SCHEMA_PATH;
        }

        @Override
        protected String getSchema() {
            return TEST_SCHEMA;
        }
    }

    static class ElementHandlerStub extends AbstractElementHandler {
        int parseCount = 0;
        Metadata answer = PrototypeBeanMetadataUtil.create(String.class);

        public ElementHandlerStub(AbstractNamespaceHandler namespaceHandler, String elementName) {
            super(namespaceHandler, elementName);
        }

        @Override
        public Metadata createMetadata(ElementParser elementParser) {
            ++parseCount;
            return answer;
        }
    }
}
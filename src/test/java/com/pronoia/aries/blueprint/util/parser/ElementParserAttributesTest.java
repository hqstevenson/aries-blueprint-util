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
package com.pronoia.aries.blueprint.util.parser;

import com.pronoia.aries.blueprint.util.namespace.ElementDefinitionException;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Tests for the  class.
 */
public class ElementParserAttributesTest extends ElementParserTestSupport {
    ElementParser instance;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        instance = new ElementParser(handledElement);
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetAttributes() throws Exception {
        List<Attr> attributes = instance.getAttributes();
        assertEquals(5, attributes.size());

        assertEquals(0, instance.getElement("empty-sub-element").getAttributes().size());
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetAttributeMap() throws Exception {
        Map<String, Attr> attributeMap = instance.getAttributeMap();
        assertEquals(5, attributeMap.size());
        assertThat(attributeMap.keySet(), containsInAnyOrder("xmlns", "id", "string-handler-attribute", "short-handler-attribute", "empty-string-handler-attribute"));
        assertThat(attributeMap.get("short-handler-attribute"), instanceOf(Attr.class));
        assertEquals("5678", attributeMap.get("short-handler-attribute").getValue());

        attributeMap = instance.getElement("empty-sub-element").getAttributeMap();
        assertEquals(0, attributeMap.size());
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetAttributeValueMap() throws Exception {
        Map<String, String> attributeMap = instance.getAttributeValueMap();

        assertEquals(5, attributeMap.size());
        assertThat(attributeMap.keySet(), containsInAnyOrder("xmlns", "id", "string-handler-attribute", "short-handler-attribute", "empty-string-handler-attribute"));
        assertThat(attributeMap.get("short-handler-attribute"), instanceOf(String.class));
        assertEquals("5678", attributeMap.get("short-handler-attribute"));

        attributeMap = instance.getElement("empty-sub-element").getAttributeValueMap();
        assertEquals(0, attributeMap.size());
    }


    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetAttribute() throws Exception {
        try {
            instance.getAttribute(null);
            fail("Should fail");
        } catch (IllegalArgumentException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("getAttribute(attributeName[null]) - attributeName cannot be null or empty {element = 'simple-handler' document = '"));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

        try {
            instance.getAttribute("");
            fail("Should fail");
        } catch (IllegalArgumentException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("getAttribute(attributeName[]) - attributeName cannot be null or empty {element = 'simple-handler' document = '"));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetAttributeIllegalStateException() throws Exception {
        Element mockElement = mock(Element.class);

        when(mockElement.hasAttribute(anyString())).thenReturn(true);
        when(mockElement.getTagName()).thenReturn("mockio-element");
        when(mockElement.getAttributeNode(anyString())).thenReturn(null);

        ElementParser mockedElementParser = new ElementParser(mockElement);
        try {
            mockedElementParser.getAttribute("blah");
            fail("Should fail");
        } catch (IllegalStateException expectedEx) {
            assertEquals("getAttribute(attributeName[blah]) - Element.getAttributeNode(attributeName) failed after Element.hasAttribute(attributeName) returned true {element = 'mockio-element' document = 'null'}", expectedEx.getMessage());
        }

    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetBooleanAttribute() throws Exception {
        String attributeName = "boolean-sub-attribute";

        try {
            instance.getBooleanAttribute("non-existent-attribute", true);
            fail("Should fail for non-existent attribute");
        } catch (ElementDefinitionException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("Attribute 'non-existent-attribute' not found {element = 'simple-handler' document = '"));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

        assertNull(instance.getBooleanAttribute(attributeName, false));

        assertEquals(Boolean.valueOf(false), instance.getElement("sub-element").getBooleanAttribute(attributeName, true));
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetStringAttribute() throws Exception {
        assertEquals("my required handler attribute value", instance.getStringAttribute("string-handler-attribute", false));
        assertEquals("my required handler attribute value", instance.getStringAttribute("string-handler-attribute", true));


        try {
            instance.getStringAttribute("non-existent-attribute", true);
            fail("Should fail for non-existent attribute");
        } catch (ElementDefinitionException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("Attribute 'non-existent-attribute' not found {element = 'simple-handler' document = '"));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

        ElementParser subElement = instance.getElement("sub-element");
        assertEquals("", subElement.getStringAttribute("empty-string-sub-attribute", false));

        assertEquals("", subElement.getStringAttribute("empty-string-sub-attribute", true));

        try {
            assertEquals("", subElement.getStringAttribute("non-existent-attribute", true));
            fail("Should fail for a missing attribute value");
        } catch (ElementDefinitionException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("Attribute 'non-existent-attribute' not found {element = 'sub-element' document = '"));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetByteAttribute() throws Exception {
        String attributeName = "short-sub-attribute";
        try {
            instance.getByteAttribute("non-existent-attribute", true);
            fail("Should fail for non-existent attribute");
        } catch (ElementDefinitionException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("Attribute 'non-existent-attribute' not found {element = 'simple-handler' document = '"));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

        ElementParser subElement = instance.getElement("sub-element");
        assertEquals(Byte.valueOf("123"), subElement.getByteAttribute(attributeName, true));
        assertEquals(Byte.valueOf("123"), subElement.getByteAttribute(attributeName, false));

        assertNull(instance.getByteAttribute(attributeName, false));

        try {
            assertNull(instance.getByteAttribute("string-handler-attribute", true));
            fail("Should fail for un-convertible attribute value");
        } catch (ElementDefinitionException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("Failed to convert 'string-handler-attribute' attribute value 'my required handler attribute value' to class java.lang.Byte {element = 'simple-handler' document ="));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

        try {
            assertNull(instance.getByteAttribute("string-handler-attribute", false));
            fail("Should fail for un-convertible attribute value");
        } catch (ElementDefinitionException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("Failed to convert 'string-handler-attribute' attribute value 'my required handler attribute value' to class java.lang.Byte {element = 'simple-handler' document ="));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetShortAttribute() throws Exception {
        assertNull(instance.getShortAttribute("non-existent-attribute", false));

        try {
            instance.getShortAttribute("non-existent-attribute", true);
            fail("Should fail for non-existent attribute");
        } catch (ElementDefinitionException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("Attribute 'non-existent-attribute' not found {element = 'simple-handler' document = '"));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

        assertEquals(Short.valueOf("5678"), instance.getShortAttribute("short-handler-attribute", true));
        assertEquals(Short.valueOf("5678"), instance.getShortAttribute("short-handler-attribute", false));

        try {
            instance.getShortAttribute("string-handler-attribute", true);
            fail("Should fail for un-convertible attribute value");
        } catch (ElementDefinitionException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("Failed to convert 'string-handler-attribute' attribute value 'my required handler attribute value' to class java.lang.Short {element = 'simple-handler' document ="));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

        try {
            instance.getShortAttribute("string-handler-attribute", false);
            fail("Should fail for un-convertible attribute value");
        } catch (ElementDefinitionException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("Failed to convert 'string-handler-attribute' attribute value 'my required handler attribute value' to class java.lang.Short {element = 'simple-handler' document ="));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetIntegerAttribute() throws Exception {
        String attributeName = "integer-sub-attribute";

        assertNull(instance.getIntegerAttribute(attributeName, false));

        try {
            instance.getIntegerAttribute("non-existent-attribute", true);
            fail("Should fail for non-existent attribute");
        } catch (ElementDefinitionException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("Attribute 'non-existent-attribute' not found {element = 'simple-handler' document = '"));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

        ElementParser subElement = instance.getElement("sub-element");
        assertEquals(Integer.valueOf(1234567890), subElement.getIntegerAttribute(attributeName, true));

        try {
            instance.getIntegerAttribute("string-handler-attribute", true);
            fail("Should fail for un-convertible attribute value");
        } catch (ElementDefinitionException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("Failed to convert 'string-handler-attribute' attribute value 'my required handler attribute value' to class java.lang.Integer {element = 'simple-handler' document ="));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

        try {
            instance.getIntegerAttribute("string-handler-attribute", false);
            fail("Should fail for un-convertible attribute value");
        } catch (ElementDefinitionException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("Failed to convert 'string-handler-attribute' attribute value 'my required handler attribute value' to class java.lang.Integer {element = 'simple-handler' document ="));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetLongAttribute() throws Exception {
        String attributeName = "long-sub-attribute";

        assertNull(instance.getLongAttribute(attributeName, false));

        try {
            instance.getLongAttribute("non-existent-attribute", true);
            fail("Should fail for non-existent attribute");
        } catch (ElementDefinitionException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("Attribute 'non-existent-attribute' not found {element = 'simple-handler' document = '"));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

        ElementParser subElement = instance.getElement("sub-element");
        assertEquals(Long.valueOf(9876543210L), subElement.getLongAttribute(attributeName, true));
        assertEquals(Long.valueOf(9876543210L), subElement.getLongAttribute(attributeName, false));

        try {
            instance.getLongAttribute("string-handler-attribute", true);
            fail("Should fail for un-convertible attribute value");
        } catch (ElementDefinitionException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("Failed to convert 'string-handler-attribute' attribute value 'my required handler attribute value' to class java.lang.Long {element = 'simple-handler' document ="));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

        try {
            instance.getLongAttribute("string-handler-attribute", false);
            fail("Should fail for un-convertible attribute value");
        } catch (ElementDefinitionException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("Failed to convert 'string-handler-attribute' attribute value 'my required handler attribute value' to class java.lang.Long {element = 'simple-handler' document ="));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetFloatAttribute() throws Exception {
        String attributeName = "float-sub-attribute";

        assertNull(instance.getFloatAttribute(attributeName, false));

        try {
            instance.getFloatAttribute("non-existent-attribute", true);
            fail("Should fail for non-existent attribute");
        } catch (ElementDefinitionException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("Attribute 'non-existent-attribute' not found {element = 'simple-handler' document = '"));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

        ElementParser subElement = instance.getElement("sub-element");
        assertEquals(Float.valueOf(1.234f), subElement.getFloatAttribute(attributeName, true));
        assertEquals(Float.valueOf(1.234f), subElement.getFloatAttribute(attributeName, false));

        try {
            instance.getFloatAttribute("string-handler-attribute", true);
            fail("Should fail for un-convertible attribute value");
        } catch (ElementDefinitionException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("Failed to convert 'string-handler-attribute' attribute value 'my required handler attribute value' to class java.lang.Float {element = 'simple-handler' document ="));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

        try {
            instance.getFloatAttribute("string-handler-attribute", false);
            fail("Should fail for un-convertible attribute value");
        } catch (ElementDefinitionException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("Failed to convert 'string-handler-attribute' attribute value 'my required handler attribute value' to class java.lang.Float {element = 'simple-handler' document ="));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetDoubleAttribute() throws Exception {
        String attributeName = "double-sub-attribute";

        assertNull(instance.getDoubleAttribute(attributeName, false));

        try {
            instance.getDoubleAttribute("non-existent-attribute", true);
            fail("Should fail for non-existent attribute");
        } catch (ElementDefinitionException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("Attribute 'non-existent-attribute' not found {element = 'simple-handler' document = '"));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

        ElementParser subElement = instance.getElement("sub-element");
        assertEquals(Double.valueOf(5.6789), subElement.getDoubleAttribute(attributeName, true));
        assertEquals(Double.valueOf(5.6789), subElement.getDoubleAttribute(attributeName, false));

        try {
            instance.getDoubleAttribute("string-handler-attribute", true);
            fail("Should fail for un-convertible attribute value");
        } catch (ElementDefinitionException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("Failed to convert 'string-handler-attribute' attribute value 'my required handler attribute value' to class java.lang.Double {element = 'simple-handler' document ="));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

        try {
            instance.getDoubleAttribute("string-handler-attribute", false);
            fail("Should fail for un-convertible attribute value");
        } catch (ElementDefinitionException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("Failed to convert 'string-handler-attribute' attribute value 'my required handler attribute value' to class java.lang.Double {element = 'simple-handler' document ="));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }
    }

}
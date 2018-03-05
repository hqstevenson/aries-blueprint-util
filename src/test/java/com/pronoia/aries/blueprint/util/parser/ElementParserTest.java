package com.pronoia.aries.blueprint.util.parser;

import com.pronoia.aries.blueprint.util.namespace.ElementDefinitionException;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.osgi.service.blueprint.container.ComponentDefinitionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;


/**
 * Tests for the  class.
 */
public class ElementParserTest {
    static final String HANDLER_ELEMENT = "simple-handler";
    static final String CHILD_ELEMENT =    "sub-element-with-value";

    Element handledElement;

    ElementParser instance;

    @Before
    public void setUp() throws Exception {

        File fXmlFile = new File("src/test/resources/OSGI-INF/blueprint/simple-handler-blueprint.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
        doc.getDocumentElement().normalize();

        handledElement = (Element) doc.getDocumentElement().getElementsByTagName(HANDLER_ELEMENT).item(0);

        instance = new ElementParser(handledElement);

        assertEquals(HANDLER_ELEMENT, instance.getName());
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetElement() throws Exception {

        ElementParser actual = instance.getElement(CHILD_ELEMENT);

        assertEquals(CHILD_ELEMENT, actual.getName());

        actual = instance.getElement(CHILD_ELEMENT, 1);

        assertEquals(CHILD_ELEMENT, actual.getName());

        try {
            instance.getElement("bogus-element");
        } catch (ElementDefinitionException expectedEx) {
            // TODO: check exception message
        }

        try {
            instance.getElement(CHILD_ELEMENT, 9999);
        } catch (IllegalArgumentException expectedEx) {
            // TODO: check exception message
        }
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetValue() throws Exception {
        assertEquals("\n        ", instance.getValue());

        ElementParser actual = instance.getElement(CHILD_ELEMENT);


        assertEquals("Sub-Element Value", actual.getValue());

        actual = instance.getElement(CHILD_ELEMENT, 1);

        assertEquals("Sub-Element Value 2", actual.getValue());

        actual = instance.getElement("empty-sub-element");
        assertEquals(null, actual.getValue());

        actual = instance.getElement("empty-sub-element", 1);
        assertEquals(null, actual.getValue());
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
            instance.getBooleanAttribute(attributeName, true);
            fail("Should fail for non-existent attribute");
        } catch (ElementDefinitionException expectedEx) {
            // TODO: Verify exception message
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
            instance.getStringAttribute("non-existent-attribute-name", true);
            fail("Should fail for non-existent attribute");
        } catch (ElementDefinitionException expectedEx) {
            // TODO: Verify exception message
        }

        ElementParser subElement = instance.getElement("sub-element");
        assertEquals("", subElement.getStringAttribute("empty-string-sub-attribute", false));

        try {
            assertEquals("", subElement.getStringAttribute("empty-string-sub-attribute", true));
            fail("Should fail for empty attribute value");
        } catch (ElementDefinitionException expectedEx) {
            // TODO: Verify exception message
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
            instance.getByteAttribute(attributeName, true);
            fail("Should fail for non-existent attribute");
        } catch (ElementDefinitionException expectedEx) {
            // TODO: Verify exception message
        }

        ElementParser subElement = instance.getElement("sub-element");
        assertEquals(Byte.valueOf("123"), subElement.getByteAttribute(attributeName, true));
        assertEquals(Byte.valueOf("123"), subElement.getByteAttribute(attributeName, false));

        assertNull(instance.getByteAttribute(attributeName, false));

        try {
            assertNull(instance.getByteAttribute("string-handler-attribute", true));
            fail("Should fail for un-convertible attribute value");
        } catch (ElementDefinitionException expectedEx) {
            // TODO: Verify exception message
        }

        try {
            assertNull(instance.getByteAttribute("string-handler-attribute", false));
            fail("Should fail for un-convertible attribute value");
        } catch (ElementDefinitionException expectedEx) {
            // TODO: Verify exception message
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
            // TODO: Verify exception message
        }

        assertEquals(Short.valueOf("5678"), instance.getShortAttribute("short-handler-attribute", true));
        assertEquals(Short.valueOf("5678"), instance.getShortAttribute("short-handler-attribute", false));

        try {
            instance.getShortAttribute("string-handler-attribute", true);
            fail("Should fail for un-convertible attribute value");
        } catch (ElementDefinitionException expectedEx) {
            // TODO: Verify exception message
        }

        try {
            instance.getShortAttribute("string-handler-attribute", false);
            fail("Should fail for un-convertible attribute value");
        } catch (ElementDefinitionException expectedEx) {
            // TODO: Verify exception message
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
            instance.getIntegerAttribute(attributeName, true);
            fail("Should fail for non-existent attribute");
        } catch (ElementDefinitionException expectedEx) {
            // TODO: Verify exception message
        }

        ElementParser subElement = instance.getElement("sub-element");
        assertEquals(Integer.valueOf(1234567890), subElement.getIntegerAttribute(attributeName, true));

        try {
            instance.getIntegerAttribute("string-handler-attribute", true);
            fail("Should fail for un-convertible attribute value");
        } catch (ElementDefinitionException expectedEx) {
            // TODO: Verify exception message
        }

        try {
            instance.getIntegerAttribute("string-handler-attribute", false);
            fail("Should fail for un-convertible attribute value");
        } catch (ElementDefinitionException expectedEx) {
            // TODO: Verify exception message
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
            instance.getLongAttribute(attributeName, true);
            fail("Should fail for non-existent attribute");
        } catch (ElementDefinitionException expectedEx) {
            // TODO: Verify exception message
        }

        ElementParser subElement = instance.getElement("sub-element");
        assertEquals(Long.valueOf(9876543210L), subElement.getLongAttribute(attributeName, true));
        assertEquals(Long.valueOf(9876543210L), subElement.getLongAttribute(attributeName, false));

        try {
            instance.getLongAttribute("string-handler-attribute", true);
            fail("Should fail for un-convertible attribute value");
        } catch (ElementDefinitionException expectedEx) {
            // TODO: Verify exception message
        }

        try {
            instance.getLongAttribute("string-handler-attribute", false);
            fail("Should fail for un-convertible attribute value");
        } catch (ElementDefinitionException expectedEx) {
            // TODO: Verify exception message
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
            instance.getFloatAttribute(attributeName, true);
            fail("Should fail for non-existent attribute");
        } catch (ElementDefinitionException expectedEx) {
            // TODO: Verify exception message
        }

        ElementParser subElement = instance.getElement("sub-element");
        assertEquals(Float.valueOf(1.234f), subElement.getFloatAttribute(attributeName, true));
        assertEquals(Float.valueOf(1.234f), subElement.getFloatAttribute(attributeName, false));

        try {
            instance.getFloatAttribute("string-handler-attribute", true);
            fail("Should fail for un-convertible attribute value");
        } catch (ElementDefinitionException expectedEx) {
            // TODO: Verify exception message
        }

        try {
            instance.getFloatAttribute("string-handler-attribute", false);
            fail("Should fail for un-convertible attribute value");
        } catch (ElementDefinitionException expectedEx) {
            // TODO: Verify exception message
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
            instance.getDoubleAttribute(attributeName, true);
            fail("Should fail for non-existent attribute");
        } catch (ElementDefinitionException expectedEx) {
            // TODO: Verify exception message
        }

        ElementParser subElement = instance.getElement("sub-element");
        assertEquals(Double.valueOf(5.6789), subElement.getDoubleAttribute(attributeName, true));
        assertEquals(Double.valueOf(5.6789), subElement.getDoubleAttribute(attributeName, false));

        try {
            instance.getDoubleAttribute("string-handler-attribute", true);
            fail("Should fail for un-convertible attribute value");
        } catch (ElementDefinitionException expectedEx) {
            // TODO: Verify exception message
        }

        try {
            instance.getDoubleAttribute("string-handler-attribute", false);
            fail("Should fail for un-convertible attribute value");
        } catch (ElementDefinitionException expectedEx) {
            // TODO: Verify exception message
        }
    }

}
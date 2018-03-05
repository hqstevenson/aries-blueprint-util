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

import static com.pronoia.aries.blueprint.util.namespace.NamespaceHandlerUtil.getAttribute;
import static com.pronoia.aries.blueprint.util.namespace.NamespaceHandlerUtil.getAttributeMap;
import static com.pronoia.aries.blueprint.util.namespace.NamespaceHandlerUtil.getAttributeValue;
import static com.pronoia.aries.blueprint.util.namespace.NamespaceHandlerUtil.getAttributeValueMap;
import static com.pronoia.aries.blueprint.util.namespace.NamespaceHandlerUtil.getAttributes;
import static com.pronoia.aries.blueprint.util.namespace.NamespaceHandlerUtil.getChildElementMap;
import static com.pronoia.aries.blueprint.util.namespace.NamespaceHandlerUtil.getChildElementValues;
import static com.pronoia.aries.blueprint.util.namespace.NamespaceHandlerUtil.getChildElements;

import static com.pronoia.aries.blueprint.util.namespace.NamespaceHandlerUtil.getElementValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

// TODO:  Test for getElementValue with descendants with content
/**
 * Tests for the  class.
 */
public class NamespaceHandlerUtilTest {
    static final String HANDLER_ELEMENT = "simple-handler";
    static final String SUB_ELEMENT = "sub-element";
    static final String SUB_ELEMENT_WITH_VALUE = "sub-element-with-value";
    static final String EMPTY_SUB_ELEMENT = "empty-sub-element";

    Element handledElement;
    Element sub;
    Element subWithValue;
    Element emptySub;

    @Before
    public void setUp() throws Exception {
        File fXmlFile = new File("src/test/resources/OSGI-INF/blueprint/simple-handler-blueprint.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
        doc.getDocumentElement().normalize();

        handledElement = (Element) doc.getDocumentElement().getElementsByTagName(HANDLER_ELEMENT).item(0);
        sub = (Element) handledElement.getElementsByTagName(SUB_ELEMENT).item(0);
        subWithValue = (Element) handledElement.getElementsByTagName(SUB_ELEMENT_WITH_VALUE).item(0);
        emptySub = (Element) handledElement.getElementsByTagName(EMPTY_SUB_ELEMENT).item(0);
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetChildElement() throws Exception {
        String elementName = "sub-element-with-value";

        List<Element> childElements = getChildElements(handledElement, elementName, true);

        Element childElement = childElements.get(0);

        assertEquals(elementName, childElement.getTagName());
        assertEquals("Sub-Element Value", childElement.getTextContent());

        childElement = childElements.get(1);

        assertEquals(elementName, childElement.getTagName());
        assertEquals("Sub-Element Value 2", childElement.getTextContent());

        assertFalse(getChildElementValues(handledElement, elementName, false).isEmpty());
        assertTrue(getChildElementValues(handledElement, "bogus-" + elementName, false).isEmpty());
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetChildElements() throws Exception {
        List<Element> childElements = getChildElements(handledElement, true);

        assertEquals(5, childElements.size());
        assertEquals("Sub-Element Value", childElements.get(1).getTextContent());

        childElements = getChildElements(handledElement, "sub-element-with-value", true);

        assertEquals(2, childElements.size());
        assertEquals("Sub-Element Value 2", childElements.get(1).getTextContent());

        childElements = getChildElements(sub, false);
        assertEquals(0, childElements.size());
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetChildElementMap() throws Exception {
        Map<String, List<Element>> childElements = getChildElementMap(handledElement,true);

        assertEquals(3, childElements.size());
        assertEquals(1, childElements.get("sub-element").size());
        assertEquals(2, childElements.get("sub-element-with-value").size());
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetElementValue() throws Exception {
        String expected = "Sub-Element Value";
        String actual = getElementValue(subWithValue,true);
        assertEquals(expected, actual);

        // The SUB_ELEMENT doesn't have a value
        try {
            getElementValue(sub, true);
            fail("Should fail for empty element");
        } catch (ElementDefinitionException expectedEx) {
            // TODO: verify exception message
        }

        assertEquals(null, getElementValue(sub, false));

        try {
            assertNull(getElementValue(null, false));
            fail("Should have thrown an exception");
        } catch (IllegalArgumentException expectedEx) {
            // TODO: verify exception message
        }
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetChildElementValue() throws Exception {
        String expected = "Sub-Element Value";
        String actual = getChildElementValues(handledElement, SUB_ELEMENT_WITH_VALUE, true).get(0);
        assertEquals(expected, actual);

        try {
            assertEquals("", getElementValue(null, true));
            fail("Should fail for empty element");
        } catch (IllegalArgumentException expectedEx) {
            // TODO: verify exception message
        }

        try {
            assertEquals("", getElementValue(null, false));
            fail("Should fail for empty element");
        } catch (IllegalArgumentException expectedEx) {
            // TODO: verify exception message
        }

        try {
            assertEquals("", getElementValue(emptySub, true));
            fail("Should fail for empty element");
        } catch (ElementDefinitionException expectedEx) {
            // TODO: verify exception message
        }

        assertEquals(null, getElementValue(emptySub, false));
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetChildElementValueWithEmptyElement() throws Exception {
        try {
            getChildElementValues(handledElement, SUB_ELEMENT, true);
            fail("Should fail for empty element");
        } catch (ElementDefinitionException expectedEx) {
            // TODO: verify exception message
        }

        assertEquals(1, getChildElementValues(handledElement, SUB_ELEMENT, false).size());
        assertEquals(null, getChildElementValues(handledElement, SUB_ELEMENT, false).get(0));
    }


    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetChildElementValueWithNonExistentElement() throws Exception {
        final String nonexistentElementName = "nonexistent-" + SUB_ELEMENT;
        try {
            getChildElementValues(handledElement, nonexistentElementName, true);
            fail("Should fail for empty element");
        } catch (ElementDefinitionException expectedEx) {
            // TODO: verify exception message
        }

        assertEquals(0, getChildElementValues(handledElement, nonexistentElementName, false).size());
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetChildElementValueWithNullElementParameter() throws Exception {
        try {
            getChildElementValues(null, SUB_ELEMENT_WITH_VALUE, true);
            fail("Should fail for empty element");
        } catch (IllegalArgumentException expectedEx) {
            // TODO: verify exception message
        }

        try {
            getChildElementValues(null, SUB_ELEMENT_WITH_VALUE, false);
            fail("Should fail for empty element");
        } catch (IllegalArgumentException expectedEx) {
            // TODO: verify exception message
        }
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetChildElementValueWithNullElementNameParameter() throws Exception {
        try {
            getChildElementValues(handledElement, null, true);
            fail("Should fail for empty element");
        } catch (IllegalArgumentException expectedEx) {
            // TODO: verify exception message
        }
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetChildElementValueWithEmptyElementNameParameter() throws Exception {
        try {
            getChildElementValues(handledElement, "", true);
            fail("Should fail for empty element");
        } catch (IllegalArgumentException expectedEx) {
            // TODO: verify exception message
        }
    }



    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetAttribute() throws Exception {
        String attributeName = "string-handler-attribute";

        Attr attribute = getAttribute(handledElement, attributeName, false);

        assertEquals(attributeName, attribute.getName());
        assertEquals("my required handler attribute value", attribute.getValue());

        assertNull("my required handler attribute value", getAttribute(handledElement, "bogus-" + attributeName, false));
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetAttributeValue() throws Exception {
        String attributeName = "string-handler-attribute";

        assertEquals("my required handler attribute value", getAttributeValue(handledElement, attributeName, false));

        assertNull("my required handler attribute value", getAttributeValue(handledElement, "bogus-" + attributeName, false));
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetAttributes() throws Exception {
        List<Attr> attributes = getAttributes(handledElement, true);

        assertEquals(3, attributes.size());
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetAttributeMap() throws Exception {
        Map<String, Attr> attributeMap = getAttributeMap(handledElement, true);

        assertEquals(3, attributeMap.size());
        assertThat(attributeMap.keySet(), containsInAnyOrder("xmlns", "string-handler-attribute", "short-handler-attribute"));
        assertThat(attributeMap.get("short-handler-attribute"), instanceOf(Attr.class));
        assertEquals("5678", attributeMap.get("short-handler-attribute").getValue());
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetAttributeValueMap() throws Exception {
        Map<String, String> attributeMap = getAttributeValueMap(handledElement, true);

        assertEquals(3, attributeMap.size());
        assertThat(attributeMap.keySet(), containsInAnyOrder("xmlns", "string-handler-attribute", "short-handler-attribute"));
        assertThat(attributeMap.get("short-handler-attribute"), instanceOf(String.class));
        assertEquals("5678", attributeMap.get("short-handler-attribute"));
    }

}
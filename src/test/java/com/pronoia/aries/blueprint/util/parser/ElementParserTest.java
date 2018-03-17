package com.pronoia.aries.blueprint.util.parser;

import com.pronoia.aries.blueprint.util.namespace.ElementDefinitionException;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.osgi.service.blueprint.container.ComponentDefinitionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static com.pronoia.aries.blueprint.util.namespace.NamespaceHandlerUtil.getChildElementValues;
import static com.pronoia.aries.blueprint.util.namespace.NamespaceHandlerUtil.getChildElements;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * Tests for the  class.
 */
public class ElementParserTest extends ElementParserTestSupport {
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
    public void testGetValue() throws Exception {
        assertEquals("\n        ", instance.getValue());

        ElementParser actual = instance.getElement("sub-element-with-value");

        assertEquals("Sub-Element Value", actual.getValue());

        actual = instance.getElement("sub-element-with-value", 1);

        assertEquals("Sub-Element Value 2", actual.getValue());

        actual = instance.getElement("empty-sub-element");
        assertEquals(null, actual.getValue());

        actual = instance.getElement("empty-sub-element", 1);
        assertEquals(null, actual.getValue());

        assertEquals(null, actual.getValue(false));

        try {
            actual.getValue(true);
        } catch (ElementDefinitionException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("Element 'empty-sub-element' text content is null or empty in document "));
        }
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetElementWithInvalidArguments() throws Exception {

        try {
            instance.getElement(null);
            fail("Should have failed");
        } catch (IllegalArgumentException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("getElement(tagName[null], occurrence[0]) - tagName cannot be null or empty {element = 'simple-handler' document = '"));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

        try {
            instance.getElement("");
            fail("Should have failed");
        } catch (IllegalArgumentException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("getElement(tagName[], occurrence[0]) - tagName cannot be null or empty {element = 'simple-handler' document = '"));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

        try {
            instance.getElement("sub-element", -1);
            fail("Should have failed");
        } catch (IllegalArgumentException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("getElement(tagName[sub-element], occurrence[-1]) - occurrence cannot be less than zero {element = 'simple-handler' document = '"));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetElement() throws Exception {
        String tagName = "sub-element-with-value";

        ElementParser actual = instance.getElement(tagName);

        assertEquals(tagName, actual.getTagName());

        actual = instance.getElement(tagName, 1);

        assertEquals(tagName, actual.getTagName());

        assertNull(instance.getElement("bogus-element",false));

        try {
            instance.getElement("bogus-element",true);
            fail("Should have failed");
        } catch (ElementDefinitionException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("Descendant 'bogus-element' element not found {element = 'simple-handler' document = '"));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml' }"));
        }

        try {
            instance.getElement(tagName, 9999, true);
            fail("Should have failed");
        } catch (ElementDefinitionException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("Descendant 'sub-element-with-value' element occurrence [9999] not found {element = 'simple-handler' document = '"));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml' }"));
        }
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetElements() throws Exception {
        List<ElementParser> childElements = instance.getElements();

        assertEquals(6, childElements.size());
        assertEquals("Sub-Element Value", childElements.get(1).getValue());

        ElementParser sub = childElements.get(0);

        childElements = sub.getElements();
        assertEquals(0, childElements.size());
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetElementsWithTagName() throws Exception {
        List<ElementParser> childElements = instance.getElements("sub-element-with-value");

        assertEquals(2, childElements.size());
        assertEquals("Sub-Element Value 2", childElements.get(1).getValue());


        childElements = instance.getElements("non-existent-element");
        assertEquals(0, childElements.size());
    }


    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetElementMap() throws Exception {
        Map<String, List<ElementParser>> childElements = instance.getElementMap();

        assertEquals(4, childElements.size());
        assertEquals(1, childElements.get("sub-element").size());
        assertEquals(2, childElements.get("sub-element-with-value").size());

        ElementParser childElementParser = childElements.get("sub-element").get(0);
        assertTrue(childElementParser.getElementMap().isEmpty());
    }

}
package com.pronoia.aries.blueprint.util.parser;

import com.pronoia.aries.blueprint.util.namespace.ElementDefinitionException;
import com.pronoia.aries.blueprint.util.namespace.NamespaceHandlerUtil;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static com.pronoia.aries.blueprint.util.namespace.NamespaceHandlerUtil.getAttributeValue;
import static com.pronoia.aries.blueprint.util.namespace.NamespaceHandlerUtil.getChildElements;


/**
 *
 */
public class ElementParser {
    final Element element;
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Construct an element parser with the supplied {@link Element}.
     *
     * @param element The source {@link Element}.
     */
    public ElementParser(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("The element cannot be null");
        }

        this.element = element;
    }

    public ElementParser(Element parentElement, String tagName) {
        if (parentElement == null) {
            throw new IllegalArgumentException(String.format("ElementParser(parentElement[null], tagName[%s]) - the parent element cannot be null", tagName));
        }

        if (tagName == null || tagName.isEmpty()) {
            throw new IllegalArgumentException(String.format("ElementParser(parentElement[%s], tagName[%s]) - the tagName cannot be null or empty", parentElement.getTagName(), tagName));
        }

        List<Element> descendants = getChildElements(parentElement, tagName, false);
        if (descendants == null || descendants.isEmpty()) {
            throw new IllegalArgumentException(String.format("ElementParser(parentElement[%s], tagName[%s]) - descendant element not found in parent element", parentElement.getTagName(), tagName));
        }

        if (descendants.size() > 1) {
            log.warn("Parent element '%s' contains %d descendants with tagName '%s' - using first descendants", parentElement.getTagName(), descendants.size(), tagName);
        }
        this.element = descendants.get(0);
    }

    public ElementParser(Element parentElement, String tagName, int occurrence) {
        if (parentElement == null) {
            throw new IllegalArgumentException(String.format("ElementParser(parentElement[null], tagName[%s], occurrence[%d]) - the parent element cannot be null", tagName, occurrence));
        }

        if (tagName == null || tagName.isEmpty()) {
            throw new IllegalArgumentException(String.format("ElementParser(parentElement[%s], tagName[%s], occurrence[%d]) - the tagName cannot be null or empty", parentElement.getTagName(), tagName, occurrence));
        }

        if (occurrence < 0) {
            throw new IllegalArgumentException(String.format("ElementParser(parentElement[%s], tagName[%s], occurrence[%d]) - occurrence cannot be less than zero", parentElement.getTagName(), tagName, occurrence));
        }

        List<Element> descendants = getChildElements(parentElement, tagName, false);
        if (descendants == null || descendants.isEmpty()) {
            throw new IllegalArgumentException(String.format("ElementParser(parentElement[%s], tagName[%s], occurrence[%d]) - descendant element not found in parent element", parentElement.getTagName(), tagName, occurrence));
        }

        if (descendants.size() < occurrence) {
            throw new IllegalArgumentException(String.format("ElementParser(parentElement[%s], tagName[%s], occurrence[%d]) - insufficient number of descendant elements [%d] found in parent element ", parentElement.getTagName(), tagName, occurrence, descendants.size()));
        }

        this.element = descendants.get(occurrence);
    }

    /**
     * Return the tag name of the source {@link Element}.
     *
     * @return The tag name of the source {@link Element}.
     */
    public String getName() {
        return element.getTagName();
    }

    /**
     * Return the value of the source {@link Element}.
     *
     * @return The value of the source {@link Element}.
     */
    public String getValue() {
        return NamespaceHandlerUtil.getElementValue(element, false);
    }

    /**
     * Return the first descendant {@link Element} in the source {@link Element}.
     *
     * @param tagName The {@link Element} tag-name to match on.
     *
     * @return The descendant element.
     */
    public ElementParser getElement(String tagName) {
        return getElement(tagName, 0);
    }

    /**
     * Return the first descendant {@link Element} in the source {@link Element}.
     *
     * @param tagName    The {@link Element} tag-name to match on.
     * @param occurrence The occurrence (zero-based) of the element to return.
     *
     * @return The specified occurrence of the descendant element.
     */
    public ElementParser getElement(String tagName, int occurrence) {
        List<Element> descendants = getChildElements(element, tagName, true);

        if (descendants.size() < occurrence) {
            throw new IllegalArgumentException(String.format("Descendant '%s' element occurrence [%d] not found in '%s' element ", tagName, occurrence, element.getTagName()));
        }

        return new ElementParser(descendants.get(occurrence));
    }

    /**
     * Returns a {@link List} of all descendant {@link Element} instances in document order.
     *
     * @return A list of descendant elements.
     */
    public List<ElementParser> getElements() {
        List<ElementParser> answer = new LinkedList<>();

        for(Element descendant : getChildElements(element, true)) {
            answer.add(new ElementParser(descendant));
        }

        return answer;
    }

    /**
     * Returns a {@link List} of descendant {@link Element} instances, matching the specified tag name, in document order.
     *
     * @param tagName The {@link Element} tag-name to match on.
     *
     * @return A list of descendant elements matching the specified tag name.
     */
    public List<ElementParser> getElements(String tagName) {
        List<ElementParser> answer = new LinkedList<>();

        for(Element descendant : getChildElements(element, tagName,true)) {
            answer.add(new ElementParser(descendant));
        }

        return answer;
    }

    /**
     * Return the {@link Boolean} value of an {@link Attr} in the source {@link Element}.
     *
     * @param attributeName    Then name of the attribute the return the value of.
     * @param requireAttribute If true, the attribute must exist and its value must convertible to a {@link Boolean} value or an {@link ElementDefinitionException} will be thrown.
     *
     * @return The {@link Boolean} value of the attribute, or null if the attribute was not found in the element.
     */
    public Boolean getBooleanAttribute(final String attributeName, boolean requireAttribute) {
        return NamespaceHandlerUtil.getBooleanAttribute(element, attributeName, requireAttribute);
    }


    /**
     * Return the {@link String} value of an {@link Attr} in the source {@link Element}.
     *
     * @param attributeName    Then name of the attribute the return the value of.
     * @param requireAttribute If true, the attribute must exist and its value must convertible to a {@link String} value or an {@link ElementDefinitionException} will be thrown.
     *
     * @return The {@link String} value of the attribute, or null if the attribute was not found in the element.
     */
    public String getStringAttribute(final String attributeName, boolean requireAttribute) {
        return NamespaceHandlerUtil.getStringAttribute(element, attributeName, requireAttribute);
    }

    /**
     * Return the {@link Byte} value of an {@link Attr} in the source {@link Element}.
     *
     * @param attributeName    Then name of the attribute the return the value of.
     * @param requireAttribute If true, the attribute must exist and its value must convertible to a {@link Byte} value or an {@link ElementDefinitionException} will be thrown.
     *
     * @return The {@link Byte} value of the attribute, or null if the attribute was not found in the element.
     */
    public Byte getByteAttribute(final String attributeName, boolean requireAttribute) {
        return NamespaceHandlerUtil.getByteAttribute(element, attributeName, requireAttribute);
    }

    /**
     * Return the {@link Short} value of an {@link Attr} in the source {@link Element}.
     *
     * @param attributeName    Then name of the attribute the return the value of.
     * @param requireAttribute If true, the attribute must exist and its value must convertible to a {@link Short} value or an {@link ElementDefinitionException} will be thrown.
     *
     * @return The {@link Short} value of the attribute, or null if the attribute was not found in the element.
     */
    public Short getShortAttribute(final String attributeName, boolean requireAttribute) {
        return NamespaceHandlerUtil.getShortAttribute(element, attributeName, requireAttribute);
    }

    /**
     * Return the {@link Integer} value of an {@link Attr} in the source {@link Element}.
     *
     * @param attributeName    Then name of the attribute the return the value of.
     * @param requireAttribute If true, the attribute must exist and its value must convertible to a {@link Integer} value or an {@link ElementDefinitionException} will be thrown.
     *
     * @return The {@link Integer} value of the attribute, or null if the attribute was not found in the element.
     */
    public Integer getIntegerAttribute(final String attributeName, boolean requireAttribute) {
        return NamespaceHandlerUtil.getIntegerAttribute(element, attributeName, requireAttribute);
    }

    /**
     * Return the {@link Long} value of an {@link Attr} in the source {@link Element}.
     *
     * @param attributeName    Then name of the attribute the return the value of.
     * @param requireAttribute If true, the attribute must exist and its value must convertible to a {@link Long} value or an {@link ElementDefinitionException} will be thrown.
     *
     * @return The {@link Long} value of the attribute, or null if the attribute was not found in the element.
     */
    public Long getLongAttribute(final String attributeName, boolean requireAttribute) {
        return NamespaceHandlerUtil.getLongAttribute(element, attributeName, requireAttribute);
    }

    /**
     * Return the {@link Float} value of an {@link Attr} in the source {@link Element}.
     *
     * @param attributeName    Then name of the attribute the return the value of.
     * @param requireAttribute If true, the attribute must exist and its value must convertible to a {@link Float} value or an {@link ElementDefinitionException} will be thrown.
     *
     * @return The {@link Float} value of the attribute, or null if the attribute was not found in the element.
     */
    public Float getFloatAttribute(final String attributeName, boolean requireAttribute) {
        return NamespaceHandlerUtil.getFloatAttribute(element, attributeName, requireAttribute);
    }

    /**
     * Return the {@link Double} value of an {@link Attr} in the source {@link Element}.
     *
     * @param attributeName    Then name of the attribute the return the value of.
     * @param requireAttribute If true, the attribute must exist and its value must convertible to a {@link Double} value or an {@link ElementDefinitionException} will be thrown.
     *
     * @return The {@link Double} value of the attribute, or null if the attribute was not found in the element.
     */
    public Double getDoubleAttribute(final String attributeName, boolean requireAttribute) {
        return NamespaceHandlerUtil.getDoubleAttribute(element, attributeName, requireAttribute);
    }
}

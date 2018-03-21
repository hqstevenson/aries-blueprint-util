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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 *
 */
public class ElementParser {
    static final String ATTRIBUTE_CONVERSION_FAILURE_EXPLANATION_FORMAT = "Failed to convert '%s' attribute value '%s' to %s {element = '%s' document = '%s'}";

    Element element;

    /**
     * Construct an element parser with the supplied {@link Element}.
     *
     * @param element The source {@link Element}.
     */
    public ElementParser(Element element) {
        this.init(element);
    }

    public ElementParser(Element parentElement, String tagName) {
        init(parentElement, tagName, 0);
    }

    public ElementParser(Element parentElement, String tagName, int occurrence) {
        init(parentElement, tagName, occurrence);
    }

    /**
     * Return the owner document URI of the source {@link Element}, checking for a null document first.
     *
     * @param element The element to extract the document URI from.
     *
     * @return The owner document URI of the source {@link Element}, or null if the owner document is null.
     */
    static String safeGetOwnerDocumentUri(Element element) {
        String answer = null;

        if (element != null) {
            Document ownerDocument = element.getOwnerDocument();
            if (ownerDocument != null) {
                answer = ownerDocument.getDocumentURI();
            }
        }

        return answer;
    }

    /**
     * Return the tag name of the source {@link Element}.
     *
     * @return The tag name of the source {@link Element}.
     */
    public String getTagName() {
        return element.getTagName();
    }

    /**
     * Return the owner document URI of the source {@link Element}.
     *
     * @return The owner document URI of the source {@link Element}.
     */
    public String getOwnerDocumentURI() {
        String answer = null;

        Document ownerDocument = element.getOwnerDocument();
        if (ownerDocument != null) {
            answer = ownerDocument.getDocumentURI();
        }

        return answer;
    }

    /**
     * Return the text content of the source {@link Element}.
     *
     * @return The text content of the source {@link Element}, which may be null.
     */
    public String getValue() {
        NodeList children = element.getChildNodes();

        if (children != null && children.getLength() > 0) {
            for (int childNodeIndex = 0; childNodeIndex < children.getLength(); ++childNodeIndex) {
                Node childNode = children.item(childNodeIndex);
                if (Node.TEXT_NODE == childNode.getNodeType()) {
                    return childNode.getNodeValue();
                }
            }
        }

        return null;
    }

    /**
     * Return the text content of the {@link Element}.
     *
     * @param requireValue If true, the element must have content or an {@link ElementDefinitionException} will be thrown.
     *
     * @return the text content of the element, which may be null/empty.
     *
     * @throws ElementDefinitionException Raised if requireElement is true and the {@link Element} does not have content.
     */
    public String getValue(boolean requireValue) {
        String answer = getValue();

        if (requireValue) {
            if (answer == null || answer.isEmpty()) {
                String explanation = String.format("Element '%s' text content is null or empty in document '%s'", getTagName(), getOwnerDocumentURI());

                throw new ElementDefinitionException(explanation);
            }
        }

        return answer;
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
     * @param tagName        The {@link Element} tag-name to match on.
     * @param requireElement If true, the descendant {@link Element} must be found in the parent {@link Element} or
     *                       an {@link ElementDefinitionException} will be thrown.
     *
     * @return The descendant element.
     */
    public ElementParser getElement(String tagName, boolean requireElement) {
        return getElement(tagName, 0, requireElement);
    }

    /**
     * Return the specified occurrence of the descendant {@link Element} in the source {@link Element}.
     *
     * @param tagName    The {@link Element} tag-name to match on.
     * @param occurrence The occurrence (zero-based) of the element to return.
     *
     * @return The specified occurrence of the descendant element.
     */
    public ElementParser getElement(String tagName, int occurrence) {
        if (tagName == null || tagName.isEmpty()) {
            String message = String.format("getElement(tagName[%s], occurrence[%d]) - tagName cannot be null or empty {element = '%s' document = '%s'}",
                tagName, occurrence, getTagName(), getOwnerDocumentURI());
            throw new IllegalArgumentException(message);
        }

        if (occurrence < 0) {
            String message = String.format("getElement(tagName[%s], occurrence[%d]) - occurrence cannot be less than zero {element = '%s' document = '%s'}",
                tagName, occurrence, getTagName(), getOwnerDocumentURI());
            throw new IllegalArgumentException(message);
        }

        NodeList children = element.getChildNodes();
        int occurrenceCounter = -1;
        if (children != null && children.getLength() > 0) {
            for (int childIndex = 0; childIndex < children.getLength(); ++childIndex) {
                Node child = children.item(childIndex);
                if (Node.ELEMENT_NODE == child.getNodeType()) {
                    Element childElement = (Element) child;
                    if (childElement.getTagName().equals(tagName)) {
                        if (++occurrenceCounter == occurrence) {
                            return new ElementParser((Element) child);
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Return the specified occurrence of the descendant {@link Element} in the source {@link Element}.
     *
     * @param tagName        The {@link Element} tag-name to match on.
     * @param occurrence     The occurrence (zero-based) of the element to return.
     * @param requireElement If true, the specified occurrence of the descendant {@link Element} must be found in the parent {@link Element} or
     *                       an {@link ElementDefinitionException} will be thrown.
     *
     * @return The specified occurrence of the descendant element.
     *
     * @throws ElementDefinitionException Raised if requireElement is true and the specified occurrence of the descendant {@link Element} is not found the parent {@link Element}.
     */
    public ElementParser getElement(String tagName, int occurrence, boolean requireElement) {
        ElementParser answer = getElement(tagName, occurrence);

        if (requireElement && answer == null) {
            String explanation;
            if (occurrence > 0) {
                explanation = String.format("Descendant '%s' element occurrence [%d] not found {element = '%s' document = '%s' }", tagName, occurrence, getTagName(), getOwnerDocumentURI());
            } else {
                explanation = String.format("Descendant '%s' element not found {element = '%s' document = '%s' }", tagName, getTagName(), getOwnerDocumentURI());
            }

            throw new ElementDefinitionException(explanation);
        }

        return answer;
    }

    /**
     * Returns a {@link List} of all descendant {@link Element} instances in document order.
     *
     * @return A list of descendant elements, which may be empty.
     */
    public List<ElementParser> getElements() {
        List<ElementParser> answer = new LinkedList<>();

        NodeList children = element.getChildNodes();
        if (children != null && children.getLength() > 0) {
            for (int childIndex = 0; childIndex < children.getLength(); ++childIndex) {
                Node child = children.item(childIndex);
                if (Node.ELEMENT_NODE == child.getNodeType()) {
                    answer.add(new ElementParser((Element) child));
                }
            }
        }

        return answer;
    }

    /**
     * Returns a {@link List} of descendant {@link Element} instances, matching the specified tag name, in document order.
     *
     * @param tagName The {@link Element} tag-name to match on.
     *
     * @return A list of descendant elements matching the specified tag name, which may be empty.
     */
    public List<ElementParser> getElements(String tagName) {
        List<ElementParser> answer = new LinkedList<>();

        NodeList children = element.getChildNodes();
        if (children != null && children.getLength() > 0) {
            for (int childIndex = 0; childIndex < children.getLength(); ++childIndex) {
                Node child = children.item(childIndex);
                if (Node.ELEMENT_NODE == child.getNodeType()) {
                    Element childElement = (Element) child;
                    if (childElement.getTagName().equals(tagName)) {
                        answer.add(new ElementParser((Element) child));
                    }
                }
            }
        }

        return answer;
    }

    /**
     * Returns a {@link List} of descendant {@link Element} instances, matching the specified tag name, in document order.
     *
     * @param tagName The {@link Element} tag-name to match on.
     * @param requireElement If true, at least one descendant {@link Element} must be found in the parent {@link Element} or
     *                       an {@link ElementDefinitionException} will be thrown.
     *
     * @return A list of descendant elements matching the specified tag name, which may be empty.
     */
    public List<ElementParser> getElements(String tagName, boolean requireElement) {
        List<ElementParser> answer = getElements(tagName);

        if (requireElement && (answer == null || answer.isEmpty())) {
            String explanation = String.format("Descendant '%s' element not found {element = '%s' document = '%s' }", tagName, getTagName(), getOwnerDocumentURI());

            throw new ElementDefinitionException(explanation);
        }

        return answer;
    }

    /**
     * Returns a {@link Map} of all descendant {@link Element}s in document order.  The {@link Map} key is the @{link Element} tag name
     * and the {@link Map} value is a {@link List} of {@link Element}s.
     *
     * @return a map of descendant elements, which may be empty.
     */
    public Map<String, List<ElementParser>> getElementMap() {
        Map<String, List<ElementParser>> answer = new LinkedHashMap<>();

        NodeList children = element.getChildNodes();

        if (children != null && children.getLength() > 0) {
            for (int childIndex = 0; childIndex < children.getLength(); ++childIndex) {
                Node child = children.item(childIndex);
                if (Node.ELEMENT_NODE == child.getNodeType()) {
                    Element childElement = (Element) child;
                    String childTagName = childElement.getTagName();
                    if (answer.containsKey(childTagName)) {
                        answer.get(childTagName).add(new ElementParser(childElement));
                    } else {
                        List<ElementParser> tmpElementList = new LinkedList<>();
                        tmpElementList.add(new ElementParser(childElement));
                        answer.put(childTagName, tmpElementList);
                    }
                }
            }
        }

        return answer;
    }

    /**
     * Returns a {@link List} of values of descendant {@link Element} instances, matching the specified tag name, in document order.
     *
     * @param tagName The {@link Element} tag-name to match on.
     *
     * @return A list of the values of descendant elements matching the specified tag name, which may be empty.
     */
    public List<String> getElementValues(String tagName) {
        List<String> answer = new LinkedList<>();

        List<ElementParser> elements = getElements(tagName);
        if (elements != null && !elements.isEmpty()) {
            for (ElementParser element : elements) {
                String value = element.getValue();
                if (value != null && !value.isEmpty()) {
                    answer.add(value);
                }
            }
        }

        return answer;
    }

    /**
     * Returns a {@link List} of values of descendant {@link Element} instances, matching the specified tag name, in document order.
     *
     * @param tagName The {@link Element} tag-name to match on.
     * @param requireElement If true, at least one descendant {@link Element} with content must be found in the parent {@link Element} or
     *                       an {@link ElementDefinitionException} will be thrown.
     *
     * @return A list of values of descendant elements matching the specified tag name, which may be empty.
     */
    public List<String> getElementValues(String tagName, boolean requireElement) {
        List<String> answer = getElementValues(tagName);

        if (requireElement && (answer == null || answer.isEmpty())) {
            String explanation = String.format("Descendant '%s' element not found {element = '%s' document = '%s' }", tagName, getTagName(), getOwnerDocumentURI());

            throw new ElementDefinitionException(explanation);
        }

        return answer;
    }

    /**
     * Returns a {@link List} of all {@link Attr}s in the {@link Element}.
     *
     * @return a list of attributes, which may be empty.
     */
    public List<Attr> getAttributes() {
        List<Attr> answer = new LinkedList<>();

        NamedNodeMap attributes = element.getAttributes();
        if (attributes != null && attributes.getLength() > 0) {
            for (int attributeIndex = 0; attributeIndex < attributes.getLength(); ++attributeIndex) {
                answer.add((Attr) attributes.item(attributeIndex));
            }
        }

        return answer;
    }

    /**
     * Returns a {@link Map} of all {@link Attr}s in the {@link Element}.  The {@link Map} key is the @{link Attr} name
     * and the {@link Map} value the {@link Attr}.
     *
     * @return a map of attribute values, which may be empty.
     */
    public Map<String, Attr> getAttributeMap() {
        Map<String, Attr> answer = new LinkedHashMap<>();

        NamedNodeMap attributes = element.getAttributes();
        if (attributes != null && attributes.getLength() > 0) {
            for (int attributeIndex = 0; attributeIndex < attributes.getLength(); ++attributeIndex) {
                Attr attr = (Attr) attributes.item(attributeIndex);
                answer.put(attr.getName(), attr);
            }
        }

        return answer;
    }

    /**
     * Returns a {@link Map} of all {@link Attr}s in the {@link Element}.  The {@link Map} key is the @{link Attr} name
     * and the {@link Map} value the {@link Attr} value.
     *
     * @return a map of attribute values, which may be empty.
     */
    public Map<String, String> getAttributeValueMap() {
        Map<String, String> answer = new LinkedHashMap<>();

        NamedNodeMap attributes = element.getAttributes();
        if (attributes != null && attributes.getLength() > 0) {
            for (int attributeIndex = 0; attributeIndex < attributes.getLength(); ++attributeIndex) {
                Attr attr = (Attr) attributes.item(attributeIndex);
                answer.put(attr.getName(), attr.getValue());
            }
        }

        return answer;
    }

    /**
     * Return the value of an {@link Attr} in the {@link Element}.
     *
     * @param attributeName Then name of the attribute the return.
     *
     * @return the value of the attribute, or null if the attribute was not found in the element.
     */
    public String getAttribute(String attributeName) {
        if (attributeName == null || attributeName.isEmpty()) {
            String message = String.format("getAttribute(attributeName[%s]) - attributeName cannot be null or empty {element = '%s' document = '%s'}", attributeName, getTagName(), getOwnerDocumentURI());
            throw new IllegalArgumentException(message);
        }

        if (element.hasAttribute(attributeName)) {
            Attr attr = element.getAttributeNode(attributeName);
            if (attr == null) {
                String message = String.format("getAttribute(attributeName[%s]) - Element.getAttributeNode(attributeName) failed after Element.hasAttribute(attributeName) returned true {element = '%s' document = '%s'}", attributeName, getTagName(), getOwnerDocumentURI());
                throw new IllegalStateException(message);
            }
            return attr.getValue();
        }

        return null;
    }

    /**
     * Return the value of an {@link Attr} in the {@link Element}.
     *
     * @param attributeName    Then name of the attribute the return.
     * @param requireAttribute If true, the attribute must exist or an {@link ElementDefinitionException} will be thrown.
     *
     * @return the value of the attribute, or null if the attribute was not found in the element.
     *
     * @throws ElementDefinitionException Raised if requireAttribute is true and the @{link Attr} is not found in source {@link Element}.
     */
    public String getAttribute(String attributeName, boolean requireAttribute) {
        String answer = getAttribute(attributeName);

        if (requireAttribute && answer == null) {
            String explanation = String.format("Attribute '%s' not found {element = '%s' document = '%s'}", attributeName, getTagName(), getOwnerDocumentURI());

            throw new ElementDefinitionException(explanation);
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
        String stringValue = getAttribute(attributeName, requireAttribute);

        return stringValue != null ? Boolean.valueOf(stringValue) : null;
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
        return getAttribute(attributeName, requireAttribute);
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
        String stringValue = getAttribute(attributeName, requireAttribute);

        Byte answer = null;

        if (stringValue != null) {
            try {
                answer = Byte.valueOf(stringValue);
            } catch (Exception conversionEx) {
                throw new ElementDefinitionException(String.format(ATTRIBUTE_CONVERSION_FAILURE_EXPLANATION_FORMAT, attributeName, stringValue, Byte.class, getTagName(), getOwnerDocumentURI()), conversionEx);
            }
        }

        return answer;
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
        String stringValue = getAttribute(attributeName, requireAttribute);

        Short answer = null;

        if (stringValue != null) {
            try {
                answer = Short.valueOf(stringValue);
            } catch (Exception conversionEx) {
                throw new ElementDefinitionException(String.format(ATTRIBUTE_CONVERSION_FAILURE_EXPLANATION_FORMAT, attributeName, stringValue, Short.class, getTagName(), getOwnerDocumentURI()), conversionEx);
            }
        }

        return answer;
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
        String stringValue = getAttribute(attributeName, requireAttribute);

        Integer answer = null;

        if (stringValue != null) {
            try {
                answer = Integer.valueOf(stringValue);
            } catch (Exception conversionEx) {
                throw new ElementDefinitionException(String.format(ATTRIBUTE_CONVERSION_FAILURE_EXPLANATION_FORMAT, attributeName, stringValue, Integer.class, getTagName(), getOwnerDocumentURI()), conversionEx);
            }
        }

        return answer;
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
        String stringValue = getAttribute(attributeName, requireAttribute);

        Long answer = null;

        if (stringValue != null) {
            try {
                answer = Long.valueOf(stringValue);
            } catch (Exception conversionEx) {
                throw new ElementDefinitionException(String.format(ATTRIBUTE_CONVERSION_FAILURE_EXPLANATION_FORMAT, attributeName, stringValue, Long.class, getTagName(), getOwnerDocumentURI()), conversionEx);
            }
        }

        return answer;
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
        String stringValue = getAttribute(attributeName, requireAttribute);

        Float answer = null;

        if (stringValue != null) {
            try {
                answer = Float.valueOf(stringValue);
            } catch (Exception conversionEx) {
                throw new ElementDefinitionException(String.format(ATTRIBUTE_CONVERSION_FAILURE_EXPLANATION_FORMAT, attributeName, stringValue, Float.class, getTagName(), getOwnerDocumentURI()), conversionEx);
            }
        }

        return answer;
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
        String stringValue = getAttribute(attributeName, requireAttribute);

        Double answer = null;

        if (stringValue != null) {
            try {
                answer = Double.valueOf(stringValue);
            } catch (Exception conversionEx) {
                throw new ElementDefinitionException(String.format(ATTRIBUTE_CONVERSION_FAILURE_EXPLANATION_FORMAT, attributeName, stringValue, Double.class, getTagName(), getOwnerDocumentURI()), conversionEx);
            }
        }

        return answer;
    }

    void init(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("ElementParser(element[null]) - the element cannot be null");
        }

        this.element = element;
    }

    void init(Element parentElement, String tagName, int occurrence) {
        if (parentElement == null) {
            throw new IllegalArgumentException(String.format("ElementParser(parentElement[null], tagName[%s], occurrence[%d]) - the parent element cannot be null",
                tagName, occurrence));
        }

        if (tagName == null || tagName.isEmpty()) {
            String message = String.format("ElementParser(parentElement[%s], tagName[%s], occurrence[%d]) - the tagName cannot be null or empty {document = '%s'}",
                parentElement.getTagName(), tagName, occurrence, safeGetOwnerDocumentUri(parentElement));

            throw new IllegalArgumentException(message);
        }

        if (occurrence < 0) {
            String message = String.format("ElementParser(parentElement[%s], tagName[%s], occurrence[%d]) - occurrence cannot be less than zero {document = '%s'}",
                parentElement.getTagName(), tagName, occurrence, safeGetOwnerDocumentUri(parentElement));
            throw new IllegalArgumentException(message);
        }

        NodeList childNodes = parentElement.getChildNodes();
        int occurrenceCounter = -1;
        if (childNodes != null && childNodes.getLength() > 0) {
            for (int childIndex = 0; childIndex < childNodes.getLength(); ++childIndex) {
                Node childNode = childNodes.item(childIndex);
                if (Node.ELEMENT_NODE == childNode.getNodeType()) {
                    Element childElement = (Element) childNode;
                    if (tagName.equals(childElement.getTagName()) && occurrence == ++occurrenceCounter) {
                        init(childElement);
                        return;
                    }
                }
            }
            String message = String.format("ElementParser(parentElement[%s], tagName[%s], occurrence[%d]) - insufficient number of descendant elements [%d] found in parent element {document = '%s'}",
                    parentElement.getTagName(), tagName, occurrence, occurrenceCounter + 1, safeGetOwnerDocumentUri(parentElement));

            throw new IllegalArgumentException(message);
        } else {
            String message = String.format("ElementParser(parentElement[%s], tagName[%s], occurrence[%d]) - descendant element not found in parent element {document = '%s'}",
                    parentElement.getTagName(), tagName, occurrence, safeGetOwnerDocumentUri(parentElement));

            throw new IllegalArgumentException(message);
        }
    }

 }

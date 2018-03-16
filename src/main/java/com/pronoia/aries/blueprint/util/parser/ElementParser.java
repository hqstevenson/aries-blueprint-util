package com.pronoia.aries.blueprint.util.parser;

import com.pronoia.aries.blueprint.util.namespace.ElementDefinitionException;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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
    static final String CONVERSION_EXPLANATION_FORMAT ="Error converting value '%s' of attribute '%s' in element '%s' (document '%s') to %s";

    protected Logger log = LoggerFactory.getLogger(this.getClass());
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
     * Return the value of the source {@link Element}.
     *
     * @return The value of the source {@link Element}.
     */
    public String getValue() {
        return getValue(false);
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
        String answer = null;

        NodeList children = element.getChildNodes();

        if (children != null && children.getLength() > 0) {
            for (int childNodeIndex = 0; childNodeIndex < children.getLength(); ++childNodeIndex) {
                Node childNode = children.item(childNodeIndex);
                if (Node.TEXT_NODE == childNode.getNodeType()) {
                    answer = childNode.getNodeValue();
                    break;
                }
            }
        }

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
     * @param tagName The {@link Element} tag-name to match on.
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
        return getElement(tagName, occurrence, false);
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
        if (tagName == null || tagName.isEmpty()) {
            String message = String.format("getChildElements(element[%s], tagName[%s], requireElement[%b]) - tagName cannot be null or empty", getTagName(), tagName, requireElement);
            throw new IllegalArgumentException(message);
        }

        ElementParser answer = null;

        NodeList children = element.getChildNodes();
        int occurrenceCounter = -1;
        if (children != null && children.getLength() > 0) {
            for (int childIndex = 0; childIndex < children.getLength(); ++childIndex) {
                Node child = children.item(childIndex);
                if (Node.ELEMENT_NODE == child.getNodeType()) {
                    Element childElement = (Element) child;
                    if (childElement.getTagName().equals(tagName)) {
                        if (++occurrenceCounter == occurrence) {
                            answer = new ElementParser((Element) child);
                        }
                    }
                }
            }
        }

        if (requireElement && answer == null) {
            String explanation = String.format("Descendant '%s' element occurrence [%d] not found in '%s' element in document '%s'", tagName, occurrence, getTagName(), getOwnerDocumentURI());

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
     * @return A list of descendant elements matching the specified tag name.
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
     * Returns a {@link Map} of all descendant {@link Element}s in document order.  The {@link Map} key is the @{link Element} tag name
     * and the {@link Map} value is a {@link List} of {@link Element}s.
     *
     * @return a map of descendant elements, which may be empty if no descendant elements are found in the parent element.
     */
    public Map<String, List<ElementParser>> getElementMap() {
        Map<String, List<ElementParser>> answer = new LinkedHashMap<>();

        NodeList children = element.getChildNodes();

        if (children != null && children.getLength() > 0) {
            for (int childIndex=0; childIndex < children.getLength(); ++ childIndex) {
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
     * Returns a {@link Map} of all descendant {@link Element}s in document order.  The {@link Map} key is the @{link Element} tag name
     * and the {@link Map} value is a {@link List} of {@link Element}s.
     *
     * @return a map of descendant elements, which may be empty if no descendant elements are found in the parent element.
     */
    public Map<String, String> getAttributeMap() {
        Map<String, String> answer = new LinkedHashMap<>();

        NamedNodeMap attributes = element.getAttributes();
        if (attributes != null && attributes.getLength() > 0) {
            for (int attributeIndex=0; attributeIndex < attributes.getLength(); ++attributeIndex) {
                Attr attr = (Attr)attributes.item(attributeIndex);
                answer.put(attr.getName(), attr.getValue());
            }
        }

        return answer;
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
        if (attributeName == null || attributeName.isEmpty()) {
            String message = String.format("getAttribute(attributeName[%s], requireAttribute[%b]) from '%s' - attributeName cannot be null or empty", attributeName, requireAttribute, getTagName());
            throw new IllegalArgumentException(message);
        }

        String answer = null;

        if (element.hasAttribute(attributeName)) {
            Attr attr = element.getAttributeNode(attributeName);
            if (attr == null) {
                String message = String.format("getAttribute(attributeName[%s], requireAttribute[%b]) from '%s' - getAttributeNode(attributeName) failed after hasAttribute(attributeName) returned true", attributeName, requireAttribute, getTagName());
                throw new IllegalStateException(message);
            }
            answer = attr.getValue();
        } else if (requireAttribute) {
            String explanation = String.format("Attribute '%s' not found in element '%s'", attributeName, getTagName());

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
        String answer = getAttribute(attributeName, requireAttribute);

        if (requireAttribute && answer == null) {
            String explanation = String.format("Attribute '%s' value in element '%s' is empty", attributeName, getTagName());

            throw new ElementDefinitionException(explanation);
        }

        return answer;
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
                throw new ElementDefinitionException(String.format(CONVERSION_EXPLANATION_FORMAT, stringValue, attributeName, getTagName(), getOwnerDocumentURI(), Byte.class), conversionEx);
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
                throw new ElementDefinitionException(String.format(CONVERSION_EXPLANATION_FORMAT, stringValue, attributeName, getTagName(), getOwnerDocumentURI(), Short.class), conversionEx);
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
                throw new ElementDefinitionException(String.format(CONVERSION_EXPLANATION_FORMAT, stringValue, attributeName, getTagName(), getOwnerDocumentURI(), Integer.class), conversionEx);
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
                throw new ElementDefinitionException(String.format(CONVERSION_EXPLANATION_FORMAT, stringValue, attributeName, getTagName(), getOwnerDocumentURI(), Long.class), conversionEx);
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
                throw new ElementDefinitionException(String.format(CONVERSION_EXPLANATION_FORMAT, stringValue, attributeName, getTagName(), getOwnerDocumentURI(), Float.class), conversionEx);
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
                throw new ElementDefinitionException(String.format(CONVERSION_EXPLANATION_FORMAT, stringValue, attributeName, getTagName(), getOwnerDocumentURI(), Double.class), conversionEx);
            }
        }

        return answer;
    }

    void init(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("The element cannot be null");
        }

        this.element = element;
    }

    void init(Element parentElement, String tagName, int occurrence) {
        if (parentElement == null) {
            throw new IllegalArgumentException(String.format("ElementParser(parentElement[null], tagName[%s], occurrence[%d]) - the parent element cannot be null",
                tagName, occurrence));
        }

        if (tagName == null || tagName.isEmpty()) {
            Document document = parentElement.getOwnerDocument();

            String message;
            if (document != null) {
                message = String.format("Document '%s' ElementParser(parentElement[%s], tagName[%s], occurrence[%d]) - the tagName in '%s' cannot be null or empty",
                    document.getDocumentURI(), parentElement.getTagName(), tagName, occurrence);
            } else {
                message = String.format("ElementParser(parentElement[%s], tagName[%s], occurrence[%d]) - the tagName in '%s' cannot be null or empty",
                    parentElement.getTagName(), tagName, occurrence);
            }
            throw new IllegalArgumentException(message);
        }

        if (occurrence < 0) {
            Document document = parentElement.getOwnerDocument();

            String message;
            if (document != null) {
                message = String.format("Document '%s' ElementParser(parentElement[%s], tagName[%s], occurrence[%d]) - occurrence cannot be less than zero",
                    document.getDocumentURI(), parentElement.getTagName(), tagName, occurrence);
            } else {
                message = String.format("ElementParser(parentElement[%s], tagName[%s], occurrence[%d]) - occurrence cannot be less than zero",
                    parentElement.getTagName(), tagName, occurrence);
            }
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
            Document document = parentElement.getOwnerDocument();

            String message;
            if (document != null) {
                message = String.format("Document '%s' ElementParser(parentElement[%s], tagName[%s], occurrence[%d]) - insufficient number of descendant elements [%d] found in parent element",
                    document.getDocumentURI(), parentElement.getTagName(), tagName, occurrence, occurrenceCounter);
            } else {
                message = String.format("ElementParser(parentElement[%s], tagName[%s], occurrence[%d]) - insufficient number of descendant elements [%d] found in parent element",
                    parentElement.getTagName(), tagName, occurrence, occurrenceCounter);
            }

            throw new IllegalArgumentException(message);
        } else {
            Document document = parentElement.getOwnerDocument();

            String message;
            if (document != null) {
                message = String.format("Document '%s' ElementParser(parentElement[%s], tagName[%s], occurrence[%d]) - descendant element not found in parent element",
                    document.getDocumentURI(), parentElement.getTagName(), tagName, occurrence);
            } else {
                message = String.format("ElementParser(parentElement[%s], tagName[%s], occurrence[%d]) - descendant element not found in parent element",
                    parentElement.getTagName(), tagName, occurrence);
            }

            throw new IllegalArgumentException(message);
        }
    }

    class MDCHelper implements AutoCloseable {
        static final String MDC_DOCUMENT = "blueprint.document";
        static final String MDC_ELEMENT = "blueprint.element";

        Map<String, String> originalContextMap;

        public MDCHelper(ElementParser elementParser) {
            originalContextMap = MDC.getCopyOfContextMap();

            MDC.put(MDC_DOCUMENT, elementParser.getOwnerDocumentURI());
            MDC.put(MDC_ELEMENT, elementParser.getTagName());
        }

        @Override
        public void close() {
            MDC.setContextMap(originalContextMap);
        }
    }
}

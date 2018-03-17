package com.pronoia.aries.blueprint.util.namespace;

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
public final class NamespaceHandlerUtil {
    static final String CONVERSION_EXPLANATION_FORMAT ="Error converting value '%s' of attribute '%s' in element '%s' to %s";

    private NamespaceHandlerUtil() {
    }


    /**
     * Returns a {@link List} of all descendant {@link Element} instances in document order.
     *
     * @param element        The parent {@link Element}.
     * @param requireElement If true, at least one descendant {@link Element} must be found in the parent {@link Element} or
     *                       an {@link ElementDefinitionException} will be thrown.
     *
     * @return A list of descendant elements, which may be empty if no descendant elements are found in the parent element.
     *
     * @throws ElementDefinitionException Raised if requireElement is true and no descendant {@link Element}s are found in the parent {@link Element}.
     */
    public static List<Element> getChildElements(final Element element, boolean requireElement) {
        if (element == null) {
            String message = String.format("getChildElements(element[null], requireElement[%b]) - element cannot be null", requireElement);
            throw new IllegalArgumentException(message);
        }

        List<Element> answer = new LinkedList<>();

        NodeList childNodes = element.getChildNodes();
        if (childNodes != null && childNodes.getLength() > 0) {
            for (int childIndex = 0; childIndex < childNodes.getLength(); ++childIndex) {
                Node childNode = childNodes.item(childIndex);
                if (Node.ELEMENT_NODE == childNode.getNodeType()) {
                    answer.add((Element) childNode);
                }
            }
        }

        if (requireElement && (answer == null || answer.isEmpty())) {
            String explanation = String.format("No descendant elements found in parent element '%s' in document '%s'", element.getTagName(), element.getOwnerDocument().getDocumentURI());
            throw new ElementDefinitionException(explanation);
        }

        return answer;
    }


    /**
     * Returns a {@link List} of all descendant {@link Element} instances with the specified tag name, in document order.
     *
     * @param element        The parent {@link Element}.
     * @param tagName        The {@link Element} tag-name to match on.
     * @param requireElement If true, at least one descendant {@link Element} matching the specified tag name must be found in the parent {@link Element} or
     *                       an {@link ElementDefinitionException} will be thrown.
     *
     * @return A list of matching elements, which may be empty if no matching descendant elements are found in the parent element.
     *
     * @throws ElementDefinitionException Raised if requireElement is true and no descendant {@link Element}s are found in the parent {@link Element}.
     */
    public static List<Element> getChildElements(final Element element, String tagName, boolean requireElement) {
        if (element == null) {
            String message = String.format("getChildElements(element[null], tagName[%s], requireElement[%b]) - element cannot be null", tagName, requireElement);
            throw new IllegalArgumentException(message);
        }

        if (tagName == null || tagName.isEmpty()) {
            String message = String.format("getChildElements(element[%s], tagName[%s], requireElement[%b]) - tagName cannot be null or empty", element.getTagName(), tagName, requireElement);
            throw new IllegalArgumentException(message);
        }

        List<Element> answer = new LinkedList<>();

        NodeList childNodes = element.getChildNodes();
        if (childNodes != null && childNodes.getLength() > 0) {
            for (int childIndex = 0; childIndex < childNodes.getLength(); ++childIndex) {
                Node child = childNodes.item(childIndex);
                if (Node.ELEMENT_NODE == child.getNodeType()) {
                    Element childElement = (Element) child;
                    if (childElement.getTagName().equals(tagName)) {
                        answer.add((Element) child);
                    }
                }
            }
        }

        if (requireElement && (answer == null || answer.isEmpty())) {
            String documentUri = null;
            Document document = element.getOwnerDocument();
            if (document != null) {
                documentUri = document.getDocumentURI();
            }
            String explanation = String.format("Descendant element matching tag name '%s' not found in parent element '%s' in document '%s'", tagName, element.getTagName(), documentUri);

            throw new ElementDefinitionException(explanation);
        }

        return answer;
    }


    /**
     * Returns a {@link Map} of all descendant {@link Element}s in document order.  The {@link Map} key is the @{link Element} tag name
     * and the {@link Map} value is a {@link List} of {@link Element}s.
     *
     * @param element        The parent {@link Element}.
     * @param requireElement If true, at least one descendant {@link Element} must be found in the parent {@link Element} or
     *                       an {@link ElementDefinitionException} will be thrown.
     *
     * @return a map of descendant elements, which may be empty if no descendant elements are found in the parent element.
     *
     * @throws ElementDefinitionException Raised if requireElement is true and no descendant {@link Element}s are found in the parent {@link Element}.
     */
    public static Map<String, List<Element>> getChildElementMap(final Element element, boolean requireElement) {
        if (element == null) {
            String message = String.format("getChildElementMap(element[null], requireElement[%b]) - element cannot be null", requireElement);
            throw new IllegalArgumentException(message);
        }

        Map<String, List<Element>> answer = new LinkedHashMap<>();

        List<Element> childElements = getChildElements(element, requireElement);

        if (childElements != null && !childElements.isEmpty()) {
            for (Element childElement : childElements) {
                String childElementTagName = childElement.getTagName();
                if (answer.containsKey(childElementTagName)) {
                    answer.get(childElementTagName).add(childElement);
                } else {
                    List<Element> tmpElementList = new LinkedList<>();
                    tmpElementList.add(childElement);
                    answer.put(childElementTagName, tmpElementList);
                }
            }
        }

        // Don't need to do anything with requireElement -  getChildElements will throw an exception if there are no descendants.

        return answer;
    }


    /**
     * Return the content of an @{link Element}, excluding descendants.
     *
     * @param element      The source @{link Element}
     * @param requireValue If true, the element must have content or an {@link ElementDefinitionException} will be thrown.
     *
     * @return the text content of the element, which may be null/empty.
     *
     * @throws ElementDefinitionException Raised if requireElement is true and the {@link Element} does not have content.
     */
    public static String getElementValue(final Element element, boolean requireValue) {
        if (element == null) {
            String message = String.format("getElementValue(element[null], requireElement[%b]) - element cannot be null", requireValue);
            throw new IllegalArgumentException(message);
        }

        String answer = null;
        NodeList childNodes = element.getChildNodes();

        if (childNodes != null && childNodes.getLength() > 0) {
            for (int childNodeIndex = 0; childNodeIndex < childNodes.getLength(); ++childNodeIndex) {
                Node childNode = childNodes.item(childNodeIndex);
                if (Node.TEXT_NODE == childNode.getNodeType()) {
                    answer = childNode.getNodeValue();
                    break;
                }
            }
        }

        if (requireValue) {
            if (answer == null || answer.isEmpty()) {
                String explanation = String.format("Element '%s' text content is null or empty", element.getTagName());

                throw new ElementDefinitionException(explanation);
            }
        }

        return answer;
    }


    /**
     * Return a {@link Map} of the content of descendant @{link Element}s.  The {@link Map} key is the @{link Element} tag name
     * and the {@link Map} value is a {@link List} of the @{link Element} contents.
     *
     * @param element      The parent {@link Element}.
     * @param requireValue If true, at least one descendant @{link Element} must have content or an {@link ElementDefinitionException} will be thrown.
     *
     * @return a map of the content of descendant elements, which may be empty if no descendant elements were found in the parent element.
     *
     * @throws ElementDefinitionException Raised if requireElement is true and none of the descendant {@link Element}s have content.
     */
    public static Map<String, List<String>> getChildElementValues(final Element element, boolean requireValue) {
        Map<String, List<String>> answer = new LinkedHashMap<>();

        Map<String, List<Element>> childElementMap = getChildElementMap(element, requireValue);

        if (childElementMap != null && !childElementMap.isEmpty()) {
            for (String tagName : childElementMap.keySet()) {
                List<String> tmpElementValuesForTagName = new LinkedList<>();
                for (Element childElement : childElementMap.get(tagName)) {
                    String childElementValue = getElementValue(childElement, false);
                    if (requireValue) {
                        if (childElementValue != null && !childElementValue.isEmpty()) {
                            tmpElementValuesForTagName.add(childElementValue);
                        }
                    } else {
                        tmpElementValuesForTagName.add(childElementValue);
                    }
                }
                if (!tmpElementValuesForTagName.isEmpty()) {
                    answer.put(tagName, tmpElementValuesForTagName);
                }
            }
        }

        if (requireValue) {
            if (answer == null || answer.isEmpty()) {
                String explanation = String.format("The text content of all the descendant elements of the '%s' element is null or empty", element.getTagName());

                throw new ElementDefinitionException(explanation);
            }
        }

        return answer;
    }


    /**
     * Return a {@link List} of the content of descendant @{link Element} instances with the specified tag name, in document order.
     *
     * @param element      The parent {@link Element}.
     * @param tagName      The {@link Element} tag-name to match on.
     * @param requireValue If true, the value of at least one descendant {@link Element} matching the specified tag name must not be null or empty)
     *                     or an {@link ElementDefinitionException} will be thrown.
     *
     * @return a list of the content of descendant elements, which may be empty if no descendant elements were found in the parent element.
     *
     * @throws ElementDefinitionException Raised if requireElement is true and the text content of all the descendant {@link Element}s is null or empty.
     */
    public static List<String> getChildElementValues(final Element element, final String tagName, boolean requireValue) {
        List<String> answer = new LinkedList<>();

        List<Element> childElements = getChildElements(element, tagName, requireValue);

        if (childElements != null && !childElements.isEmpty()) {
            for (Element childElement : childElements) {
                String childElementValue = getElementValue(childElement, false);
                if (requireValue) {
                    if (childElementValue != null && !childElementValue.isEmpty()) {
                        answer.add(childElementValue);
                    }
                } else {
                    answer.add(childElementValue);
                }
            }
        }

        if (requireValue) {
            if (answer == null || answer.isEmpty()) {
                String explanation = String.format("The text content of all the descendant elements with tagName '%s' of the '%s' parent element is null or empty", tagName, element.getTagName());

                throw new ElementDefinitionException(explanation);
            }
        }

        return answer;
    }

    /**
     * Return an {@link Attr} from an {@link Element}.
     *
     * @param element          The source {@link Element}.
     * @param attributeName    Then name of the attribute the return.
     * @param requireAttribute If true, the attribute must exist or an {@link ElementDefinitionException} will be thrown.
     *
     * @return the attribute instance, or null if the attribute was not found in the element.
     *
     * @throws ElementDefinitionException Raised if requireAttribute is true and the @{link Attr} is not found in source {@link Element}.
     */
    public static Attr getAttribute(final Element element, String attributeName, boolean requireAttribute) {
        if (element == null) {
            String message = String.format("getAttribute(element[null], attributeName[%s], requireAttribute[%b]) - element cannot be null", attributeName, requireAttribute);
            throw new IllegalArgumentException(message);
        }

        if (attributeName == null || attributeName.isEmpty()) {
            String message = String.format("getAttribute(element[%s], attributeName[%s], requireAttribute[%b]) - attributeName cannot be null or empty", element.getTagName(), attributeName, requireAttribute);
            throw new IllegalArgumentException(message);
        }

        Attr answer = null;

        if (element.hasAttribute(attributeName)) {
            answer = element.getAttributeNode(attributeName);
        }

        if (requireAttribute && answer == null) {
            String explanation = String.format("Attribute '%s' not found in element '%s'", attributeName, element.getTagName());

            throw new ElementDefinitionException(explanation);
        }

        return answer;
    }


    /**
     * Return a @{link List} of all the {@link Attr} instances of an {@link Element}.
     *
     * @param element The source @{link Element}.
     * @param requireAttribute If true, the @{link Element} must contain at least one {@link Attr} or an {@link ElementDefinitionException} will be thrown.
     *
     * @return the list of attributes, which may be empty if no attributes were found in the element.
     *
     * @throws ElementDefinitionException Raised if requireAttribute is true and the source {@link Element} does not contain any attributes.
     */
    public static List<Attr> getAttributes(final Element element, boolean requireAttribute) {
        List<Attr> answer = new LinkedList<>();

        NamedNodeMap attributeNodes = element.getAttributes();
        if (attributeNodes != null && attributeNodes.getLength() > 0) {
            for (int attributeIndex = 0; attributeIndex < attributeNodes.getLength(); ++attributeIndex) {
                answer.add((Attr) attributeNodes.item(attributeIndex));
            }
        }

        if (requireAttribute && answer.isEmpty()) {
            String explanation = String.format("No attributes found in element '%s'", element.getTagName());

            throw new ElementDefinitionException(explanation);
        }

        return answer;
    }


    /**
     * Return a @{link Map} of all the {@link Attr} instances of an {@link Element}.  The {@link Map} key is the @{link Attr} name
     * and the {@link Map} value the {@link Attr}.
     *
     * @param element The source @{link Element}.
     * @param requireAttribute If true, the @{link Element} must contain at least one {@link Attr} or an {@link ElementDefinitionException} will be thrown.
     *
     * @return the map of attributes, which may be empty if no attributes were found in the element.
     *
     * @throws ElementDefinitionException Raised if requireAttribute is true and the source {@link Element} does not contain any attributes.
     */
    public static Map<String, Attr> getAttributeMap(final Element element, boolean requireAttribute) {
        Map<String, Attr> answer = new LinkedHashMap<>();

        NamedNodeMap attributeNodes = element.getAttributes();
        if (attributeNodes != null && attributeNodes.getLength() > 0) {
            for (int attributeIndex = 0; attributeIndex < attributeNodes.getLength(); ++attributeIndex) {
                Attr attribute = (Attr) attributeNodes.item(attributeIndex);
                answer.put(attribute.getName(), attribute);
            }
        }

        if (requireAttribute && answer.isEmpty()) {
            String explanation = String.format("No attributes found in element '%s'", element.getTagName());

            throw new ElementDefinitionException(explanation);
        }

        return answer;
    }


    /**
     * Return the value of an {@link Attr} in an {@link Element}.
     *
     * @param element          The source {@link Element}.
     * @param attributeName    Then name of the attribute the return the value of.
     * @param requireAttribute If true, the attribute must exist or an {@link ElementDefinitionException} will be thrown.
     *
     * @return the value of the attribute, or null if the attribute was not found in the element.
     *
     * @throws ElementDefinitionException Raised if requireAttribute is true and the attribute is not found in source {@link Element} or the attribute's value is null or empty.
     */
    public static String getAttributeValue(final Element element, String attributeName, boolean requireAttribute) {
        Attr attr = getAttribute(element, attributeName, requireAttribute);

        return attr == null ? null : attr.getValue();
    }

    /**
     * Return a @{link Map} of the values all the {@link Attr} instances of an {@link Element}.  The {@link Map} key is the @{link Attr} name
     * and the {@link Map} value is the value of the {@link Attr}.
     *
     * @param element The source @{link Element}.
     * @param requireAttribute If true, the @{link Element} must contain at least one {@link Attr} or an {@link ElementDefinitionException} will be thrown.
     *
     * @return The map of attributes, which may be empty if no attributes were found in the element.
     *
     * @throws ElementDefinitionException Raised if requireAttribute is true and the source {@link Element} does not contain any attributes.
     */
    public static Map<String, String> getAttributeValueMap(final Element element, boolean requireAttribute) {
        Map<String, Attr> attributeMap =  getAttributeMap(element, requireAttribute);

        Map<String, String> answer = new LinkedHashMap<>();
        for (Map.Entry<String, Attr> attrEntry : attributeMap.entrySet()) {
            answer.put(attrEntry.getKey(), attrEntry.getValue().getValue());
        }

        return answer;
    }


    /**
     * Return the {@link Boolean} value of an {@link Attr} in an {@link Element}.
     *
     * @param element          The source {@link Element}.
     * @param attributeName    Then name of the attribute the return the value of.
     * @param requireAttribute If true, the attribute must exist and its value must convertible to a {@link Boolean} value or an {@link ElementDefinitionException} will be thrown.
     *
     * @return The {@link Boolean} value of the attribute, or null if the attribute was not found in the element.
     *
     * @throws ElementDefinitionException Raised if requireAttribute is true and the attribute is not found in source {@link Element}.
     */
    public static Boolean getBooleanAttribute(final Element element, final String attributeName, boolean requireAttribute) {
        String stringValue = getAttributeValue(element, attributeName, requireAttribute);

        Boolean answer = null;

        if (stringValue != null) {
            answer = Boolean.valueOf(stringValue);
        }

        return answer;
    }


    /**
     * Return the {@link String} value of an {@link Attr} in an {@link Element}.
     *
     * @param element          The source {@link Element}.
     * @param attributeName    Then name of the attribute the return the value of.
     * @param requireAttribute If true, the attribute must exist and its value must not be null or empty or an {@link ElementDefinitionException} will be thrown.
     *
     * @return The {@link String} value of the attribute, or null if the attribute was not found in the element.
     *
     * @throws ElementDefinitionException Raised if requireAttribute is true and the attribute is not found in source {@link Element}.
     */
    public static String getStringAttribute(final Element element, final String attributeName, boolean requireAttribute) {
        return getAttributeValue(element, attributeName, requireAttribute);
    }

    /**
     * Return the {@link Byte} value of an {@link Attr} in an {@link Element}.
     *
     * @param element          The source {@link Element}.
     * @param attributeName    Then name of the attribute the return the value of.
     * @param requireAttribute If true, the attribute must exist and its value must convertible to a {@link Byte} value or an {@link ElementDefinitionException} will be thrown.
     *
     * @return The {@link Byte} value of the attribute, or null if the attribute was not found in the element.
     *
     * @throws ElementDefinitionException Raised if requireAttribute is true and the attribute is not found in source {@link Element} or the attribute's value cannot be converted to a {@link Byte}.
     */
    public static Byte getByteAttribute(final Element element, final String attributeName, boolean requireAttribute) {
        String stringValue = getAttributeValue(element, attributeName, requireAttribute);

        Byte answer = null;

        if (stringValue != null) {
            try {
                answer = Byte.valueOf(stringValue);
            } catch (Exception conversionEx) {
                throw new ElementDefinitionException(String.format(CONVERSION_EXPLANATION_FORMAT, stringValue, attributeName, element.getTagName(), Byte.class), conversionEx);
            }
        }

        return answer;
    }

    /**
     * Return the {@link Short} value of an {@link Attr} in an {@link Element}.
     *
     * @param element          The source {@link Element}.
     * @param attributeName    Then name of the attribute the return the value of.
     * @param requireAttribute If true, the attribute must exist and its value must convertible to a {@link Short} value or an {@link ElementDefinitionException} will be thrown.
     *
     * @return The {@link Short} value of the attribute, or null if the attribute was not found in the element.
     *
     * @throws ElementDefinitionException Raised if requireAttribute is true and the attribute is not found in source {@link Element} or the attribute's value cannot be converted to a {@link Short}.
     */
    public static Short getShortAttribute(final Element element, final String attributeName, boolean requireAttribute) {
        String stringValue = getAttributeValue(element, attributeName, requireAttribute);

        Short answer = null;

        if (stringValue != null) {
            try {
                answer = Short.valueOf(stringValue);
            } catch (Exception conversionEx) {
                throw new ElementDefinitionException(String.format(CONVERSION_EXPLANATION_FORMAT, stringValue, attributeName, element.getTagName(), Short.class), conversionEx);
            }
        }

        return answer;
    }

    /**
     * Return the {@link Integer} value of an {@link Attr} in an {@link Element}.
     *
     * @param element          The source {@link Element}.
     * @param attributeName    Then name of the attribute the return the value of.
     * @param requireAttribute If true, the attribute must exist and its value must convertible to a {@link Integer} value or an {@link ElementDefinitionException} will be thrown.
     *
     * @return The {@link Integer} value of the attribute, or null if the attribute was not found in the element.
     *
     * @throws ElementDefinitionException Raised if requireAttribute is true and the attribute is not found in source {@link Element} or the attribute's value cannot be converted to a {@link Integer}.
     */
    public static Integer getIntegerAttribute(final Element element, final String attributeName, boolean requireAttribute) {
        String stringValue = getAttributeValue(element, attributeName, requireAttribute);

        Integer answer = null;

        if (stringValue != null) {
            try {
                answer = Integer.valueOf(stringValue);
            } catch (Exception conversionEx) {
                throw new ElementDefinitionException(String.format(CONVERSION_EXPLANATION_FORMAT, stringValue, attributeName, element.getTagName(), Integer.class), conversionEx);
            }
        }

        return answer;
    }

    /**
     * Return the {@link Long} value of an {@link Attr} in an {@link Element}.
     *
     * @param element          The source {@link Element}.
     * @param attributeName    Then name of the attribute the return the value of.
     * @param requireAttribute If true, the attribute must exist and its value must convertible to a {@link Long} value or an {@link ElementDefinitionException} will be thrown.
     *
     * @return The {@link Long} value of the attribute, or null if the attribute was not found in the element.
     *
     * @throws ElementDefinitionException Raised if requireAttribute is true and the attribute is not found in source {@link Element} or the attribute's value cannot be converted to a {@link Long}.
     */
    public static Long getLongAttribute(final Element element, final String attributeName, boolean requireAttribute) {
        String stringValue = getAttributeValue(element, attributeName, requireAttribute);

        Long answer = null;

        if (stringValue != null) {
            try {
                answer = Long.valueOf(stringValue);
            } catch (Exception conversionEx) {
                throw new ElementDefinitionException(String.format(CONVERSION_EXPLANATION_FORMAT, stringValue, attributeName, element.getTagName(), Long.class), conversionEx);
            }
        }

        return answer;
    }

    /**
     * Return the {@link Float} value of an {@link Attr} in an {@link Element}.
     *
     * @param element          The source {@link Element}.
     * @param attributeName    Then name of the attribute the return the value of.
     * @param requireAttribute If true, the attribute must exist and its value must convertible to a {@link Float} value or an {@link ElementDefinitionException} will be thrown.
     *
     * @return The {@link Float} value of the attribute, or null if the attribute was not found in the element.
     *
     * @throws ElementDefinitionException Raised if requireAttribute is true and the attribute is not found in source {@link Element} or the attribute's value cannot be converted to a {@link Float}.
     */
    public static Float getFloatAttribute(final Element element, final String attributeName, boolean requireAttribute) {
        String stringValue = getAttributeValue(element, attributeName, requireAttribute);

        Float answer = null;

        if (stringValue != null) {
            try {
                answer = Float.valueOf(stringValue);
            } catch (Exception conversionEx) {
                throw new ElementDefinitionException(String.format(CONVERSION_EXPLANATION_FORMAT, stringValue, attributeName, element.getTagName(), Float.class), conversionEx);
            }
        }

        return answer;
    }

    /**
     * Return the {@link Double} value of an {@link Attr} in an {@link Element}.
     *
     * @param element          The source {@link Element}.
     * @param attributeName    Then name of the attribute the return the value of.
     * @param requireAttribute If true, the attribute must exist and its value must convertible to a {@link Double} value or an {@link ElementDefinitionException} will be thrown.
     *
     * @return The {@link Double} value of the attribute, or null if the attribute was not found in the element.
     *
     * @throws ElementDefinitionException Raised if requireAttribute is true and the attribute is not found in source {@link Element} or the attribute's value cannot be converted to a {@link Double}.
     */
    public static Double getDoubleAttribute(final Element element, final String attributeName, boolean requireAttribute) {
        String stringValue = getAttributeValue(element, attributeName, requireAttribute);

        Double answer = null;

        if (stringValue != null) {
            try {
                answer = Double.valueOf(stringValue);
            } catch (Exception conversionEx) {
                throw new ElementDefinitionException(String.format(CONVERSION_EXPLANATION_FORMAT, stringValue, attributeName, element.getTagName(), Double.class), conversionEx);
            }
        }

        return answer;
    }
}

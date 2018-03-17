package com.pronoia.aries.blueprint.util.parser;

import com.pronoia.aries.blueprint.util.namespace.ElementDefinitionException;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;


/**
 * Tests for the  class.
 */
public abstract class ElementParserTestSupport {
    Element handledElement;
    Element subElement;

    @Before
    public void setUp() throws Exception {

        File fXmlFile = new File("src/test/resources/OSGI-INF/blueprint/simple-handler-blueprint.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
        doc.getDocumentElement().normalize();

        handledElement = (Element) doc.getDocumentElement().getElementsByTagName("simple-handler").item(0);
        subElement = (Element) doc.getDocumentElement().getElementsByTagName("sub-element").item(0);
    }

}
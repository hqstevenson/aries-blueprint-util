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

import com.pronoia.aries.blueprint.util.parser.ElementParser;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.aries.blueprint.ParserContext;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.blueprint.container.ComponentDefinitionException;
import org.osgi.service.blueprint.reflect.Metadata;
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
public class AbstractElementHandlerTest {
    static final String HANDLER_ELEMENT = "simple-handler";
    static final String SUB_ELEMENT = "sub-element";
    static final String SUB_ELEMENT_WITH_VALUE = "sub-element-with-value";
    static final String EMPTY_SUB_ELEMENT = "empty-sub-element";

    Element handler;
    Element sub;
    Element subWithValue;
    Element emptySub;

    AbstractElementHandler instance;

    @Before
    public void setUp() throws Exception {
        instance = new ElementHandlerStub();

        File fXmlFile = new File("src/test/resources/OSGI-INF/blueprint/simple-handler-blueprint.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
        doc.getDocumentElement().normalize();

        handler = (Element) doc.getDocumentElement().getElementsByTagName(HANDLER_ELEMENT).item(0);
        handler.toString();
        sub = (Element) handler.getElementsByTagName(SUB_ELEMENT).item(0);
        subWithValue = (Element) handler.getElementsByTagName(SUB_ELEMENT_WITH_VALUE).item(0);
        emptySub = (Element) handler.getElementsByTagName(EMPTY_SUB_ELEMENT).item(0);
    }





    static class ElementHandlerStub extends AbstractElementHandler {
        public ElementHandlerStub() {
            super(null, "fake-element");
        }

        @Override
        public Metadata createMetadata(ElementParser elementParser) {
            return null;
        }
    }
}
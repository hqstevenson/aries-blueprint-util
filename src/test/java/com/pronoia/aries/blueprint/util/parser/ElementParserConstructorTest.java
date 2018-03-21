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

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Tests for the  class.
 */
public class ElementParserConstructorTest extends ElementParserTestSupport {
    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testConstructorWithNullElement() throws Exception {
        try {
            new ElementParser(null);
            fail("Should have failed");
        } catch (IllegalArgumentException expectedEx) {
            assertEquals("ElementParser(element[null]) - the element cannot be null", expectedEx.getMessage());
        }

        try {
            new ElementParser(null, "blah");
            fail("Should have failed");
        } catch (IllegalArgumentException expectedEx) {
            assertEquals("ElementParser(parentElement[null], tagName[blah], occurrence[0]) - the parent element cannot be null", expectedEx.getMessage());
        }

        try {
            new ElementParser(null, "blah", 9999);
            fail("Should have failed");
        } catch (IllegalArgumentException expectedEx) {
            assertEquals("ElementParser(parentElement[null], tagName[blah], occurrence[9999]) - the parent element cannot be null", expectedEx.getMessage());
        }
    }


    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testConstructorWithTagName() throws Exception {
        new ElementParser(handledElement, "sub-element");

        try {
            new ElementParser(handledElement, "blah");
            fail("Should have failed");
        } catch (IllegalArgumentException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("ElementParser(parentElement[simple-handler], tagName[blah], occurrence[0]) - insufficient number of descendant elements [0] found in parent element {document = '"));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

        new ElementParser(handledElement, "sub-element", 0);

        // This one should generate a log warning
        new ElementParser(handledElement, "sub-element-with-value");

        // These two should not generate a log warning
        new ElementParser(handledElement, "sub-element-with-value", 0);
        new ElementParser(handledElement, "sub-element-with-value", 1);

        try {
            new ElementParser(handledElement, "blah", 9999);
            fail("Should have failed");
        } catch (IllegalArgumentException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("ElementParser(parentElement[simple-handler], tagName[blah], occurrence[9999]) - insufficient number of descendant elements [0] found in parent element {document = '"));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

        try {
            new ElementParser(handledElement, "sub-element", -1);
            fail("Should have failed");
        } catch (IllegalArgumentException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("ElementParser(parentElement[simple-handler], tagName[sub-element], occurrence[-1]) - occurrence cannot be less than zero {document = '"));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

        try {
            new ElementParser(handledElement, "sub-element", 9999);
            fail("Should have failed");
        } catch (IllegalArgumentException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("ElementParser(parentElement[simple-handler], tagName[sub-element], occurrence[9999]) - insufficient number of descendant elements [1] found in parent element {document = '"));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

        try {
            new ElementParser(subElement, "does-not-matter");
            fail("Should have failed");
        } catch (IllegalArgumentException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("ElementParser(parentElement[sub-element], tagName[does-not-matter], occurrence[0]) - descendant element not found in parent element {document = '"));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testConstructorWithBadTagName() throws Exception {
        new ElementParser(handledElement, "sub-element");

        try {
            new ElementParser(handledElement, null);
            fail("Should have failed");
        } catch (IllegalArgumentException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("ElementParser(parentElement[simple-handler], tagName[null], occurrence[0]) - the tagName cannot be null or empty {document = '"));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

        try {
            new ElementParser(handledElement, "");
            fail("Should have failed");
        } catch (IllegalArgumentException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("ElementParser(parentElement[simple-handler], tagName[], occurrence[0]) - the tagName cannot be null or empty {document = '"));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

        try {
            new ElementParser(handledElement, null, 9999);
            fail("Should have failed");
        } catch (IllegalArgumentException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("ElementParser(parentElement[simple-handler], tagName[null], occurrence[9999]) - the tagName cannot be null or empty {document = '"));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

        try {
            new ElementParser(handledElement, "", 9999);
            fail("Should have failed");
        } catch (IllegalArgumentException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("ElementParser(parentElement[simple-handler], tagName[], occurrence[9999]) - the tagName cannot be null or empty {document = '"));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

        try {
            new ElementParser(handledElement, "sub-element", -1);
            fail("Should have failed");
        } catch (IllegalArgumentException expectedEx) {
            assertThat(expectedEx.getMessage(), startsWith("ElementParser(parentElement[simple-handler], tagName[sub-element], occurrence[-1]) - occurrence cannot be less than zero {document = '"));
            assertThat(expectedEx.getMessage(), endsWith("simple-handler-blueprint.xml'}"));
        }

    }
}
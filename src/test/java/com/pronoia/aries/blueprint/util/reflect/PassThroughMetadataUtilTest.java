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
package com.pronoia.aries.blueprint.util.reflect;

import com.pronoia.aries.blueprint.util.reflect.PassThroughMetadataUtil;

import org.apache.aries.blueprint.mutable.MutablePassThroughMetadata;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


/**
 * Tests for the  class.
 */
public class PassThroughMetadataUtilTest {
    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testConstructor() throws Exception {
        assertNotNull(new PassThroughMetadataUtil());
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testCreateEmpty() throws Exception {
        MutablePassThroughMetadata actual = PassThroughMetadataUtil.create();

        assertNull(actual.getObject());
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testCreateWithObject() throws Exception {
        String expectedObject = "String Object";
        MutablePassThroughMetadata actual = PassThroughMetadataUtil.create(expectedObject);

        assertEquals(expectedObject, actual.getObject());
    }

}
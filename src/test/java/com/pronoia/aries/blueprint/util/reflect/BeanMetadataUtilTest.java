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

import com.pronoia.aries.blueprint.util.reflect.BeanMetadataUtil;

import java.util.List;

import org.apache.aries.blueprint.mutable.MutableBeanMetadata;
import org.apache.aries.blueprint.mutable.MutableValueMetadata;
import org.apache.aries.blueprint.reflect.MetadataUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osgi.service.blueprint.reflect.BeanArgument;
import org.osgi.service.blueprint.reflect.ValueMetadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


/**
 * Tests for the  class.
 */
public class BeanMetadataUtilTest {
    MutableBeanMetadata metadata;

    @Before
    public void setUp() throws Exception {
        metadata = MetadataUtil.createMetadata(MutableBeanMetadata.class);
    }

    @Test
    public void testConstructor() throws Exception {
        assertNotNull(new BeanMetadataUtil());
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testAddArgumentAsObject() throws Exception {
        Object value = "Argument Value";
        BeanMetadataUtil.addArgument(metadata, value, 0);


        MutableValueMetadata expectedArgumentValue = MetadataUtil.createMetadata(MutableValueMetadata.class);
        expectedArgumentValue.setType(value.getClass().getName());
        expectedArgumentValue.setStringValue(value.toString());

        List<BeanArgument> arguments = metadata.getArguments();
        assertEquals("Unexpected number of arguments", 1, arguments.size());

        for (BeanArgument argument : arguments) {
            assertEquals("Unexpected argument index", 0, argument.getIndex());
            assertEquals("Unexpected argument value type", value.getClass().getName(), argument.getValueType());

            ValueMetadata argumentValue = (ValueMetadata) argument.getValue();
            assertEquals("Unexpected argument value metadata value type", value.getClass().getName(), argumentValue.getType());
            assertEquals("Unexpected argument value metadata value", value.toString(), argumentValue.getStringValue());
        }
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Ignore("Test for testAddArgument as String method not yet implemented")
    @Test
    public void testAddArgumentAsString() throws Exception {
        fail("Test for testAddArgument as String method not yet implemented");
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Ignore("Test for addArguments method not yet implemented")
    @Test
    public void testAddArguments() throws Exception {
        fail("Test for addArguments method not yet implemented");
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Ignore("Test for addProperty method not yet implemented")
    @Test
    public void testAddProperty() throws Exception {
        fail("Test for addProperty method not yet implemented");
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Ignore("Test for addProperties method not yet implemented")
    @Test
    public void testAddProperties() throws Exception {
        fail("Test for addProperties method not yet implemented");
    }

}
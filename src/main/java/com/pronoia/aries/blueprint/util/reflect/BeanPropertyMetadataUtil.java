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

import org.apache.aries.blueprint.mutable.MutableBeanProperty;
import org.apache.aries.blueprint.mutable.MutableValueMetadata;
import org.apache.aries.blueprint.reflect.BeanPropertyImpl;
import org.apache.aries.blueprint.reflect.MetadataUtil;
import org.osgi.service.blueprint.reflect.Metadata;
import org.osgi.service.blueprint.reflect.ValueMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class providing helper-methods for creating Blueprint reflect.
 */
public class BeanPropertyMetadataUtil {
    static final Logger LOG = LoggerFactory.getLogger(BeanPropertyMetadataUtil.class);

    BeanPropertyMetadataUtil() {
    }

    /**
     * Create an Aries-specific mutable instance of the standard BeanProperty interface.
     *
     * @param propertyName the name of the property
     * @param propertyValue the value of the property as a String
     *
     * @return a new BeanProperty instance
     */
    public static <T> MutableBeanProperty create(String propertyName, T propertyValue) {
        MutableBeanProperty metadata = new BeanPropertyImpl();

        metadata.setName(propertyName);
        if (propertyValue instanceof Metadata) {
            metadata.setValue((Metadata) propertyValue);
        } else {
            metadata.setValue(ValueMetadataUtil.create(propertyValue));
        }

        return metadata;
    }

    /**
     * Create an Aries-specific mutable instance of the standard BeanProperty interface.
     *
     * @param propertyName the name of the property
     * @param propertyType  the type of the property value
     * @param propertyValue the value of the property as a String
     *
     * @return a new BeanProperty instance
     */
    public static MutableBeanProperty create(String propertyName, Class propertyType, String propertyValue) {
        MutableBeanProperty metadata = new BeanPropertyImpl();

        metadata.setName(propertyName);
        metadata.setValue(ValueMetadataUtil.create(propertyType, propertyValue));

        return metadata;
    }

}
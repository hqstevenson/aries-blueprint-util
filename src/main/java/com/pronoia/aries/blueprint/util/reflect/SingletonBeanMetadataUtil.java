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

import org.apache.aries.blueprint.mutable.MutableBeanMetadata;
import org.apache.aries.blueprint.reflect.MetadataUtil;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class providing helper-methods for createing Blueprint reflect.
 */
public class SingletonBeanMetadataUtil {
    static final Logger LOG = LoggerFactory.getLogger(SingletonBeanMetadataUtil.class);

    private SingletonBeanMetadataUtil() {
    }

    /**
     * Create an Aries-specific mutable instance of the standard BeanMetadata interface.
     *
     * @return a new reflect instance
     */
    public static MutableBeanMetadata create() {
        MutableBeanMetadata metadata = MetadataUtil.createMetadata(MutableBeanMetadata.class);

        metadata.setScope("singleton");
        metadata.setActivation(ComponentMetadata.ACTIVATION_LAZY);

        return metadata;
    }

    /**
     * Create an Aries-specific mutable instance of the standard BeanMetadata interface.
     *
     * @param beanClass the class for the bean
     *
     * @return a new reflect instance
     */
    public static MutableBeanMetadata create(Class beanClass) {
        MutableBeanMetadata metadata = create();

        metadata.setClassName(beanClass.getName());

        return metadata;
    }

    /**
     * Create an Aries-specific mutable instance of the standard BeanMetadata interface.
     *
     * @param beanId    the ID for the singleton bean
     * @param beanClass the class for the singleton bean
     *
     * @return a new reflect instance
     */
    public static MutableBeanMetadata create(Class beanClass, String beanId) {
        MutableBeanMetadata metadata = create(beanClass);

        metadata.setId(beanId);

        return metadata;
    }

    /**
     * Create an Aries-specific mutable instance of the standard BeanMetadata interface.
     *
     * @param beanClass     the class for the singleton bean
     * @param initMethod    the initialization method of the singleton bean
     * @param destroyMethod the destroy method of the singleton bean
     *
     * @return a new reflect instance
     */
    public static MutableBeanMetadata createWithLifecycleMethods(Class beanClass, String initMethod, String destroyMethod) {
        MutableBeanMetadata metadata = create(beanClass);

        if (initMethod != null && !initMethod.isEmpty()) {
            metadata.setInitMethod(initMethod);
        }

        if (destroyMethod != null && !destroyMethod.isEmpty()) {
            metadata.setDestroyMethod(destroyMethod);
        }

        return metadata;
    }

    /**
     * Create an Aries-specific mutable instance of the standard BeanMetadata interface.
     *
     * @param beanClass     the class for the singleton bean
     * @param beanId        the ID for the singleton bean
     * @param initMethod    the initialization method of the singleton bean
     * @param destroyMethod the destroy method of the singleton bean
     *
     * @return a new reflect instance
     */
    public static MutableBeanMetadata createWithLifecycleMethods(Class beanClass, String beanId, String initMethod, String destroyMethod) {
        MutableBeanMetadata metadata = create(beanClass);

        metadata.setId(beanId);
        if (initMethod != null && !initMethod.isEmpty()) {
            metadata.setInitMethod(initMethod);
        }

        if (destroyMethod != null && !destroyMethod.isEmpty()) {
            metadata.setDestroyMethod(destroyMethod);
        }

        return metadata;
    }

}
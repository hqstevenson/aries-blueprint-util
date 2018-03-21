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
package com.pronoia.aries.blueprint.util.metadata;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.osgi.service.blueprint.reflect.BeanArgument;
import org.osgi.service.blueprint.reflect.BeanMetadata;
import org.osgi.service.blueprint.reflect.BeanProperty;
import org.osgi.service.blueprint.reflect.Target;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractBeanMetadata implements BeanMetadata {
    public static final String DEFAULT_INIT_METHOD = "start";
    public static final String DEFAULT_DESTROY_METHOD = "stop";

    final Logger log = LoggerFactory.getLogger(this.getClass());

    final String className;

    protected String initMethod = null;
    protected String destroyMethod = null;
    protected List<BeanArgument> arguments = new LinkedList<>();

    protected String factoryMethod = null;
    protected Target factoryComponent = null;

    protected String scope = SCOPE_PROTOTYPE;
    protected String id;
    protected int activation = ACTIVATION_LAZY;
    protected List<String> dependsOn = new LinkedList<>();


    Map<String, String> attributeMap = new HashMap<>();

    protected AbstractBeanMetadata(String className) {
        this.className = className;
    }

    public abstract boolean usesAttribute(String attributeName);

    public abstract String getPropertyName(String name);

    public abstract BeanProperty getPropertyMetadata(String propertyName, String propertyValue);

    /**
     * The attribute argument may contain attributes that don't apply - ignore them.
     *
     * @param attributes
     */
    public void setAttributes(Map<String, String> attributes, boolean logIgnoredAttributes) {
        if (attributes != null && !attributes.isEmpty()) {
            for (String attributeName : attributes.keySet()) {
                if (attributeName.equals("id")) {
                    id = attributes.get("id");
                } else if (usesAttribute(attributeName)) {
                    attributeMap.put(attributeName, attributes.get(attributeName));
                } else if (logIgnoredAttributes) {
                    switch (attributeName) {
                        case "xmlns":
                            // Don't need to log this one
                            break;
                        default:
                            log.warn("Ignoring attribute {} = {}", attributeName, attributes.get(attributeName));
                    }
                }
            }
        }
    }

    public boolean hasAttribute(String attributeName) {
        return attributeMap.containsKey(attributeName);
    }

    @Override
    public String getClassName() {
        if (className == null) {
            // TODO:  Add more detail to exception message
            throw new IllegalStateException("Class name must be specified");
        }
        return className;
    }

    @Override
    public String getInitMethod() {
        return initMethod;
    }

    @Override
    public String getDestroyMethod() {
        return destroyMethod;
    }

    @Override
    public List<BeanArgument> getArguments() {
        return arguments;
    }


    @Override
    public List<BeanProperty> getProperties() {
        List<BeanProperty> answer = new LinkedList<>();

        for (Map.Entry<String, String> attribute : attributeMap.entrySet()) {
            String propertyName = getPropertyName(attribute.getKey());

            if (propertyName != null && !propertyName.isEmpty()) {
                answer.add(getPropertyMetadata(propertyName, attribute.getValue()));
            }
        }

        return answer;
    }

    @Override
    public String getFactoryMethod() {
        return factoryMethod;
    }

    @Override
    public Target getFactoryComponent() {
        return factoryComponent;
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public String getId() {
        if (id == null || id.isEmpty()) {
            id = this.getClass().getSimpleName() + "-" + System.currentTimeMillis();
        }

        return id;
    }

    @Override
    public int getActivation() {
        return activation;
    }

    @Override
    public List<String> getDependsOn() {
        return dependsOn;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " {" +
               "className='" + className + '\'' +
               ", initMethod='" + initMethod + '\'' +
               ", destroyMethod='" + destroyMethod + '\'' +
               ", arguments=" + arguments +
               ", factoryMethod='" + factoryMethod + '\'' +
               ", factoryComponent=" + factoryComponent +
               ", scope='" + scope + '\'' +
               ", id='" + id + '\'' +
               ", activation=" + activation +
               ", dependsOn=" + dependsOn +
               ", attributeMap=" + attributeMap +
               '}';
    }
}

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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.aries.blueprint.reflect.BeanMetadataImpl;
import org.osgi.service.blueprint.reflect.Metadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractBeanMetadata extends BeanMetadataImpl {
    public static final String DEFAULT_INIT_METHOD = "start";
    public static final String DEFAULT_DESTROY_METHOD = "stop";

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected AbstractBeanMetadata(String className) {
        this.setClassName(className);
    }

    protected AbstractBeanMetadata(Class clazz) {
        this.setClassName(clazz.getName());
    }

    public abstract String translatePropertyName(String name);

    public abstract Metadata createPropertyMetadata(String propertyName, String propertyValue);

    /**
     * The attribute argument may contain attributes that don't apply - ignore them.
     *
     * @param properties
     */
    public void addProperties(Map<String, String> properties, boolean logIgnoredProperties) {
        if (properties != null && !properties.isEmpty()) {
            for (String propertyName : properties.keySet()) {
                String value = properties.get(propertyName);
                if (propertyName.equals("id")) {
                    this.setId(value);
                } else if (propertyName.equals("depends-on")) {
                    this.setDependsOn(parseDependsOnString(properties.get("depends-on")));
                } else {
                    String translatedPropertyName = translatePropertyName(propertyName);
                    if (translatedPropertyName != null && !translatedPropertyName.isEmpty()) {
                        addProperty(translatedPropertyName, createPropertyMetadata(translatedPropertyName, value));
                    } else if (logIgnoredProperties) {
                        switch (propertyName) {
                        case "xmlns":
                            // Don't need to log this one
                            break;
                        default:
                            log.warn("Ignoring {} = {}", propertyName, properties.get(propertyName));
                        }
                    }
                }
            }
        }
    }

    public static List<String> parseDependsOnString(String dependsOnString) {
        List<String> answer = new LinkedList<>();

        if (dependsOnString != null && !dependsOnString.isEmpty()) {
            String[] dependsOnIds = dependsOnString.split(",");
            for (String dependsOnId : dependsOnIds) {
                answer.add(dependsOnId.trim());
            }
        }
        return answer;
    }
}

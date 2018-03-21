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

import com.pronoia.aries.blueprint.util.reflect.ValueMetadataUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.aries.blueprint.mutable.MutableMapEntry;
import org.apache.aries.blueprint.reflect.MapEntryImpl;

import org.osgi.service.blueprint.reflect.MapEntry;
import org.osgi.service.blueprint.reflect.RegistrationListener;
import org.osgi.service.blueprint.reflect.ServiceMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractServiceMetadata implements ServiceMetadata {
    final Logger log = LoggerFactory.getLogger(this.getClass());

    protected String id;
    protected int activation = ACTIVATION_LAZY;
    protected int autoExport = AUTO_EXPORT_DISABLED;
    protected int ranking = 0;
    protected List<String> dependsOn = new LinkedList<>();

    protected List<String> interfaces = new LinkedList<>();
    protected Map<String, String> serviceProperties = new HashMap<>();
    protected Collection<RegistrationListener> registrationListeners = new LinkedList<>();

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

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public List<String> getInterfaces() {
        return interfaces;
    }

    @Override
    public int getAutoExport() {
        return autoExport;
    }

    @Override
    public List<MapEntry> getServiceProperties() {
        List<MapEntry> answer = new LinkedList<>();

        if (serviceProperties != null && !serviceProperties.isEmpty()) {
            for (Map.Entry<String, String> serviceProperty : serviceProperties.entrySet()) {
                MutableMapEntry entry = new MapEntryImpl();

                entry.setKey(ValueMetadataUtil.create(String.class, serviceProperty.getKey()));
                entry.setValue(ValueMetadataUtil.create(String.class, serviceProperty.getValue()));

                answer.add(entry);
            }
        }

        return answer;
    }

    @Override
    public int getRanking() {
        return ranking;
    }

    @Override
    public Collection<RegistrationListener> getRegistrationListeners() {
        return registrationListeners;
    }
}

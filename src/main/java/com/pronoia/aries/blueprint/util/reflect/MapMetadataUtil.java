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

import java.util.Map;

import org.apache.aries.blueprint.mutable.MutableMapMetadata;
import org.apache.aries.blueprint.mutable.MutableValueMetadata;
import org.apache.aries.blueprint.reflect.MetadataUtil;

import org.osgi.service.blueprint.container.ComponentDefinitionException;
import org.osgi.service.blueprint.reflect.MapMetadata;
import org.osgi.service.blueprint.reflect.Metadata;
import org.osgi.service.blueprint.reflect.NullMetadata;
import org.osgi.service.blueprint.reflect.NonNullMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class providing helper-methods for createing Blueprint reflect.
 */
public class MapMetadataUtil {
    static final Logger LOG = LoggerFactory.getLogger(MapMetadataUtil.class);

    private MapMetadataUtil() {
    }

    /**
     * Create an un-configured Aries-specific {@link MutableMapMetadata} instance of the standard OSGi {@link MapMetadata} interface.
     *
     * @return a new metadata instance
     */
    public static MutableMapMetadata create() {
        MutableMapMetadata metadata = MetadataUtil.createMetadata(MutableMapMetadata.class);

        return metadata;
    }

    /**
     * Create a configured Aries-specific {@link MutableMapMetadata} instance of the standard OSGi {@link MapMetadata} interface.
     *
     * @param keyType   the type of keys the Map will contain
     * @param valueType the type of values the Map will contain
     *
     * @return a new metadata instance
     */
    public static MutableMapMetadata create(Class keyType, Class valueType) {
        MutableMapMetadata metadata = create();

        metadata.setKeyType(keyType.getName());
        metadata.setValueType(valueType.getName());

        return metadata;
    }

    /**
     * Create a configured Aries-specific {@link MutableMapMetadata} instance of the standard @{link MapMetadata} interface containing metadata for the provided values.
     *
     * @param keyType   the type of keys the Map will contain
     * @param valueType the type of values the Map will contain
     * @param values the values the for which metadata will be generated
     *
     * @return a new metadata instance
     */
    public static MutableMapMetadata create(Class keyType, Class valueType, Map values) {
        MutableMapMetadata mapMetadata = create(keyType, valueType);

        addValues(mapMetadata, values);

        return mapMetadata;
    }


    /**
     * Create a configured Aries-specific {@link MutableMapMetadata} instance of the standard @{link MapMetadata} interface containing metadata for the provided values.
     *
     * NOTE:  Use this with caution.  The types for the Map key and Map value will be determined from the first values
     * returned when iterating over the map.  If generic/base types are used, using this will result in incorrect metadata.
     *
     * @param values the values the for which metadata will be generated
     *
     * @return a new metadata instance
     */
    public static MutableMapMetadata create(Map values) {
        MutableMapMetadata mapMetadata = create();

        // TODO: Figure out a better way to set the list value type
        if (values != null && !values.isEmpty()) {
            Class keyType = null;
            Class valueType = null;

            for (Object key : values.keySet()) {
                if (keyType == null) {
                    keyType = key.getClass();
                    mapMetadata.setKeyType(keyType.getName());
                }

                Object value = values.get(key);
                if (valueType == null && value != null) {
                    valueType = value.getClass();
                    mapMetadata.setValueType(valueType.getName());
                }

                addValue(mapMetadata, key, value);
            }
        }

        return mapMetadata;
    }


    /**
     * Add values to an Aries-specific mutable instance of the standard CollectionMetadata interface for a Map.
     *
     * @param mapMetadata The {@link MutableMapMetadata} to add an entry.
     * @param key The new entry key
     * @param value The new entry value
     */
    public static void addValue(MutableMapMetadata mapMetadata, Object key, Object value) {
        if (mapMetadata == null) {
            throw new ComponentDefinitionException("Cannot add values to a null MutableMapMetadata");
        }

        if (key != null) {
            NonNullMetadata keyMetadata;
            if (key instanceof NonNullMetadata) {
                keyMetadata = (NonNullMetadata) key;
            } else {
                MutableValueMetadata tmpKeyMetadata = MetadataUtil.createMetadata(MutableValueMetadata.class);

                tmpKeyMetadata.setType(key.getClass().getName());
                tmpKeyMetadata.setStringValue(key.toString());

                keyMetadata = tmpKeyMetadata;
            }

            if (value != null) {
                if (value instanceof Metadata) {
                    mapMetadata.addEntry(keyMetadata, (Metadata) value);
                } else {
                    MutableValueMetadata tmpValueMetadata = MetadataUtil.createMetadata(MutableValueMetadata.class);

                    tmpValueMetadata.setType(value.getClass().getName());
                    tmpValueMetadata.setStringValue(value.toString());

                    mapMetadata.addEntry(keyMetadata, tmpValueMetadata);
                }
            } else {
                mapMetadata.addEntry(keyMetadata, MetadataUtil.createMetadata(NullMetadata.class));
            }
        }
    }

    /**
     * Add values to an Aries-specific mutable instance of the standard CollectionMetadata interface for a Map.
     *
     * @param mapMetadata The {@link MutableMapMetadata} to add an entries.
     * @param values The map of entries to add.
     */
    public static void addValues(MutableMapMetadata mapMetadata, Map values) {
        if (mapMetadata == null) {
            throw new ComponentDefinitionException("Cannot add values to a null MutableMapMetadata");
        }

        if (values != null && !values.isEmpty()) {
            for (Object key : values.keySet()) {
                addValue(mapMetadata, key, values.get(key));
            }

        }
    }
}
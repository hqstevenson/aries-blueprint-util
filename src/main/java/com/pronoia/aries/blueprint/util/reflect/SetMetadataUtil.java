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

import java.util.Collection;
import java.util.Set;

import org.apache.aries.blueprint.mutable.MutableCollectionMetadata;
import org.apache.aries.blueprint.mutable.MutableValueMetadata;
import org.apache.aries.blueprint.reflect.MetadataUtil;
import org.osgi.service.blueprint.container.ComponentDefinitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class providing helper-methods for createing Blueprint reflect.
 */
public class SetMetadataUtil {
    static final Logger LOG = LoggerFactory.getLogger(SetMetadataUtil.class);

    private SetMetadataUtil() {
    }

    /**
     * Create an Aries-specific mutable instance of the standard CollectionMetadata interface for a Set.
     *
     * @return a new reflect instance
     */
    public static MutableCollectionMetadata create() {
        MutableCollectionMetadata metadata = MetadataUtil.createMetadata(MutableCollectionMetadata.class);
        metadata.setCollectionClass(Set.class);

        return metadata;
    }

    /**
     * Create an Aries-specific mutable instance of the standard CollectionMetadata interface for a Set.
     *
     * @param valueType the type of values the Set will contain
     *
     * @return a new reflect instance
     */
    public static MutableCollectionMetadata create(Class valueType) {
        MutableCollectionMetadata metadata = create();
        metadata.setValueType(valueType.getName());

        return metadata;
    }

    /**
     * Create an Aries-specific mutable instance of the standard CollectionMetadata interface for a Set.
     *
     * @param values the values the List will contain
     *
     * @return a new reflect instance
     */
    public static <T> MutableCollectionMetadata create(T... values) {
        MutableCollectionMetadata setMetadata = create();

        if (values != null) {
            setMetadata.setValueType(values.getClass().getComponentType().getName());
            if (values != null && values.length > 0) {
                setMetadata.setValueType(values[0].getClass().getName());

                for (T value : values) {
                    MutableValueMetadata valueMetadata = MetadataUtil.createMetadata(MutableValueMetadata.class);
                    valueMetadata.setType(value.getClass().getName());
                    valueMetadata.setStringValue(value.toString());

                    setMetadata.addValue(valueMetadata);
                }
            }
        }

        return setMetadata;
    }

    /**
     * Create an Aries-specific mutable instance of the standard CollectionMetadata interface for a List.
     *
     * @param values the values the List will contain
     *
     * @return a new reflect instance
     */
    public static <T> MutableCollectionMetadata create(Collection<T> values) {
        MutableCollectionMetadata setMetadata = create();


        // TODO: Figure out a better way to set the list value type
        if (values != null && values.size() > 0) {
            Class valueType = null;

            for (T value : values) {
                if (valueType == null) {
                    valueType = value.getClass();
                }
                MutableValueMetadata valueMetadata = MetadataUtil.createMetadata(MutableValueMetadata.class);
                valueMetadata.setType(value.getClass().getName());
                valueMetadata.setStringValue(value.toString());

                setMetadata.addValue(valueMetadata);
            }

            setMetadata.setValueType(valueType.getName());
        }

        return setMetadata;
    }

    /**
     * Add a value to an Aries-specific mutable instance of the standard CollectionMetadata interface for a Set.
     *
     * @param value the value to add
     */
    public static void addValue(MutableCollectionMetadata setMetadata, Object value) {
        if (setMetadata == null) {
            throw new ComponentDefinitionException("Cannot add values to a null MutableCollectionMetadata");
        }

        if (value != null) {
            MutableValueMetadata valueMetadata = MetadataUtil.createMetadata(MutableValueMetadata.class);
            valueMetadata.setType(value.getClass().getName());
            valueMetadata.setStringValue(value.toString());

            setMetadata.addValue(valueMetadata);
        }
    }
}
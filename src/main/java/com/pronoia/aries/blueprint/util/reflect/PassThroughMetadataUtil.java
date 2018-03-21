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

import org.apache.aries.blueprint.mutable.MutablePassThroughMetadata;
import org.apache.aries.blueprint.reflect.MetadataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class providing helper-methods for createing Blueprint reflect.
 */
public class PassThroughMetadataUtil {
    static final Logger LOG = LoggerFactory.getLogger(PassThroughMetadataUtil.class);

    PassThroughMetadataUtil() {
    }

    /**
     * Create an Aries-specific mutable instance of the standard ValueMetadata interface.
     *
     * @return a new reflect instance
     */
    public static MutablePassThroughMetadata create() {
        MutablePassThroughMetadata metadata = MetadataUtil.createMetadata(MutablePassThroughMetadata.class);
        return metadata;
    }

    /**
     * Create an Aries-specific mutable instance of the standard ValueMetadata interface.
     *
     * @param value the value of the reflect
     *
     * @return a new reflect instance
     */
    public static MutablePassThroughMetadata create(Object value) {
        MutablePassThroughMetadata metadata = create();
        metadata.setObject(value);
        return metadata;
    }

}
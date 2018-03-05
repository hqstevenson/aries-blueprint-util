package com.pronoia.aries.blueprint.util.metadata;

import java.util.Map;

import org.apache.aries.blueprint.mutable.MutableMapMetadata;
import org.apache.aries.blueprint.mutable.MutableValueMetadata;
import org.apache.aries.blueprint.reflect.MetadataUtil;
import org.osgi.service.blueprint.container.ComponentDefinitionException;
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
     * Create an Aries-specific mutable instance of the standard MapMetadata interface for a Map.
     *
     * @return a new reflect instance
     */
    public static MutableMapMetadata create() {
        MutableMapMetadata metadata = MetadataUtil.createMetadata(MutableMapMetadata.class);

        return metadata;
    }

    /**
     * Create an Aries-specific mutable instance of the standard MapMetadata interface for a Map.
     *
     * @param keyType   the type of keys the Map will contain
     * @param valueType the type of values the Map will contain
     *
     * @return a new reflect instance
     */
    public static MutableMapMetadata create(Class keyType, Class valueType) {
        MutableMapMetadata metadata = create();
        metadata.setKeyType(keyType.getName());
        metadata.setValueType(valueType.getName());

        return metadata;
    }

    /**
     * Create an Aries-specific mutable instance of the standard CollectionMetadata interface for a List.
     *
     * @param values the values the List will contain
     *
     * @return a new reflect instance
     */
    public static MutableMapMetadata create(Map values) {
        MutableMapMetadata mapMetadata = create();

        // TODO: Figure out a better way to set the list value type

        if (values != null && !values.isEmpty()) {

            for (Object key : values.keySet()) {
                Object value = values.get(key);

                MutableValueMetadata keyMetadata = MetadataUtil.createMetadata(MutableValueMetadata.class);
                keyMetadata.setType(key.getClass().getName());
                keyMetadata.setStringValue(key.toString());

                MutableValueMetadata valueMetadata = MetadataUtil.createMetadata(MutableValueMetadata.class);
                valueMetadata.setType(value.getClass().getName());
                valueMetadata.setStringValue(value.toString());

                mapMetadata.addEntry(keyMetadata, valueMetadata);
            }

        }

        return mapMetadata;
    }

    /**
     * Add values to an Aries-specific mutable instance of the standard CollectionMetadata interface for a Map.
     *
     * @param value the values the List will contain
     *
     * @return a new reflect instance
     */
    public static void addValue(MutableMapMetadata mapMetadata, Object key, Object value) {
        if (mapMetadata == null) {
            throw new ComponentDefinitionException("Cannot add values to a null MutableMapMetadata");
        }

        if (key != null) {
            MutableValueMetadata keyMetadata = MetadataUtil.createMetadata(MutableValueMetadata.class);
            keyMetadata.setType(key.getClass().getName());
            keyMetadata.setStringValue(key.toString());

            MutableValueMetadata valueMetadata = MetadataUtil.createMetadata(MutableValueMetadata.class);
            valueMetadata.setType(value.getClass().getName());
            valueMetadata.setStringValue(value.toString());

            mapMetadata.addEntry(keyMetadata, valueMetadata);
        }
    }

    /**
     * Add values to an Aries-specific mutable instance of the standard CollectionMetadata interface for a Map.
     *
     * @param values the values the List will contain
     *
     * @return a new reflect instance
     */
    public static void addValues(MutableMapMetadata mapMetadata, Map values) {
        if (mapMetadata == null) {
            throw new ComponentDefinitionException("Cannot add values to a null MutableMapMetadata");
        }

        if (values != null && !values.isEmpty()) {
            Class keyType = null;
            Class valueType = null;

            for (Object key : values.keySet()) {
                Object value = values.get(key);

                if (keyType == null) {
                    keyType = key.getClass();
                    valueType = value.getClass();
                    mapMetadata.setKeyType(keyType.getName());
                    mapMetadata.setValueType(valueType.getName());
                }

                MutableValueMetadata keyMetadata = MetadataUtil.createMetadata(MutableValueMetadata.class);
                keyMetadata.setType(key.getClass().getName());
                keyMetadata.setStringValue(key.toString());

                MutableValueMetadata valueMetadata = MetadataUtil.createMetadata(MutableValueMetadata.class);
                valueMetadata.setType(value.getClass().getName());
                valueMetadata.setStringValue(value.toString());

                mapMetadata.addEntry(keyMetadata, valueMetadata);
            }

        }
    }
}
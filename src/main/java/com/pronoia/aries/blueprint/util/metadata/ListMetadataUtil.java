package com.pronoia.aries.blueprint.util.metadata;

import java.util.Collection;
import java.util.List;

import org.apache.aries.blueprint.mutable.MutableCollectionMetadata;
import org.apache.aries.blueprint.mutable.MutableValueMetadata;
import org.apache.aries.blueprint.reflect.MetadataUtil;
import org.osgi.service.blueprint.container.ComponentDefinitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class providing helper-methods for createing Blueprint reflect.
 */
public class ListMetadataUtil {
    static final Logger LOG = LoggerFactory.getLogger(ListMetadataUtil.class);

    private ListMetadataUtil() {
    }


    /**
     * Create an Aries-specific mutable instance of the standard CollectionMetadata interface for a List.
     *
     * @return a new reflect instance
     */
    public static MutableCollectionMetadata create() {
        MutableCollectionMetadata metadata = MetadataUtil.createMetadata(MutableCollectionMetadata.class);
        metadata.setCollectionClass(List.class);

        return metadata;
    }

    /**
     * Create an Aries-specific mutable instance of the standard CollectionMetadata interface for a List.
     *
     * @param valueType the type of values the List will contain
     *
     * @return a new reflect instance
     */
    public static MutableCollectionMetadata create(Class valueType) {
        MutableCollectionMetadata metadata = create();
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
    public static <T> MutableCollectionMetadata create(T... values) {
        MutableCollectionMetadata listMetadata = create();

        if (values != null) {
            listMetadata.setValueType(values.getClass().getComponentType().getName());
            if (values.length > 0) {
                listMetadata.setValueType(values[0].getClass().getName());

                for (T value : values) {
                    MutableValueMetadata valueMetadata = MetadataUtil.createMetadata(MutableValueMetadata.class);
                    valueMetadata.setType(value.getClass().getName());
                    valueMetadata.setStringValue(value.toString());

                    listMetadata.addValue(valueMetadata);
                }
            }
        }

        return listMetadata;
    }

    /**
     * Create an Aries-specific mutable instance of the standard CollectionMetadata interface for a List.
     *
     * @param values the values the List will contain
     *
     * @return a new reflect instance
     */
    public static <T> MutableCollectionMetadata create(Collection<T> values) {
        MutableCollectionMetadata listMetadata = create();

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

                listMetadata.addValue(valueMetadata);
            }

            listMetadata.setValueType(valueType.getName());
        }


        return listMetadata;
    }

    /**
     * Add a value to an Aries-specific mutable instance of the standard CollectionMetadata interface for a List.
     *
     * @param value the value to add
     */
    public static void addValue(MutableCollectionMetadata listMetadata, Object value) {
        if (listMetadata == null) {
            throw new ComponentDefinitionException("Cannot add values to a null MutableCollectionMetadata");
        }

        if (value != null) {
            MutableValueMetadata valueMetadata = MetadataUtil.createMetadata(MutableValueMetadata.class);
            valueMetadata.setType(value.getClass().getName());
            valueMetadata.setStringValue(value.toString());

            listMetadata.addValue(valueMetadata);
        }
    }
}
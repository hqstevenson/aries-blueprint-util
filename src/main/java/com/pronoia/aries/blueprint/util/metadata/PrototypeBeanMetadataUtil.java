package com.pronoia.aries.blueprint.util.metadata;

import org.apache.aries.blueprint.mutable.MutableBeanMetadata;
import org.apache.aries.blueprint.reflect.MetadataUtil;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class providing helper-methods for createing Blueprint reflect.
 */
public class PrototypeBeanMetadataUtil {
    static final Logger LOG = LoggerFactory.getLogger(PrototypeBeanMetadataUtil.class);

    private PrototypeBeanMetadataUtil() {
    }

    /**
     * Create an Aries-specific mutable instance of the standard BeanMetadata interface.
     *
     * @return a new reflect instance
     */
    public static MutableBeanMetadata create() {
        MutableBeanMetadata metadata = MetadataUtil.createMetadata(MutableBeanMetadata.class);

        metadata.setScope("prototype");
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
     * @param beanId    the ID for the prototype bean
     * @param beanClass the class for the prototype bean
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
     * @param beanClass  the class for the prototype bean
     * @param initMethod the initialization method of the singleton bean
     *
     * @return a new reflect instance
     */
    public static MutableBeanMetadata createWithLifecycleMethods(Class beanClass, String initMethod) {
        MutableBeanMetadata metadata = create(beanClass);

        if (initMethod != null && !initMethod.isEmpty()) {
            metadata.setInitMethod(initMethod);
        }

        return metadata;
    }

    /**
     * Create an Aries-specific mutable instance of the standard BeanMetadata interface.
     *
     * @param beanClass  the class for the prototype bean
     * @param beanId     the ID for the prototype bean
     * @param initMethod the initialization method of the singleton bean
     *
     * @return a new reflect instance
     */
    public static MutableBeanMetadata createWithLifecycleMethods(Class beanClass, String beanId, String initMethod) {
        MutableBeanMetadata metadata = create(beanClass);

        metadata.setId(beanId);
        if (initMethod != null && !initMethod.isEmpty()) {
            metadata.setInitMethod(initMethod);
        }

        return metadata;
    }

}
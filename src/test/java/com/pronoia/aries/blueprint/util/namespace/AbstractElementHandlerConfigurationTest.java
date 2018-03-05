package com.pronoia.aries.blueprint.util.namespace;

import com.pronoia.aries.blueprint.util.metadata.PrototypeBeanMetadataUtil;

import org.apache.aries.blueprint.ParserContext;
import org.apache.aries.blueprint.mutable.MutableBeanMetadata;
import org.junit.Before;
import org.junit.Test;
// import org.osgi.framework.BundleContext;
// import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.service.blueprint.container.ComponentDefinitionException;
import org.osgi.service.blueprint.reflect.Metadata;
import org.osgi.service.blueprint.reflect.RefMetadata;
import org.w3c.dom.Element;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 * Tests for the  class.
 */
public class AbstractElementHandlerConfigurationTest {
    AbstractElementHandler instance;

    @Before
    public void setUp() throws Exception {
        instance = new ElementHandlerStub();
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testConstructor() throws Exception {
        try {
            new ElementHandlerStub(null);
            fail("Constructor should have thrown an exception");
        } catch (IllegalArgumentException expectedEx) {
            // TODO: verify exception message
        }

        try {
            new ElementHandlerStub("");
            fail("Constructor should have thrown an exception");
        } catch (IllegalArgumentException expectedEx) {
            // TODO: verify exception message
        }
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testGetElementName() throws Exception {
        assertEquals("Unexpected default value", ElementHandlerStub.ELEMENT_NAME, instance.getElementName());

    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testAddBlueprintArguments() throws Exception {
        MutableBeanMetadata metadata = PrototypeBeanMetadataUtil.create();

        instance.addBlueprintArguments(metadata);

        assertEquals("Unexpected index for argument 0", 0, metadata.getArguments().get(0).getIndex());
        assertEquals("Unexpected valueType for argument 0", "org.osgi.framework.BundleContext", metadata.getArguments().get(0).getValueType());
        assertEquals("Unexpected component id for argument 0", "blueprintBundleContext", ((RefMetadata) metadata.getArguments().get(0).getValue()).getComponentId());

        assertEquals("Unexpected index for argument 1", 1, metadata.getArguments().get(1).getIndex());
        assertEquals("Unexpected valueType for argument 1", "org.osgi.service.blueprint.container.BlueprintContainer", metadata.getArguments().get(1).getValueType());
        assertEquals("Unexpected component id for argument 0", "blueprintContainer", ((RefMetadata) metadata.getArguments().get(1).getValue()).getComponentId());
    }

    static class ElementHandlerStub extends AbstractElementHandler {
        public static final String ELEMENT_NAME = "fake-element";

        public ElementHandlerStub() {
            super(ELEMENT_NAME);
        }

        public ElementHandlerStub(String elementName) {
            super(elementName);
        }

        @Override
        public Metadata parseElement(Element serviceElement, ParserContext parserContext) {
            return null;
        }
    }

}
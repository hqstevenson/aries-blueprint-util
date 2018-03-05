package com.pronoia.aries.blueprint.util.metadata;

import org.apache.aries.blueprint.mutable.MutablePassThroughMetadata;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


/**
 * Tests for the  class.
 */
public class PassThroughMetadataUtilTest {
    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testConstructor() throws Exception {
        assertNotNull(new PassThroughMetadataUtil());
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testCreateEmpty() throws Exception {
        MutablePassThroughMetadata actual = PassThroughMetadataUtil.create();

        assertNull(actual.getObject());
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testCreateWithObject() throws Exception {
        String expectedObject = "String Object";
        MutablePassThroughMetadata actual = PassThroughMetadataUtil.create(expectedObject);

        assertEquals(expectedObject, actual.getObject());
    }

}
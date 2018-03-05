package com.pronoia.aries.blueprint.util.metadata;

import org.apache.aries.blueprint.mutable.MutableValueMetadata;
import org.apache.aries.blueprint.reflect.MetadataUtil;
import org.junit.Test;
import org.osgi.service.blueprint.reflect.ValueMetadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Tests for the  class.
 */
public class ValueMetadataUtilTest {
    public static void assertMetadataEquals(ValueMetadata expected, ValueMetadata actual) {
        assertEquals("ValueMetadata.getType does not match", expected.getType(), actual.getType());
        assertEquals("ValueMetadata.getStringValue does not match", expected.getStringValue(), actual.getStringValue());
    }

    @Test
    public void testConstructor() throws Exception {
        assertNotNull(new ValueMetadataUtil());
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testCreateFromStringObject() throws Exception {
        String stringValue = "Test String Value";
        Object value = new String(stringValue);

        MutableValueMetadata expected = MetadataUtil.createMetadata(MutableValueMetadata.class);
        expected.setType(value.getClass().getName());
        expected.setStringValue(stringValue);

        assertMetadataEquals(expected, ValueMetadataUtil.create(value));
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testCreateFromIntegerObject() throws Exception {
        String stringValue = "123";
        Object value = Integer.valueOf(stringValue);

        MutableValueMetadata expected = MetadataUtil.createMetadata(MutableValueMetadata.class);
        expected.setType(value.getClass().getName());
        expected.setStringValue(stringValue);

        assertMetadataEquals(expected, ValueMetadataUtil.create(value));
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testCreateFromStringValue() throws Exception {
        String stringValue = "Test String Value";

        MutableValueMetadata expected = MetadataUtil.createMetadata(MutableValueMetadata.class);
        expected.setType(String.class.getName());
        expected.setStringValue(stringValue);

        assertMetadataEquals(expected, ValueMetadataUtil.create(String.class, stringValue));
    }

    /**
     * Description of test.
     *
     * @throws Exception in the event of a test error.
     */
    @Test
    public void testCreateFromIntegerStringValue() throws Exception {
        String stringValue = "123";

        MutableValueMetadata expected = MetadataUtil.createMetadata(MutableValueMetadata.class);
        expected.setType(Integer.class.getName());
        expected.setStringValue(stringValue);

        assertMetadataEquals(expected, ValueMetadataUtil.create(Integer.class, stringValue));
    }
}
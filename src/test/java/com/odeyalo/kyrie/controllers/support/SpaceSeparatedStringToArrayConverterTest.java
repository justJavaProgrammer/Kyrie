package com.odeyalo.kyrie.controllers.support;

import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for SpaceSeparatedStringToArrayConverter
 */

class SpaceSeparatedStringToArrayConverterTest {
    /**
     * Initialize converter with DefaultConversionService that contains custom converters
     */
    private final SpaceSeparatedStringToArrayConverter converter = new SpaceSeparatedStringToArrayConverter(
            new DefaultConversionService() {{
                addConverter(new String2AuthorizationGrantTypeConverter());
                addConverter(new String2ResponseTypeConverter());
            }}
    );
    private static final String EMPTY_STRING = "";
    private static final String SPACE_SEPARATED_STRING_1_ELEMENT = "H_ELEMENT1";
    private static final String SPACE_SEPARATED_STRING_2_ELEMENTS = "E_ELEMENT1 ELEMENT2";
    private static final String SPACE_SEPARATED_STRING_3_ELEMENTS = "L_ELEMENT1 ELEMENT2 ELEMENT3";
    private static final String SPACE_SEPARATED_STRING_4_ELEMENTS = "P_ELEMENT1 ELEMENT2 ELEMENT3 ELEMENT4";
    private static final String SPACE_SEPARATED_OAUTH2_RESPONSE_TYPE_CLASS_2_ELEMENTS = "code token";


    @Test
    void matches() {
        boolean stringObjectMatches = converter.matches(TypeDescriptor.valueOf(String.class), TypeDescriptor.array(TypeDescriptor.valueOf(Object.class)));
        assertTrue(stringObjectMatches);
        boolean objectStringMatches = converter.matches(TypeDescriptor.valueOf(Object.class), TypeDescriptor.array(TypeDescriptor.valueOf(String.class)));
        assertFalse(objectStringMatches);
    }

    @Test
    @DisplayName("Test 'getConvertibleTypes' method")
    void testGetConvertibleTypes() {
        Set<GenericConverter.ConvertiblePair> convertibleTypes = converter.getConvertibleTypes();
        assertNotNull(convertibleTypes, "getConvertibleTypes can't return null as result");
        assertNotEquals(0, convertibleTypes.size(), "getConvertibleTypes can't return Set with 0 elements");
        boolean contains = convertibleTypes.contains(new GenericConverter.ConvertiblePair(String.class, Object[].class));
        assertTrue(contains, "convertibleTypes must contain String.class with Object[].class pair");
    }

    @Test
    @DisplayName("Convert empty string and expect empty array")
    void convertEmptyStringAndExpectEmptyArray() {
        Object convert = converter.convert(EMPTY_STRING,
                TypeDescriptor.forObject(EMPTY_STRING),
                TypeDescriptor.array(TypeDescriptor.valueOf(String.class)));
        assertNotNull(convert);
        assertTrue(convert instanceof String[]);
        String[] cast = (String[]) convert;
        assertEquals(0, cast.length);
    }

    @Test
    @DisplayName("Convert string with one element and expect array with one element")
    void convertStringWithOneElementAndExpectArrayWithOneElement() {
        Object convert = converter.convert(SPACE_SEPARATED_STRING_1_ELEMENT,
                TypeDescriptor.forObject(SPACE_SEPARATED_STRING_1_ELEMENT),
                TypeDescriptor.array(TypeDescriptor.valueOf(String.class)));
        assertNotNull(convert);
        assertTrue(convert instanceof String[]);
        String[] cast = (String[]) convert;
        assertEquals(1, cast.length);
        String[] expectedArray = SPACE_SEPARATED_STRING_1_ELEMENT.split(" ");
        assertArrayEquals(expectedArray, cast);
    }

    @Test
    @DisplayName("Convert string with 2 elements and expect array with 2 elements")
    void convertStringWithTwoElementAndExpectArrayWithTwoElement() {
        Object convert = converter.convert(SPACE_SEPARATED_STRING_2_ELEMENTS,
                TypeDescriptor.forObject(SPACE_SEPARATED_STRING_2_ELEMENTS),
                TypeDescriptor.array(TypeDescriptor.valueOf(String.class)));
        assertNotNull(convert);
        assertTrue(convert instanceof String[]);
        String[] cast = (String[]) convert;
        assertEquals(2, cast.length);
        String[] expectedArray = SPACE_SEPARATED_STRING_2_ELEMENTS.split(" ");
        assertArrayEquals(expectedArray, cast);
    }

    @Test
    @DisplayName("Convert string to Oauth2ResponseType and expect success")
    void convertCustomTypesAndExpectSuccess() {
        Object convert = converter.convert(SPACE_SEPARATED_OAUTH2_RESPONSE_TYPE_CLASS_2_ELEMENTS,
                TypeDescriptor.forObject(SPACE_SEPARATED_OAUTH2_RESPONSE_TYPE_CLASS_2_ELEMENTS),
                TypeDescriptor.array(TypeDescriptor.valueOf(Oauth2ResponseType.class)));
        assertNotNull(convert);
        assertTrue(convert instanceof Oauth2ResponseType[]);
        Oauth2ResponseType[] cast = (Oauth2ResponseType[]) convert;
        assertEquals(2, cast.length);
        String[] expectedArray = SPACE_SEPARATED_OAUTH2_RESPONSE_TYPE_CLASS_2_ELEMENTS.split(" ");
        assertEquals(Oauth2ResponseType.OAUTH2_RESPONSE_TYPES.get(expectedArray[0]), cast[0]);
        assertEquals(Oauth2ResponseType.OAUTH2_RESPONSE_TYPES.get(expectedArray[1]), cast[1]);
    }
}

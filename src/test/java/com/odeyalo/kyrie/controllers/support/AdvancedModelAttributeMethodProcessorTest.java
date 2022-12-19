package com.odeyalo.kyrie.controllers.support;

import lombok.ToString;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.support.DefaultDataBinderFactory;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.method.support.ModelAndViewContainer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for  AdvancedModelAttributeMethodProcessor class
 *
 * @see AdvancedModelAttributeMethodProcessor
 */
class AdvancedModelAttributeMethodProcessorTest {
    private final AdvancedModelAttributeMethodProcessor processor = new AdvancedModelAttributeMethodProcessor(
            new ModelAttributeMethodProcessor(true)
    );

    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final NativeWebRequest nativeWebRequest = new ServletWebRequest(request, new MockHttpServletResponse());
    private final ModelAndViewContainer EMPTY_MAV_CONTAINER = new ModelAndViewContainer();
    private final WebDataBinderFactory WEB_DATA_BINDER_FACTORY = new DefaultDataBinderFactory(null);

    private static final String DEFAULT_VALUE_NAME = "Tired";
    private static final String DEFAULT_VALUE_AGE = "15";
    private static final String DEFAULT_VALUE_SEX = "m";
    private static final String DEFAULT_VALUE_VERIFIED = "false";


    @Test
    @DisplayName("Expect true for support parameter with AdvancedModelAttribute annotation")
    void supportsParameterWithAnnotationAndExpectTrue() throws NoSuchMethodException {
        MethodParameter parameter =
                new MethodParameter(TestHandler.class.getDeclaredMethod("advancedModelAttributeWithDefaultValue", TestFormPropertyWithDefaultValue.class), 0);
        boolean actual = processor.supportsParameter(parameter);
        assertTrue(actual);
    }

    @Test
    @DisplayName("Expect false for support parameter with AdvancedModelAttribute annotation")
    void supportsParameterWithAnnotationAndExpectFalse() throws NoSuchMethodException {
        MethodParameter parameter =
                new MethodParameter(TestHandler.class.getDeclaredMethod("methodWithoutAdvancedModelAttributeAnnotation", TestFormPropertyWithoutAnnotations.class), 0);
        boolean actual = processor.supportsParameter(parameter);
        assertFalse(actual);
    }

    @Test
    @DisplayName("Resolve argument from request and bind it to data class without defaultValue parameter in FormProperty")
    void resolveArgumentWithoutDefaultValue() throws Exception {
        MethodParameter parameter = new MethodParameter(TestHandler.class.getDeclaredMethod("advancedModelAttributeWithoutDefaultValue", TestFormPropertyWithoutDefaultValue.class), 0);
        String AGE_KEY = "age";
        String AGE_VALUE = "18";
        String NAME_KEY = "name";
        String NAME_VALUE = "Odeyalo";
        String VERIFIED_KEY = "verified";
        String VERIFIED_VALUE = "true";

        request.setParameter(AGE_KEY, AGE_VALUE);
        request.setParameter(NAME_KEY, NAME_VALUE);
        request.setParameter(VERIFIED_KEY, VERIFIED_VALUE);

        Object o = processor.resolveArgument(parameter, EMPTY_MAV_CONTAINER, nativeWebRequest, WEB_DATA_BINDER_FACTORY);
        TestFormPropertyWithoutDefaultValue actual = (TestFormPropertyWithoutDefaultValue) o;

        assertNotNull(actual);
        assertEquals(Integer.parseInt(AGE_VALUE), actual.age);
        assertEquals(NAME_VALUE, actual.differentName);
        assertEquals(Boolean.parseBoolean(VERIFIED_VALUE), actual.isVerified);
        assertEquals(Character.MIN_VALUE, actual.sex);
    }

    @Test
    @DisplayName("Resolve argument from request and bind it to data class with defaultValue parameter in FormProperty")
    void resolveArgumentWithDefaultValue() throws Exception {
        MethodParameter parameter = new MethodParameter(TestHandler.class.getDeclaredMethod("advancedModelAttributeWithDefaultValue", TestFormPropertyWithDefaultValue.class), 0);

        Object o = processor.resolveArgument(parameter, EMPTY_MAV_CONTAINER, nativeWebRequest, WEB_DATA_BINDER_FACTORY);
        TestFormPropertyWithDefaultValue actual = (TestFormPropertyWithDefaultValue) o;

        assertNotNull(actual);
        assertEquals(Integer.parseInt(DEFAULT_VALUE_AGE), actual.age);
        assertEquals(DEFAULT_VALUE_NAME, actual.differentName);
        assertEquals(Boolean.parseBoolean(DEFAULT_VALUE_VERIFIED), actual.isVerified);
        assertEquals(DEFAULT_VALUE_SEX.charAt(0), actual.sex);
    }

    @Test
    @DisplayName("Resolve argument from request and bind it to data class without annotation and no parameters in request. Except null in all fields")
    void resolveArgumentWithoutAnnotationsAndParameters() throws Exception {
        MethodParameter parameter = new MethodParameter(TestHandler.class.getDeclaredMethod("advancedModelAttributeWithoutAnnotationsAndParameters", TestFormPropertyWithoutAnnotations.class), 0);

        Object o = processor.resolveArgument(parameter, EMPTY_MAV_CONTAINER, nativeWebRequest, WEB_DATA_BINDER_FACTORY);
        TestFormPropertyWithoutAnnotations actual = (TestFormPropertyWithoutAnnotations) o;

        assertNotNull(actual);
        assertEquals(0, actual.age);
        assertNull(actual.differentName);
        assertFalse(actual.isVerified);
        assertEquals(Character.MIN_VALUE, actual.sex);
    }


    private static class TestHandler {

        public void advancedModelAttributeWithoutDefaultValue(@AdvancedModelAttribute TestFormPropertyWithoutDefaultValue dto) {

        }

        public void advancedModelAttributeWithDefaultValue(@AdvancedModelAttribute TestFormPropertyWithDefaultValue dto) {

        }

        public void advancedModelAttributeWithoutAnnotationsAndParameters(@AdvancedModelAttribute TestFormPropertyWithoutAnnotations dto) {

        }

        public void methodWithoutAdvancedModelAttributeAnnotation(TestFormPropertyWithoutAnnotations dto) {

        }

    }


    @ToString
    private static class TestFormPropertyWithoutDefaultValue {
        @FormProperty(value = "name")
        private String differentName;
        private int age;
        @FormProperty(value = "verified")
        private boolean isVerified;
        private char sex;
    }

    @ToString
    private static class TestFormPropertyWithDefaultValue {
        @FormProperty(value = "name", defaultValue = DEFAULT_VALUE_NAME)
        private String differentName;
        @FormProperty(defaultValue = DEFAULT_VALUE_AGE)
        private int age;
        @FormProperty(value = "verified", defaultValue = DEFAULT_VALUE_VERIFIED)
        private boolean isVerified;
        @FormProperty(defaultValue = DEFAULT_VALUE_SEX)
        private char sex;
    }

    @ToString
    private static class TestFormPropertyWithoutAnnotations {
        private String differentName;
        private int age;
        private boolean isVerified;
        private char sex;
    }
}

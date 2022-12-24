package com.odeyalo.kyrie.support.cookie;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.SerializationUtils;

import javax.servlet.http.Cookie;
import java.io.Serializable;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CookieUtils class.
 * @see CookieUtils
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CookieUtilsTest {
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    public static final String COOKIE_NAME_1 = "name1";
    public static final String COOKIE_NAME_2 = "name2";
    public static final String COOKIE_VALUE_1 = "value1";
    public static final String COOKIE_VALUE_2 = "value2";
    public static final String NOT_EXISTING_COOKIE = "not_existing";

    public static final String NAME = "Odeyalo";
    public static final int AGE = 18;

    private static final Cookie[] DEFAULT_COOKIES = Arrays.array(new Cookie(COOKIE_NAME_1, COOKIE_VALUE_1), new Cookie(COOKIE_NAME_2, COOKIE_VALUE_2));

    /**
     * Before each test reset the request and response and set default cookies
     */
    @BeforeEach
    public void init() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        request.setCookies(DEFAULT_COOKIES);
        for (Cookie defaultCookie : DEFAULT_COOKIES) {
            response.addCookie(defaultCookie);
        }
    }

    @Test
    @DisplayName("Get existing cookie from request and expect success")
    void getExistingCookieByName_AndExpectSuccess() {
        Optional<Cookie> cookie = CookieUtils.getCookieByName(request, COOKIE_NAME_1);
        assertTrue(cookie.isPresent());
        Cookie value = cookie.get();
        assertEquals(COOKIE_NAME_1, value.getName());
        assertEquals(COOKIE_VALUE_1, value.getValue());
    }


    @Test
    @DisplayName("Get not existing cookie from request and expect Optional.empty()")
    void getNotExistingCookieByName_AndExpectEmpty() {
        Optional<Cookie> cookie = CookieUtils.getCookieByName(request, NOT_EXISTING_COOKIE);
        assertTrue(cookie.isEmpty());
    }

    @Test
    @DisplayName("Add cookie to response without lifecycle and expect success")
    void testAddCookieWithoutLifecycle() {
        String addCookieName = "ADDED1";
        String addCookieValue = "added_value1";
        CookieUtils.addCookie(response, addCookieName, addCookieValue);
        Cookie cookie = response.getCookie(addCookieName);
        assertNotNull(cookie);
        assertEquals(addCookieName, cookie.getName());
        assertEquals(addCookieValue, cookie.getValue());
    }

    @Test
    @DisplayName("Add cookie to response with lifecycle and expect success")
    void testAddCookieWithLifecycle() {
        String addCookieName = "ADDED1";
        String addCookieValue = "added_value1";
        int addCookieMaxAge = 10;
        CookieUtils.addCookie(response, addCookieName, addCookieValue, addCookieMaxAge);
        Cookie cookie = response.getCookie(addCookieName);
        assertNotNull(cookie);
        assertEquals(addCookieName, cookie.getName());
        assertEquals(addCookieValue, cookie.getValue());
        assertEquals(cookie.getMaxAge(), addCookieMaxAge);
    }


    @Test
    @DisplayName("Delete existing cookie and expect success")
    void deleteCookieAndExpectSuccess() {
        // Create a new MockHttpServletResponse to avoid default cookies
        response = new MockHttpServletResponse();

        request.setCookies(new Cookie(COOKIE_NAME_1, COOKIE_VALUE_1), new Cookie(COOKIE_NAME_2, COOKIE_VALUE_2));

        CookieUtils.deleteCookie(request, response, COOKIE_NAME_1);
        Cookie cookie = response.getCookie(COOKIE_NAME_1);

        assertNotNull(cookie);
        assertEquals("", cookie.getValue());
        assertEquals("/", cookie.getPath());
        assertEquals(0, cookie.getMaxAge());
    }

    @Test
    @DisplayName("Serialize Example class with fields and expect success")
    void serialize() {
        String serializationResult = CookieUtils.serialize(new Example(NAME, AGE));
        assertNotNull(serializationResult);
        byte[] decodedBytes = Base64.getUrlDecoder().decode(serializationResult.getBytes());
        Object deserialize = SerializationUtils.deserialize(decodedBytes);
        assertTrue(deserialize instanceof Example);
        Example example = (Example) deserialize;
        assertEquals(NAME, example.getName());
        assertEquals(AGE, example.getAge());
    }

    @Test
    @DisplayName("Deserialize encoded cookie to Example class with fields and expect success")
    void deserialize() {
        String encodedString = "rO0ABXNyADhjb20ub2RleWFsby5reXJpZS5zdXBwb3J0LmNvb2tpZS5Db29raWVVdGlsc1Rlc3QkRXhhbXBsZbToJy42tcnzAgACSQADYWdlTAAEbmFtZXQAEkxqYXZhL2xhbmcvU3RyaW5nO3hwAAAAEnQAB09kZXlhbG8=";
        Cookie cookie = new Cookie("example", encodedString);
        Example deserialize = CookieUtils.deserialize(cookie, Example.class);
        assertNotNull(deserialize);
        assertEquals(NAME, deserialize.getName());
        assertEquals(AGE, deserialize.getAge());
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static final class Example implements Serializable {
        private String name;
        private int age;
    }
}

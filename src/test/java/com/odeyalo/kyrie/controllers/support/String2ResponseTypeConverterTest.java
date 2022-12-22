package com.odeyalo.kyrie.controllers.support;

import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.oauth2.oidc.OidcResponseType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for String2ResponseTypeConverter class
 * @see String2ResponseTypeConverter
 * @version 1.0
 */
class String2ResponseTypeConverterTest {
    private final String2ResponseTypeConverter converter = new String2ResponseTypeConverter();
    public static final String CODE_SOURCE = "code";
    public static final String TOKEN_SOURCE = "token";
    public static final String ID_TOKEN_SOURCE = "id_token";
    public static final String NOT_EXISTING_SOURCE = "NOT_EXISTING";

    public static final Oauth2ResponseType EXPECTED_TOKEN_SOURCE_RESULT = Oauth2ResponseType.TOKEN;
    public static final Oauth2ResponseType EXPECTED_ID_TOKEN_SOURCE_RESULT = OidcResponseType.ID_TOKEN;
    public static final Oauth2ResponseType EXPECTED_CODE_SOURCE_RESULT = Oauth2ResponseType.CODE;

    @Test
    @DisplayName("Convert CODE_SOURCE to Oauth2ResponseType.CODE and expect success result")
    void convertCodeSourceAndExpectSuccessResult() {
        Oauth2ResponseType convertingResult = converter.convert(CODE_SOURCE);
        assertNotNull(convertingResult);
        assertNotEquals(convertingResult, EXPECTED_TOKEN_SOURCE_RESULT);
        assertNotEquals(convertingResult, EXPECTED_ID_TOKEN_SOURCE_RESULT);
        assertEquals(convertingResult, EXPECTED_CODE_SOURCE_RESULT);
    }

    @Test
    @DisplayName("Convert TOKEN_SOURCE to Oauth2ResponseType.TOKEN and expect success result")
    void convertTokenSourceAndExpectSuccessResult() {
        Oauth2ResponseType convertingResult = converter.convert(TOKEN_SOURCE);
        assertNotNull(convertingResult);
        assertNotEquals(convertingResult, EXPECTED_CODE_SOURCE_RESULT);
        assertNotEquals(convertingResult, EXPECTED_ID_TOKEN_SOURCE_RESULT);
        assertEquals(convertingResult, EXPECTED_TOKEN_SOURCE_RESULT);
    }

    @Test
    @DisplayName("Convert ID_TOKEN_SOURCE to OidcResponseType.ID_TOKEN and expect success result")
    void convertIdTokenSourceAndExpectSuccessResult() {
        Oauth2ResponseType convertingResult = converter.convert(ID_TOKEN_SOURCE);
        assertNotNull(convertingResult);
        assertNotEquals(convertingResult, EXPECTED_TOKEN_SOURCE_RESULT);
        assertNotEquals(convertingResult, EXPECTED_CODE_SOURCE_RESULT);
        assertEquals(convertingResult, EXPECTED_ID_TOKEN_SOURCE_RESULT);
    }

    @Test
    @DisplayName("Convert not existing source to Oauth2ResponseType and expect null as the result")
    void convertNotExistingSourceAndExpectNull() {
        Oauth2ResponseType convertingResult = converter.convert(NOT_EXISTING_SOURCE);
        assertNull(convertingResult);
    }
}

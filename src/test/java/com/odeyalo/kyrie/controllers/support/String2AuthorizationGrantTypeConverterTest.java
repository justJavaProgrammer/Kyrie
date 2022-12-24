package com.odeyalo.kyrie.controllers.support;

import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests for String2AuthorizationGrantTypeConverter class.
 *
 * @see String2AuthorizationGrantTypeConverter
 */
class String2AuthorizationGrantTypeConverterTest {
    private final String2AuthorizationGrantTypeConverter converter = new String2AuthorizationGrantTypeConverter();
    public static final String NOT_EXISTING_GRANT_TYPE = "non existing";

    @Test
    @DisplayName("Convert authorization code grant type and expect success")
    void convertAuthorizationCodeGrantTypeAndExceptSuccess() {
        AuthorizationGrantType convert = converter.convert(AuthorizationGrantType.AUTHORIZATION_CODE.getGrantName());
        assertEquals(AuthorizationGrantType.AUTHORIZATION_CODE, convert);
    }

    @Test
    @DisplayName("Convert implicit grant type and expect success")
    void convertImplicitGrantTypeAndExceptSuccess() {
        AuthorizationGrantType convert = converter.convert(AuthorizationGrantType.IMPLICIT.getGrantName());
        assertEquals(AuthorizationGrantType.IMPLICIT, convert);
    }


    @Test
    @DisplayName("Convert multiple(Hybrid) grant type and expect success")
    void convertMultipleGrantTypeAndExceptSuccess() {
        AuthorizationGrantType convert = converter.convert(AuthorizationGrantType.MULTIPLE.getGrantName());
        assertEquals(AuthorizationGrantType.MULTIPLE, convert);
    }


    @Test
    @DisplayName("Convert not existing grant type and expect null")
    void convertNotExistingGrantTypeAndExceptNull() {
        AuthorizationGrantType convert = converter.convert(NOT_EXISTING_GRANT_TYPE);
        assertNull(convert);
    }
}

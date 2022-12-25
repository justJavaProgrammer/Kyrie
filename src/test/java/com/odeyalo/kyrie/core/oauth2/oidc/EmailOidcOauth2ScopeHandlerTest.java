package com.odeyalo.kyrie.core.oauth2.oidc;

import com.odeyalo.kyrie.core.Oauth2User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for EmailOidcOauth2ScopeHandler class.
 * @see EmailOidcOauth2ScopeHandlerTest
 */
class EmailOidcOauth2ScopeHandlerTest {
    private static final String USER_ID = "1";
    private final EmailOidcOauth2ScopeHandler handler = new EmailOidcOauth2ScopeHandler();

    @Test
    @DisplayName("Create claims for user that contains email claims and expect email and email_verified properties set")
    void createClaimsToUserThatContainsEmail() {
        String expectedUserEmail = "odeyalo@gmail.com";
        boolean isVerified = true;


        Oauth2User user = new Oauth2User(USER_ID, "odeyalo", "password", Set.of("USER"),
                Map.of(EmailOidcOauth2ScopeHandler.EMAIL_SCOPE, expectedUserEmail,
                        EmailOidcOauth2ScopeHandler.EMAIL_VERIFIED_SCOPE, isVerified));

        Map<String, Object> claims = handler.createClaims(user);
        Object emailObj = claims.get(EmailOidcOauth2ScopeHandler.EMAIL_SCOPE);
        Object isVerifiedObj = claims.get(EmailOidcOauth2ScopeHandler.EMAIL_VERIFIED_SCOPE);
        assertNotNull(emailObj, "Email should be presented");
        assertNotNull(isVerifiedObj, "email_verified should be presented");

        assertTrue(emailObj instanceof String, "Email should be String!");
        assertTrue(isVerifiedObj instanceof Boolean, "email_verified should be boolean!");

        String email = (String) emailObj;
        Boolean emailVerified = (Boolean) isVerifiedObj;
        assertEquals(expectedUserEmail, email);
        assertEquals(isVerified, emailVerified);
    }

    @Test
    @DisplayName("Create claims for user that does not contains email claims and expect email and email_verified properties set")
    void createClaimsToUserThatNotContainsEmail() {
        Oauth2User user = new Oauth2User(USER_ID, "odeyalo", "password", Set.of("USER"),
               Collections.emptyMap());

        Map<String, Object> claims = handler.createClaims(user);
        Object emailObj = claims.get(EmailOidcOauth2ScopeHandler.EMAIL_SCOPE);
        Object isVerifiedObj = claims.get(EmailOidcOauth2ScopeHandler.EMAIL_VERIFIED_SCOPE);
        assertNotNull(emailObj, "Email should be presented");
        assertNotNull(isVerifiedObj, "email_verified should be presented");

        assertTrue(emailObj instanceof String, "Email should be String!");
        assertTrue(isVerifiedObj instanceof Boolean, "email_verified should be boolean!");

        String email = (String) emailObj;
        Boolean emailVerified = (Boolean) isVerifiedObj;
        assertEquals("null", email, "If user does not contain email then null wrapped as String should be returned ");
        assertFalse(emailVerified, "If email is not presented email_verified should be false");
    }

    @Test
    void supportedScope() {
        String scope = handler.supportedScope();
        assertEquals(EmailOidcOauth2ScopeHandler.EMAIL_SCOPE, scope);
    }
}

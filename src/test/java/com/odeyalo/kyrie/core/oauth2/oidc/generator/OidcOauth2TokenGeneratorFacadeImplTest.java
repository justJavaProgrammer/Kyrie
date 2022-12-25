package com.odeyalo.kyrie.core.oauth2.oidc.generator;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;
import com.odeyalo.kyrie.core.oauth2.Oauth2ScopeHandler;
import com.odeyalo.kyrie.core.oauth2.oidc.EmailOidcOauth2ScopeHandler;
import com.odeyalo.kyrie.core.oauth2.oidc.OidcIdToken;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.JwtTokenProviderImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for OidcOauth2TokenGeneratorFacadeImpl class.
 *
 * @see OidcOauth2TokenGeneratorFacadeImpl
 */
class OidcOauth2TokenGeneratorFacadeImplTest {
    private static final String USER_ID = "1";
    private final String SECRET_WORD = "secret";
    private final String ISSUER = "http://localhost:9000";

    private final List<Oauth2ScopeHandler> handlers = List.of(
            new EmailOidcOauth2ScopeHandler()
    );

    private final OidcOauth2TokenGeneratorFacadeImpl generatorFacade = new OidcOauth2TokenGeneratorFacadeImpl(
            new OidcIdTokenGeneratorImpl(
                    new JwtTokenProviderImpl(SECRET_WORD), ISSUER), handlers);

    @Test
    @DisplayName("Generate token with email scope for user without email and expect email and email_verified properties in result claims")
    void generateTokenWithEmailScopeWithoutUserEmailAndExpectSuccess() {
        String clientId = "client_123";
        String[] scopes = {"read", "write", "email", "openid"};
        Oauth2ClientCredentials credentials = Oauth2ClientCredentials.of(clientId);
        Oauth2User user = new Oauth2User(USER_ID, "odeyalo", "password", Set.of("USER"), Collections.emptyMap());
        OidcIdToken token = generatorFacade.generateToken(credentials, user, scopes);

        String tokenValue = token.getTokenValue();
        Instant expiresIn = token.getExpiresIn();
        Instant issuedAt = token.getIssuedAt();
        Map<String, Object> tokenMetadataClaims = token.getClaims();

        assertNotNull(tokenValue);
        assertNotNull(expiresIn);
        assertNotNull(issuedAt);
        // Assert that all required fields are set and valid
        assertTrue(expiresIn.isAfter(issuedAt), "expires_is param should be greater than issued_at");
        assertTrue(tokenMetadataClaims.containsKey(EmailOidcOauth2ScopeHandler.EMAIL_SCOPE), "If 'email' scope is presented then claims should contain user's email");
        assertTrue(tokenMetadataClaims.containsKey(EmailOidcOauth2ScopeHandler.EMAIL_VERIFIED_SCOPE), "If 'email' scope is presented then claims should email_verified property");
        String email = (String) tokenMetadataClaims.get(EmailOidcOauth2ScopeHandler.EMAIL_SCOPE);
        boolean emailVerified = (Boolean) tokenMetadataClaims.get(EmailOidcOauth2ScopeHandler.EMAIL_VERIFIED_SCOPE);
        assertEquals("null", email, "If email is not presented for user then null should be returned");
        assertFalse(emailVerified, "If email is not presented for user then false should be returned in email_verified property");
        // Parse token and check claims
        Claims body = Jwts.parser().setSigningKey(SECRET_WORD).parseClaimsJws(tokenValue).getBody();
        assertNotNull(body, "Body claims can't be null");
        Object emailValueFromParsedToken = body.get(EmailOidcOauth2ScopeHandler.EMAIL_SCOPE);
        Object emailVerifiedFromParsedToken = body.get(EmailOidcOauth2ScopeHandler.EMAIL_VERIFIED_SCOPE);
        assertNotNull(emailValueFromParsedToken, "If 'email' scope is presented then claims should contain user's email");
        assertNotNull(emailVerifiedFromParsedToken, "If 'email' scope is presented then claims should email_verified property");
    }

    @Test
    @DisplayName("Generate token with email scope for user with email and expect email and email_verified properties in result claims")
    void generateTokenWithEmailScopeWithUserEmailAndExpectSuccess() {
        String clientId = "client_123";
        String[] scopes = {"read", "write", "email", "openid"};
        String expectedUserEmail = "odeyalo@gmail.com";
        boolean isVerified = true;


        Oauth2User user = new Oauth2User(USER_ID, "odeyalo", "password", Set.of("USER"),
                Map.of(EmailOidcOauth2ScopeHandler.EMAIL_SCOPE, expectedUserEmail,
                        EmailOidcOauth2ScopeHandler.EMAIL_VERIFIED_SCOPE, isVerified));

        Oauth2ClientCredentials credentials = Oauth2ClientCredentials.of(clientId);
        OidcIdToken token = generatorFacade.generateToken(credentials, user, scopes);

        String tokenValue = token.getTokenValue();
        Instant expiresIn = token.getExpiresIn();
        Instant issuedAt = token.getIssuedAt();
        Map<String, Object> tokenMetadataClaims = token.getClaims();

        assertNotNull(tokenValue);
        assertNotNull(expiresIn);
        assertNotNull(issuedAt);
        // Assert that all required fields are set and valid
        assertTrue(expiresIn.isAfter(issuedAt), "expires_is param should be greater than issued_at");
        assertTrue(tokenMetadataClaims.containsKey(EmailOidcOauth2ScopeHandler.EMAIL_SCOPE), "If 'email' scope is presented then claims should contain user's email");
        assertTrue(tokenMetadataClaims.containsKey(EmailOidcOauth2ScopeHandler.EMAIL_VERIFIED_SCOPE), "If 'email' scope is presented then claims should email_verified property");
        String email = (String) tokenMetadataClaims.get(EmailOidcOauth2ScopeHandler.EMAIL_SCOPE);
        boolean emailVerified = (Boolean) tokenMetadataClaims.get(EmailOidcOauth2ScopeHandler.EMAIL_VERIFIED_SCOPE);
        assertEquals(expectedUserEmail, email, "If email is presented for user then user's email should be returned");
        assertTrue(emailVerified, "If user's email is verified then true should be returned in email_verified claim");
        // Parse token and check claims
        Claims body = Jwts.parser().setSigningKey(SECRET_WORD).parseClaimsJws(tokenValue).getBody();
        assertNotNull(body, "Body claims can't be null");
        Object emailValueFromParsedToken = body.get(EmailOidcOauth2ScopeHandler.EMAIL_SCOPE);
        Object emailVerifiedFromParsedToken = body.get(EmailOidcOauth2ScopeHandler.EMAIL_VERIFIED_SCOPE);
        assertNotNull(emailValueFromParsedToken, "If 'email' scope is presented then claims should contain user's email");
        assertNotNull(emailVerifiedFromParsedToken, "If 'email' scope is presented then claims should email_verified property");
    }

    @Test
    @DisplayName("Generate token without openid scopes and expect only default result claims")
    void generateTokenWithoutOpenidScopesWithUserEmailAndExpectSuccess() {
        String clientId = "client_123";
        String[] scopes = {"read", "write"};
        String expectedUserEmail = "odeyalo@gmail.com";
        boolean isVerified = true;


        Oauth2User user = new Oauth2User(USER_ID, "odeyalo", "password", Set.of("USER"),
                Map.of(EmailOidcOauth2ScopeHandler.EMAIL_SCOPE, expectedUserEmail,
                        EmailOidcOauth2ScopeHandler.EMAIL_VERIFIED_SCOPE, isVerified));

        Oauth2ClientCredentials credentials = Oauth2ClientCredentials.of(clientId);
        OidcIdToken token = generatorFacade.generateToken(credentials, user, scopes);

        String tokenValue = token.getTokenValue();
        Instant expiresIn = token.getExpiresIn();
        Instant issuedAt = token.getIssuedAt();
        Map<String, Object> oidcIdTokenMetadataClaims = token.getClaims();

        assertNotNull(tokenValue);
        assertNotNull(expiresIn);
        assertNotNull(issuedAt);
        // Assert that all required fields are set and valid
        assertTrue(expiresIn.isAfter(issuedAt), "expires_is param should be greater than issued_at");
        assertFalse(oidcIdTokenMetadataClaims.containsKey(EmailOidcOauth2ScopeHandler.EMAIL_SCOPE), "If 'email' scope does not presented then claims should not contain user's email");
        assertFalse(oidcIdTokenMetadataClaims.containsKey(EmailOidcOauth2ScopeHandler.EMAIL_VERIFIED_SCOPE), "If 'email' scope does not presented then claims should not email_verified property");
    }
}

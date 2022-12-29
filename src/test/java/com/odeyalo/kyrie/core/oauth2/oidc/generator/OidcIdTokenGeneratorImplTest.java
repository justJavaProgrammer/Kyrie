package com.odeyalo.kyrie.core.oauth2.oidc.generator;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.oauth2.oidc.OidcIdToken;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.JwtTokenProvider;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.DefaultSecretWordJwtTokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for OidcIdTokenGeneratorImpl class.
 * @see OidcIdTokenGeneratorImpl
 */
class OidcIdTokenGeneratorImplTest {
    private static final String ISSUER = "http://localhost:9999";
    private static final String USER_ID = "1";
    private static final String SECRET_WORD = "secret";
    private final JwtTokenProvider provider = new DefaultSecretWordJwtTokenProvider(SECRET_WORD);
    private final OidcIdTokenGeneratorImpl oidcIdTokenGenerator = new OidcIdTokenGeneratorImpl(provider, ISSUER);

    @Test
    @DisplayName("Generate oidc id token with only default claims and expect success with valid metadata")
    void generateOidcTokenWithAllFieldsSetAndExpectSuccess() {
        String clientId = "123";
        Oauth2User user = new Oauth2User(USER_ID, "Odeyalo", "password", Set.of("USER"), Collections.emptyMap());
        OidcIdToken actualToken = oidcIdTokenGenerator.generateOidcToken(clientId, user);

        String tokenValue = actualToken.getTokenValue();
        Instant expiresIn = actualToken.getExpiresIn();
        Instant issuedAt = actualToken.getIssuedAt();

        Map<String, Object> metadataClaims = actualToken.getClaims();
        assertNotNull(expiresIn);
        assertNotNull(issuedAt);
        assertTrue(expiresIn.isAfter(issuedAt), "The expires_in parameter must be greater than issued_at");

        Claims claims = Jwts.parser().setSigningKey(SECRET_WORD).parseClaimsJws(tokenValue).getBody();

        assertNotNull(claims, "Token can't have null claims!");
        assertEquals(metadataClaims.get(Claims.SUBJECT), claims.getSubject());
        assertEquals(metadataClaims.get(Claims.ISSUER), claims.getIssuer());
        // Cast to long to avoid wrong casting by Junit

        assertNotEquals(0L, metadataClaims.get(OidcIdTokenGenerator.AUTH_TIME), "Auth time cannot be null!");
        long metadataAuthTimeClaim = ((Number) metadataClaims.get(OidcIdTokenGenerator.AUTH_TIME)).longValue();
        long parsedJwtAuthTimeClaim = ((Number) claims.get(OidcIdTokenGenerator.AUTH_TIME)).longValue();
        assertEquals(metadataAuthTimeClaim, parsedJwtAuthTimeClaim, "Claim from metadata and from token must be equal!");

        Instant authTime = Instant.ofEpochSecond(parsedJwtAuthTimeClaim);
        assertTrue((Instant.now().isAfter(authTime)), "Auth time must be less than current time!");
        assertEquals(metadataClaims.get(Claims.AUDIENCE), claims.getAudience());
        assertEquals(clientId, claims.getAudience());
    }

    @Test
    @DisplayName("Generate oidc id token with additional claims and expect success with valid metadata")
    void testGenerateOidcTokenWithAdditionalClaimsAndExpectSuccess() {
        String clientId = "123";
        String username = "Odeyalo";
        final String USERNAME_CLAIM_KEY = "username";
        Oauth2User user = new Oauth2User(USER_ID, username, "password", Set.of("USER"), Collections.emptyMap());
        Map<String, Object> additionalClaims = Map.of(USERNAME_CLAIM_KEY, username);
        OidcIdToken actualToken = oidcIdTokenGenerator.generateOidcToken(clientId, user, additionalClaims);

        String tokenValue = actualToken.getTokenValue();
        Instant expiresIn = actualToken.getExpiresIn();
        Instant issuedAt = actualToken.getIssuedAt();
        Map<String, Object> metadataClaims = actualToken.getClaims();

        assertNotNull(expiresIn);
        assertNotNull(issuedAt);
        assertNotNull(metadataClaims, "TokenMetadata can't return null as claims value");
        assertTrue(expiresIn.isAfter(issuedAt), "The expires_in parameter must be greater than issued_at");

        Claims claims = Jwts.parser().setSigningKey(SECRET_WORD).parseClaimsJws(tokenValue).getBody();

        assertNotNull(claims, "Token can't have null claims!");
        assertEquals(metadataClaims.get(Claims.SUBJECT), claims.getSubject());
        assertEquals(metadataClaims.get(Claims.ISSUER), claims.getIssuer());
        assertEquals(metadataClaims.get(USERNAME_CLAIM_KEY), claims.get(USERNAME_CLAIM_KEY));
        assertEquals(username, claims.get(USERNAME_CLAIM_KEY));
        // Cast to long to avoid wrong casting by Junit
        assertEquals(((Number) metadataClaims.get(OidcIdTokenGenerator.AUTH_TIME)).longValue(), ((Number) claims.get(OidcIdTokenGenerator.AUTH_TIME)).longValue());
        assertEquals(metadataClaims.get(Claims.AUDIENCE), claims.getAudience());
        assertEquals(clientId, claims.getAudience());
    }

    @Test
    @DisplayName("Generate oidc id token with null client id and expect error")
    void generateOidcTokenWithNullClientId_AndExpectError() {
        Oauth2User user = new Oauth2User(USER_ID, "Odeyalo", "password", Set.of("USER"), Collections.emptyMap());
        assertThrows(IllegalArgumentException.class, () -> oidcIdTokenGenerator.generateOidcToken(null, user));
    }
    @Test
    @DisplayName("Generate oidc id token with null user and expect error")
    void generateOidcTokenWithNullOauth2User_AndExpectError() {
        String clientId = "123";
        assertThrows(IllegalArgumentException.class, () -> oidcIdTokenGenerator.generateOidcToken(clientId, null));
    }

    @Test
    @DisplayName("Generate oidc id token with null null claims and expect error")
    void generateOidcTokenWithNullAdditionalClaims_AndExpectError() {
        String clientId=  "client123";
        Oauth2User user = new Oauth2User(USER_ID, "Odeyalo", "password", Set.of("USER"), Collections.emptyMap());
        assertThrows(IllegalArgumentException.class, () -> oidcIdTokenGenerator.generateOidcToken(clientId, user, null));
    }
}

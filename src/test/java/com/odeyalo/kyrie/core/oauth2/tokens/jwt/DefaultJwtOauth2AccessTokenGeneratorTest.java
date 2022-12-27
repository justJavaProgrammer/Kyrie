package com.odeyalo.kyrie.core.oauth2.tokens.jwt;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DefaultJwtOauth2AccessTokenGenerator class.
 * @see DefaultJwtOauth2AccessTokenGenerator
 */
class DefaultJwtOauth2AccessTokenGeneratorTest {
    public static final String SECRET_WORD = "secret";
    private final JwtTokenProvider provider = new DefaultSecretWordJwtTokenProvider(SECRET_WORD);
    private final DefaultJwtOauth2AccessTokenGenerator generator = new DefaultJwtOauth2AccessTokenGenerator(provider);

    @Test
    @DisplayName("Generate access token with valid request and user")
    void generateAccessToken() {
        Oauth2User user = Oauth2User
                .builder()
                .id("1")
                .username("odeyalo")
                .password("pxsswxrd")
                .authorities(Set.of("USER"))
                .additionalInfo(Collections.emptyMap())
                .build();
        String[] actualScopes = {"read", "write"};
        Oauth2AccessToken accessToken = generator.generateAccessToken(user, actualScopes);
        Oauth2AccessToken.TokenType tokenType = accessToken.getTokenType();
        Instant expiresIn = accessToken.getExpiresIn();
        Instant issuedAt = accessToken.getIssuedAt();
        String scope = accessToken.getScope();
        String tokenValue = accessToken.getTokenValue();

        assertNotNull(expiresIn, "expires_in must be not null!");
        assertNotNull(issuedAt, "issued_at must be not null!");
        assertNotNull(scope, "scope must be not null!");
        assertNotNull(tokenValue, "token value must be not null!");
        assertNotNull(tokenType, "token type must be not null!");
        assertTrue(expiresIn.isAfter(issuedAt), "expires_in must be greater than issued_at");

        String[] metadataScopes = scope.split(" ");
        assertArrayEquals(actualScopes, metadataScopes, "Scopes from metadata must be equal");
        Claims claims = Jwts.parser().setSigningKey(SECRET_WORD).parseClaimsJws(tokenValue).getBody();
        String scopesFromJwt = claims.get(Oauth2AccessTokenGenerator.SCOPE, String.class);
        assertEquals(String.join(" ", actualScopes), scopesFromJwt, "Scopes from jwt claims must be equal");
    }
}

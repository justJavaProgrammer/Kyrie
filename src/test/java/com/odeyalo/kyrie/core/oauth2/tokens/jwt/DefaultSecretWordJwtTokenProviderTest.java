package com.odeyalo.kyrie.core.oauth2.tokens.jwt;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.oauth2.tokens.TokenMetadata;
import com.odeyalo.kyrie.core.oauth2.tokens.TokenValidationResult;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for JwtTokenProviderImpl class
 *
 * @version 1.0.0
 * @see DefaultSecretWordJwtTokenProvider
 */
class DefaultSecretWordJwtTokenProviderTest {
    public static final String USER_ID = "1";
    private final String secretWord = "secretWord";
    private final DefaultSecretWordJwtTokenProvider provider = new DefaultSecretWordJwtTokenProvider(secretWord);


    @Test
    @DisplayName("Generate jwt token and expect success")
    void generateJwtToken() {
        Oauth2User user = new Oauth2User(USER_ID, "odeyalo", "password", Set.of("USER"), Collections.emptyMap());
        TokenMetadata tokenMetadata = provider.generateJwtToken(user, Collections.emptyMap());
        Object o = tokenMetadata.getClaims().get(Claims.SUBJECT);
        assertNotNull(o, "The jwt token must include SUBJECT claim");
        String subject = (String) o;
        Long issuedAt = tokenMetadata.getIssuedAt();
        Long expiresIn = tokenMetadata.getExpiresIn();
        Claims claims = Jwts.parser().setSigningKey(secretWord).parseClaimsJws(tokenMetadata.getToken()).getBody();
        assertEquals(claims.getIssuedAt().toInstant(), Instant.ofEpochSecond(issuedAt));
        assertEquals(claims.getExpiration().toInstant(), Instant.ofEpochSecond(expiresIn));
        assertEquals(claims.getSubject(), subject);
    }

    @Test
    @DisplayName("Parse valid jwt token and expect success")
    void parseValidTokenAndExpectSuccess() {
        TokenMetadata expectedMetadata = getToken();
        TokenMetadata metadata = provider.parseToken(expectedMetadata.getToken());
        assertEquals(expectedMetadata, metadata);
    }

    @Test
    @DisplayName("Get expiration time of the valid jwt token and expect success")
    void getExpiredJwtTokenTimeInDate() {
        TokenMetadata metadata = getToken();
        Date actual = provider.getExpiredJwtTokenTimeInDate(metadata.getToken());
        Date expected = Date.from(Instant.ofEpochSecond(metadata.getExpiresIn()));
        assertEquals(expected, actual);

    }

    @Test
    @DisplayName("Test isTokenValid for valid jwt token and expect success")
    void isTokenValidForValidTokenAndExpectSuccess() {
        TokenMetadata metadata = getToken();
        TokenValidationResult validationResult = provider.isTokenValid(metadata.getToken());
        assertTrue(validationResult.isValid());
        assertNull(validationResult.getMessage());
    }

    @Test
    @DisplayName("Test isTokenValid for invalid jwt token signature and expect TokenValidationResult with isValid is false and message is not null")
    void isTokenValidWithInvalidJwtTokenSignatureAndExpectSuccess() {
        String jwtWithInvalidSignature = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NzExMjA1OTgsInN1YiI6IjFhMDk3MTUxLWNmMDAtNGUxNC05MGYyLWI5ODAxZTU1NTk3MCIsImlhdCI6MTY3MTExNjk5OCwic2NvcGUiOiJyZWFkIHdyaXRlIn0.qX1GXEc8k0EAKsuZQaY2PvMsTORHSYvBq_suKNnfqyQ";
        TokenValidationResult validationResult = provider.isTokenValid(jwtWithInvalidSignature);
        assertFalse(validationResult.isValid());
        String message = validationResult.getMessage();
        assertNotNull(message);
        assertTrue(message.startsWith("Invalid JWT signature"));
    }

    @Test
    @DisplayName("Test isTokenValid with expired jwt token and expect TokenValidationResult with isValid is false and message is not null")
    void isTokenValidWithExpiredJwtTokenAndExpectSuccess() {
        String expiredJwt = getExpiredToken();
        TokenValidationResult validationResult = provider.isTokenValid(expiredJwt);
        assertFalse(validationResult.isValid());
        String message = validationResult.getMessage();
        assertNotNull(message);
        assertTrue(message.startsWith("JWT token is expired"));
    }

    @Test
    @DisplayName("Get claims from valid jwt token and expect success")
    void getClaims() {
        TokenMetadata metadata = getToken();
        Claims actualClaims = provider.getClaims(metadata.getToken());
        Map<String, Object> expectedClaims = metadata.getClaims();
        assertEquals(expectedClaims, actualClaims);
    }

    @Test
    @DisplayName("Get claims from invalid jwt token and expect null")
    void getClaimsFromInvalidJwtToken_AndExpectNull() {
        String jwtWithInvalidSignature = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NzExMjA1OTgsInN1YiI6IjFhMDk3MTUxLWNmMDAtNGUxNC05MGYyLWI5ODAxZTU1NTk3MCIsImlhdCI6MTY3MTExNjk5OCwic2NvcGUiOiJyZWFkIHdyaXRlIn0.qX1GXEc8k0EAKsuZQaY2PvMsTORHSYvBq_suKNnfqyQ";
        Claims actualClaims = provider.getClaims(jwtWithInvalidSignature);
        assertNull(actualClaims);
    }

    private TokenMetadata getToken() {
        Date issuedAt = Date.from(Instant.now());
        Date expiration = Date.from(Instant.now().plusSeconds(300));
        Map<String, Object> claims = Map.of(Claims.SUBJECT, USER_ID, Claims.ISSUED_AT, issuedAt.getTime(), Claims.EXPIRATION, expiration.getTime());
        String tokenValue = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, secretWord)
                .setClaims(claims)
                .compact();
        return new TokenMetadata(true, tokenValue, issuedAt.getTime(), expiration.getTime(), claims);
    }

    private String getExpiredToken() {
        long issuedAt = System.currentTimeMillis() / 1000;
        long expiredAt = (System.currentTimeMillis() / 1000);
        Map<String, Object> claims = Map.of(Claims.SUBJECT, USER_ID, Claims.ISSUED_AT, issuedAt, Claims.EXPIRATION, expiredAt);
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, secretWord)
                .setClaims(claims)
                .compact();
    }
}

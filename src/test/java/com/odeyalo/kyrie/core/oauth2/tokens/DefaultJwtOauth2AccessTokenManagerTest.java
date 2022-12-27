package com.odeyalo.kyrie.core.oauth2.tokens;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;
import com.odeyalo.kyrie.core.oauth2.client.ClientCredentialsValidator;
import com.odeyalo.kyrie.core.oauth2.tokens.code.*;
import com.odeyalo.kyrie.core.oauth2.tokens.code.provider.AuthorizationCodeProvider;
import com.odeyalo.kyrie.core.oauth2.tokens.code.provider.DefaultStoringAuthorizationCodeProvider;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.DefaultJwtOauth2AccessTokenGenerator;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.DefaultSecretWordJwtTokenProvider;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.JwtTokenProvider;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.Oauth2AccessTokenGenerator;
import com.odeyalo.kyrie.core.support.ValidationResult;
import com.odeyalo.kyrie.exceptions.InvalidAuthorizationCodeObtainTokenException;
import com.odeyalo.kyrie.exceptions.InvalidClientCredentialsException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DefaultJwtOauth2AccessTokenManager class.
 * @see DefaultJwtOauth2AccessTokenManager
 */
class DefaultJwtOauth2AccessTokenManagerTest {
    public static final String CLIENT_ID = "odeyalo";
    public static final String CLIENT_SECRET = "tired";
    public static final String SECRET_WORD = "secret";
    private static final String STORED_AUTHORIZATION_CODE_ID = "code_id";

    private static final String AUTHORIZATION_CODE_VALUE = "king";

    private final Oauth2User user = Oauth2User
            .builder()
            .id("1")
            .username("odeyalo")
            .password("pxsswxrd")
            .authorities(Set.of("USER"))
            .additionalInfo(Collections.emptyMap())
            .build();
    private final String[] SCOPES = new String[]{"read", "write"};

    private final AuthorizationCode authorizationCode = AuthorizationCode
            .builder()
            .issuedAt(Instant.now())
            .expiresIn(Instant.now().plusSeconds(60))
            .codeValue(AUTHORIZATION_CODE_VALUE)
            .scopes(SCOPES)
            .user(user)
            .build();

    private final ClientCredentialsValidator validator = (id, secret) -> {
        boolean validation = CLIENT_ID.equals(id) && CLIENT_SECRET.equals(secret);
        return validation ? ValidationResult.success() : ValidationResult.failed("Client id or client secret is not valid or is wrong");
    };
    private final JwtTokenProvider jwtTokenProvider = new DefaultSecretWordJwtTokenProvider(SECRET_WORD);
    private final Oauth2AccessTokenGenerator accessTokenGenerator = new DefaultJwtOauth2AccessTokenGenerator(jwtTokenProvider);
    private final AuthorizationCodeStore codeStore = new InMemoryAuthorizationCodeStore();
    private final AuthorizationCodeProvider authorizationCodeProvider = new DefaultStoringAuthorizationCodeProvider(new AuthorizationCodeGeneratorImpl(), codeStore);
    private final AuthorizationCodeManager authorizationCodeManager = new DefaultStoringAuthorizationCodeManager(authorizationCodeProvider, codeStore);

    private final DefaultAccessTokenReturner returner = new DefaultAccessTokenReturner(validator, authorizationCodeManager, accessTokenGenerator);

    private final DefaultJwtOauth2AccessTokenManager manager = new DefaultJwtOauth2AccessTokenManager(jwtTokenProvider, returner, accessTokenGenerator);


    @BeforeEach
    public void setup() {
        codeStore.save(STORED_AUTHORIZATION_CODE_ID, authorizationCode);
    }

    @AfterEach
    public void clear() {
        codeStore.deleteALl();
    }

    @Test
    @DisplayName("Generate access token and expect success")
    void generateAccessToken() {
        Oauth2AccessToken accessToken = manager.generateAccessToken(user, SCOPES);

        Oauth2AccessToken.TokenType tokenType = accessToken.getTokenType();
        Instant expiresIn = accessToken.getExpiresIn();
        Instant issuedAt = accessToken.getIssuedAt();
        String tokenValue = accessToken.getTokenValue();
        String scope = accessToken.getScope();

        // Assert that all required fields are presented and not null
        assertNotNull(tokenType, "Token type must be presented!");
        assertNotNull(expiresIn, "expiresIn type must be presented!");
        assertNotNull(issuedAt, "issuedAt type must be presented!");
        assertNotNull(tokenValue, "Token value type must be presented!");
        assertNotNull(scope, "Scopes must be presented!");

        assertEquals(String.join(" ", SCOPES), scope, "Scopes in AuthorizationCode and in Oauth2AccessToken must be equal");
        assertTrue(expiresIn.isAfter(issuedAt), "expiresIn must be greater than issuedAt");
        Claims claims = Jwts.parser().setSigningKey(SECRET_WORD).parseClaimsJws(tokenValue).getBody();
        Object scopes = claims.get(Oauth2AccessTokenGenerator.SCOPE);
        assertNotNull(scopes, "Access token must contain 'scope' claim and it cannot be null!");
        assertTrue(scopes instanceof String, "Access token must contain 'scope' claim which is a space-separated string");
        String parsedScopes = (String) scopes;
        assertEquals(String.join(" ", SCOPES), parsedScopes, "Scopes from jwt token claims and original scopes must be equal!");
    }

    @Test
    @DisplayName("Get token from token")
    void getTokenInfo() {
        String[] scopes = {"read"};

        Oauth2AccessToken expected = accessTokenGenerator.generateAccessToken(user, scopes);
        Oauth2AccessToken actual = manager.getTokenInfo(expected.getTokenValue());
        assertEquals(expected, actual, "getTokenInfo() method must return correct info about access token");
    }

    @Test
    @DisplayName("Get token from invalid token and expect message that token is expired")
    void getExpiredTokenInfoAndExpectMessage() {
        Oauth2AccessToken expected = Oauth2AccessToken.alreadyExpired();
        String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NzAzNDE0MTQsInN1YiI6IjdhMmRhODAyLTFkNjEtNGQ4MS04MDhlLWQwYmY5OTM4MTBmNCIsImlhdCI6MTY3MDMzNzgxNCwic2NvcGUiOiJyZWFkIHdyaXRlIn0.5NT_kbHXWayrj4mpjMQqqZ2C98tj_xpmeAfWNE-ikBk";
        Oauth2AccessToken actual = manager.getTokenInfo(expiredToken);
        assertEquals(expected, actual, "If token is invalid then Oauth2AccessToken.alreadyExpired() must be returned as result");
    }

    @Test
    @DisplayName("Validate valid access token and expect success")
    void validateValidAccessToken() {
        String[] scopes = {"read"};

        Oauth2AccessToken expected = accessTokenGenerator.generateAccessToken(user, scopes);
        TokenValidationResult validationResult = manager.validateAccessToken(expected.getTokenValue());
        assertTrue(validationResult.isValid());
    }

    @Test
    @DisplayName("Validate invalid access token and expect TokenValidationResult.invalid()")
    void validateInvalidAccessToken() {
        String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NzAzNDE0MTQsInN1YiI6IjdhMmRhODAyLTFkNjEtNGQ4MS04MDhlLWQwYmY5OTM4MTBmNCIsImlhdCI6MTY3MDMzNzgxNCwic2NvcGUiOiJyZWFkIHdyaXRlIn0.5NT_kbHXWayrj4mpjMQqqZ2C98tj_xpmeAfWNE-ikBk";
        TokenValidationResult validationResult = manager.validateAccessToken(expiredToken);
        assertFalse(validationResult.isValid(), "If token is invalid then false must be returned");
        assertNotNull(validationResult.getMessage(), "If token isn't valid then message with error description can't be null");
    }

    @Test
    @DisplayName("Obtain access token by not existing authorization code with correct client credentials and expect InvalidAuthorizationCodeObtainTokenException")
    void obtainAccessTokenByAuthorizationCodeValueUsingNotExistingAuthCodeWithCorrectCredentials_AndExpectError() {
        String notExistingCode = "garden";
        Oauth2ClientCredentials credentials = Oauth2ClientCredentials.of(CLIENT_ID, CLIENT_SECRET);
        assertThrows(InvalidAuthorizationCodeObtainTokenException.class, () -> returner.getToken(credentials, notExistingCode));
    }

    @Test
    @DisplayName("Obtain access token by not existing authorization code with wrong client credentials and expect exception")
    void obtainAccessTokenByAuthorizationCodeWithWrongAuthCodeAndWithWrongCredentials_AndExpectError() {
        String notExistingCode = "sorry";
        String invalidClientId = "iwas";
        String invalidClientSecret = "inshower";
        Oauth2ClientCredentials credentials = Oauth2ClientCredentials.of(invalidClientId, invalidClientSecret);
        assertThrows(InvalidClientCredentialsException.class, () -> returner.getToken(credentials, notExistingCode));
    }

    @Test
    @DisplayName("Obtain access token by existing authorization code with valid client credentials and expect success")
    void obtainAccessTokenByAuthorizationCodeUsingCorrectAuthCodeWithCorrectCredentials_AndExpectSuccess() {
        Oauth2ClientCredentials credentials = Oauth2ClientCredentials.of(CLIENT_ID, CLIENT_SECRET);
        Oauth2AccessToken accessToken = returner.getToken(credentials, AUTHORIZATION_CODE_VALUE);
        Oauth2AccessToken.TokenType tokenType = accessToken.getTokenType();
        Instant expiresIn = accessToken.getExpiresIn();
        Instant issuedAt = accessToken.getIssuedAt();
        String tokenValue = accessToken.getTokenValue();
        String scope = accessToken.getScope();
        // Assert that all required fields are presented and not null
        assertNotNull(tokenType, "Token type must be presented!");
        assertNotNull(expiresIn, "expiresIn type must be presented!");
        assertNotNull(issuedAt, "issuedAt type must be presented!");
        assertNotNull(tokenValue, "Token value type must be presented!");
        assertNotNull(scope, "Scopes must be presented!");

        assertEquals(String.join(" ", SCOPES), scope, "Scopes in AuthorizationCode and in Oauth2AccessToken must be equal");
        assertTrue(expiresIn.isAfter(issuedAt), "expiresIn must be greater than issuedAt");
        Claims claims = Jwts.parser().setSigningKey(SECRET_WORD).parseClaimsJws(tokenValue).getBody();
        Object scopes = claims.get(Oauth2AccessTokenGenerator.SCOPE);
        assertNotNull(scopes, "Access token must contain 'scope' claim and it cannot be null!");
        assertTrue(scopes instanceof String, "Access token must contain 'scope' claim which is a space-separated string");
        String parsedScopes = (String) scopes;
        assertEquals(String.join(" ", SCOPES), parsedScopes);
    }
}

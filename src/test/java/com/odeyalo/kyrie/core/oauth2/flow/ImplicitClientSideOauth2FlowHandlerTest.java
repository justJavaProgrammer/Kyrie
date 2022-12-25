package com.odeyalo.kyrie.core.oauth2.flow;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessToken;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.DefaultJwtOauth2AccessTokenGenerator;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.JwtTokenProviderImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ImplicitClientSideOauth2FlowHandler class.
 *
 * @version 1.0
 * @see ImplicitClientSideOauth2FlowHandler
 */
class ImplicitClientSideOauth2FlowHandlerTest {
    public final JwtTokenProviderImpl jwtTokenProvider = new JwtTokenProviderImpl("secret");
    private final ImplicitClientSideOauth2FlowHandler flowHandler = new ImplicitClientSideOauth2FlowHandler(
            new DefaultJwtOauth2AccessTokenGenerator(jwtTokenProvider)
    );


    @Test
    @DisplayName("Test default method 'handleClientSideFlow' and expect success in checks")
    void handleClientSideFlowAndExpectSuccess() {
        String[] scopes = {"read", "write"};
        AuthorizationRequest request = AuthorizationRequest
                .builder()
                .responseTypes(Oauth2ResponseType.CODE)
                .grantType(AuthorizationGrantType.IMPLICIT)
                .scopes(scopes)
                .clientId("client_123")
                .redirectUrl("http://localhost:6666/callback")
                .state("state123")
                .build();

        Oauth2User user = Oauth2User.builder().username("odeyalo").password("password").id("1").authorities(Set.of("USER")).additionalInfo(Collections.emptyMap()).build();

        Oauth2AccessToken token = flowHandler.handleClientSideFlow(request, user);
        assertNotNull(token);

        String scope = token.getScope();
        String tokenValue = token.getTokenValue();
        Instant expiresIn = token.getExpiresIn();
        Instant issuedAt = token.getIssuedAt();
        String tokenType = token.getTokenType().getValue();

        assertNotNull(tokenValue);
        assertNotNull(expiresIn);
        assertNotNull(issuedAt);
        assertNotNull(tokenType);
        assertTrue(expiresIn.isAfter(issuedAt), "The expires_in parameter must be greater than issued_at");
        assertTrue(jwtTokenProvider.isTokenValid(tokenValue).isValid());
        ;
        assertEquals(String.join(" ", scopes), scope);

    }

    @Test
    @DisplayName("Test 'handleFlow' method and expect success in checks")
    void handleFlowAndExpectSuccess() {
        String[] scopes = {"read", "write"};

        AuthorizationRequest request = AuthorizationRequest
                .builder()
                .responseTypes(Oauth2ResponseType.CODE)
                .grantType(AuthorizationGrantType.IMPLICIT)
                .clientId("client_123")
                .redirectUrl("http://localhost:6666/callback")
                .state("state123")
                .scopes(scopes)
                .build();
        Oauth2User user = Oauth2User.builder().username("odeyalo").password("password").id("1").authorities(Set.of("USER")).additionalInfo(Collections.emptyMap()).build();
        Oauth2AccessToken token = flowHandler.handleClientSideFlow(request, user);
        assertNotNull(token);

        String scope = token.getScope();
        String tokenValue = token.getTokenValue();
        Instant expiresIn = token.getExpiresIn();
        Instant issuedAt = token.getIssuedAt();
        String tokenType = token.getTokenType().getValue();

        assertNotNull(tokenValue);
        assertNotNull(expiresIn);
        assertNotNull(issuedAt);
        assertNotNull(tokenType);
        assertTrue(expiresIn.isAfter(issuedAt), "The expires_in parameter must be greater than issued_at");
        assertTrue(jwtTokenProvider.isTokenValid(tokenValue).isValid());
        ;
        assertEquals(String.join(" ", scopes), scope);
    }

    @Test
    @DisplayName("Expect implicit flow name from 'getFlowName' method")
    void getFlowName() {
        String actual = flowHandler.getFlowName();
        String expected = AuthorizationGrantType.IMPLICIT.getGrantName();
        assertEquals(expected, actual, "The ImplicitFlowHandler must return implicit grant type as flow name");
    }
}

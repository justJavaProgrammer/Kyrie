package com.odeyalo.kyrie.core.oauth2.support;

import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.oauth2.CombinedOauth2Token;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ImplicitFlowRedirectUrlCreationService class.
 *
 * @see ImplicitFlowRedirectUrlCreationService
 */
class ImplicitFlowRedirectUrlCreationServiceTest {
    private final ImplicitFlowRedirectUrlCreationService redirectUrlCreationService = new ImplicitFlowRedirectUrlCreationService();

    @Test
    @DisplayName("Create redirect url using implicit flow with state and expect state, token, expires_in and token type params in redirect url")
    void createRedirectUrlWithStateAndExpectResultWithStatePresented() {
        String rootRedirectUrl = "http://localhost:6666/callback";
        String state = "state123";
        String accessTokenValue = "youdontwannadiewithaguylikeme";
        String[] scopes = {"read", "write"};
        AuthorizationRequest request = AuthorizationRequest
                .builder()
                .redirectUrl(rootRedirectUrl)
                .state(state)
                .grantType(AuthorizationGrantType.IMPLICIT)
                .responseTypes(Oauth2ResponseType.TOKEN)
                .scopes(scopes)
                .build();

        Oauth2AccessToken accessToken = Oauth2AccessToken
                .builder()
                .issuedAt(Instant.now())
                .expiresIn(Instant.now().plusSeconds(30))
                .tokenValue(accessTokenValue)
                .tokenType(Oauth2AccessToken.TokenType.BEARER)
                .build();
        String redirectUrl = redirectUrlCreationService.createRedirectUrl(request, accessToken);
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(redirectUrl).build();
        assertNotNull(redirectUrl);
        assertTrue(redirectUrl.startsWith(rootRedirectUrl));
        MultiValueMap<String, String> params = uriComponents.getQueryParams();
        assertEquals(4, params.size());
        assertEquals(state, params.getFirst(Oauth2Constants.STATE));
        assertEquals(accessTokenValue, params.getFirst(Oauth2Constants.ACCESS_TOKEN));
        assertEquals(Oauth2AccessToken.TokenType.BEARER.getValue(), params.getFirst(Oauth2Constants.TOKEN_TYPE));
        assertNotNull(params.getFirst(Oauth2Constants.EXPIRES_IN));
    }


    @Test
    @DisplayName("Create redirect url using implicit flow without state and expect token, expires_in and token type params in redirect url")
    void createRedirectUrlWithoutStateAndExpectResultWithioutStateParam() {
        String rootRedirectUrl = "http://localhost:6666/callback";
        String accessTokenValue = "youdontwannadiewithaguylikeme";
        String[] scopes = {"read", "write"};
        AuthorizationRequest request = AuthorizationRequest
                .builder()
                .redirectUrl(rootRedirectUrl)
                .grantType(AuthorizationGrantType.IMPLICIT)
                .responseTypes(Oauth2ResponseType.TOKEN)
                .scopes(scopes)
                .build();

        Oauth2AccessToken accessToken = Oauth2AccessToken
                .builder()
                .issuedAt(Instant.now())
                .expiresIn(Instant.now().plusSeconds(30))
                .tokenValue(accessTokenValue)
                .tokenType(Oauth2AccessToken.TokenType.BEARER)
                .build();
        String redirectUrl = redirectUrlCreationService.createRedirectUrl(request, accessToken);
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(redirectUrl).build();

        assertNotNull(redirectUrl);
        MultiValueMap<String, String> params = uriComponents.getQueryParams();
        assertEquals(3, params.size());
        assertTrue(redirectUrl.startsWith(rootRedirectUrl));

        assertNull(params.getFirst(Oauth2Constants.STATE));
        assertEquals(accessTokenValue, params.getFirst(Oauth2Constants.ACCESS_TOKEN));
        assertEquals(Oauth2AccessToken.TokenType.BEARER.getValue(), params.getFirst(Oauth2Constants.TOKEN_TYPE));
        assertNotNull(params.getFirst(Oauth2Constants.EXPIRES_IN));
    }


    @Test
    @DisplayName("Create redirect url with wrong Oauth2Token and expect error")
    void createRedirectUrlWithWrongOauth2TokenAndExpectError() {
        String rootRedirectUrl = "http://localhost:6666/callback";
        String accessTokenValue = "youdontwannadiewithaguylikeme";
        String[] scopes = {"read", "write"};
        AuthorizationRequest request = AuthorizationRequest
                .builder()
                .redirectUrl(rootRedirectUrl)
                .grantType(AuthorizationGrantType.IMPLICIT)
                .responseTypes(Oauth2ResponseType.TOKEN)
                .scopes(scopes)
                .build();

        CombinedOauth2Token token = CombinedOauth2Token
                .builder()
                .tokenValue(accessTokenValue)
                .issuedAt(Instant.now())
                .expiresIn(Instant.now().plusSeconds(60))
                .build();

        assertThrows(UnsupportedOperationException.class, () -> redirectUrlCreationService.createRedirectUrl(request, token));
    }

    @Test
    @DisplayName("Check if supported grant type is same as expected")
    void supportedGrantType() {
        AuthorizationGrantType expected = AuthorizationGrantType.IMPLICIT;
        AuthorizationGrantType actual = redirectUrlCreationService.supportedGrantType();
        assertEquals(expected, actual);
    }
}

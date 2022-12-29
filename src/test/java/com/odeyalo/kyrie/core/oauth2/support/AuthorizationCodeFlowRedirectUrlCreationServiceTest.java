package com.odeyalo.kyrie.core.oauth2.support;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessToken;
import com.odeyalo.kyrie.core.oauth2.tokens.code.AuthorizationCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AuthorizationCodeFlowRedirectUrlCreationService class.
 * @see AuthorizationCodeFlowRedirectUrlCreationService
 */
class AuthorizationCodeFlowRedirectUrlCreationServiceTest {
    private final AuthorizationCodeFlowRedirectUrlCreationService redirectUrlCreationService = new AuthorizationCodeFlowRedirectUrlCreationService();

    @Test
    @DisplayName("Create redirect url with state param and expect code and state in redirect url")
    void createRedirectUrlWithStateAndExpectStateAndCode() {
        String rootRedirectUrl = "http://localhost:6666/callback";
        String state = "state123";
        String authCodeValue = "helpmeiwanttokillmyself";
        String[] scopes = {"read", "write"};
        AuthorizationRequest request = AuthorizationRequest
                .builder()
                .redirectUrl(rootRedirectUrl)
                .state(state)
                .grantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .responseTypes(Oauth2ResponseType.CODE)
                .scopes(scopes)
                .build();
        Oauth2User user = new Oauth2User("1", "odeyalo", "password", Set.of("USER"),
                Collections.emptyMap());

        AuthorizationCode authorizationCode = AuthorizationCode
                .builder()
                .codeValue(authCodeValue)
                .user(user)
                .expiresIn(LocalDateTime.now()
                .plusSeconds(60))
                .scopes(scopes)
                .build();

        String createdRedirectUrl = redirectUrlCreationService.createRedirectUrl(request, authorizationCode);
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(createdRedirectUrl).build();

        MultiValueMap<String, String> params = uriComponents.getQueryParams();
        assertEquals(2, params.size());
        assertEquals(state, params.getFirst(Oauth2Constants.STATE),
                "If state parameter was presented in authorization request then redirect url must contain same state as provided in authorization request");
        assertEquals(authCodeValue, params.getFirst(Oauth2ResponseType.CODE.getSimplifiedName()), "Redirect url must contain code parameter");
    }

    @Test
    @DisplayName("Create redirect url withiout state param and expect only code in redirect url")
    void createRedirectUrlWithoutStateAndExpectOnlyCode() {
        String rootRedirectUrl = "http://localhost:6666/callback";
        String authCodeValue = "helpmeiwanttokillmyself";
        String[] scopes = {"read", "write"};
        AuthorizationRequest request = AuthorizationRequest
                .builder()
                .redirectUrl(rootRedirectUrl)
                .grantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .responseTypes(Oauth2ResponseType.CODE)
                .scopes(scopes)
                .build();
        Oauth2User user = new Oauth2User("1", "odeyalo", "password", Set.of("USER"),
                Collections.emptyMap());

        AuthorizationCode authorizationCode = AuthorizationCode.builder()
                .codeValue(authCodeValue)
                .user(user)
                .expiresIn(LocalDateTime.now().plusSeconds(60))
                .scopes(scopes)
                .build();

        String createdRedirectUrl = redirectUrlCreationService.createRedirectUrl(request, authorizationCode);
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(createdRedirectUrl).build();

        MultiValueMap<String, String> params = uriComponents.getQueryParams();
        assertEquals(1, params.size());
        assertNull(params.get(Oauth2Constants.STATE), "If state is not presented then redirect url must contain only an authorization code");
        assertEquals(authCodeValue, params.getFirst(Oauth2ResponseType.CODE.getSimplifiedName()), "Redirect url must contain an authorization code");
    }

    @Test
    @DisplayName("Test redirect uri creation with wrong oauth2 token type and expect exception")
    void testUrlCreationWithWrongOauth2TokenAndExpectError() {
        String rootRedirectUrl = "http://localhost:6666/callback";
        String state = "state123";
        String authCodeValue = "helpmeiwanttokillmyself";
        String[] scopes = {"read", "write"};
        AuthorizationRequest request = AuthorizationRequest
                .builder()
                .redirectUrl(rootRedirectUrl)
                .grantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .responseTypes(Oauth2ResponseType.CODE)
                .scopes(scopes)
                .state(state)
                .build();

        Oauth2AccessToken accessToken = Oauth2AccessToken.builder().tokenValue("token_value").issuedAt(Instant.now()).expiresIn(Instant.now().plusSeconds(60)).build();
        assertThrows(UnsupportedOperationException.class, () -> redirectUrlCreationService.createRedirectUrl(request, accessToken));

    }

    @Test
    @DisplayName("Check supported grant type")
    void supportedGrantType() {
        AuthorizationGrantType actual = redirectUrlCreationService.supportedGrantType();
        AuthorizationGrantType expected = AuthorizationGrantType.AUTHORIZATION_CODE;
        assertEquals(expected, actual);
    }
}

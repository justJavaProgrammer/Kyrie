package com.odeyalo.kyrie.core.oauth2.support;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.oauth2.CombinedOauth2Token;
import com.odeyalo.kyrie.core.oauth2.flow.MultipleResponseTypeOidcOauth2FlowHandler;
import com.odeyalo.kyrie.core.oauth2.oidc.OidcResponseType;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessToken;
import com.odeyalo.kyrie.core.oauth2.tokens.code.AuthorizationCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for MultipleResponseTypeFlowRedirectUrlCreationService class.
 * @see MultipleResponseTypeFlowRedirectUrlCreationService
 * @version 1.0
 */
class MultipleResponseTypeFlowRedirectUrlCreationServiceTest {
    private final MultipleResponseTypeFlowRedirectUrlCreationService multipleResponseTypeFlowRedirectUrlCreationService = new MultipleResponseTypeFlowRedirectUrlCreationService();
    private static final String STATE_QUERY_PARAM_KEY = "state";
    private static final String REDIRECT_URL = "http://localhost:9000/callback";
    private static final String STATE = "state123";
    private static final String CLIENT_ID = "com.user.kyrie.900000012342";
    private static final String[] READ_WRITE_SCOPES = {"read", "write"};
    private static final String EXPECTED_AUTH_CODE_VALUE = "auth_code_test";
    private static final String EXPECTED_ID_TOKEN_VALUE = "id_token_test";
    private static final String EXPECTED_ACCESS_TOKEN_VALUE = "access_token_test";
    private static final String EXPECTED_ACCESS_TOKEN_TYPE_VALUE = "Bearer";

    /**
     * Testing code id_token response types and expect successful result
     */
    @Test
    @DisplayName("Create redirect url for Id Token and Authorization Code response types and expect success")
    void createRedirectUrlWithAuthCodeWithIdTokenAndExpectSuccess() {

        AuthorizationRequest request = AuthorizationRequest
                .builder()
                .redirectUrl(REDIRECT_URL)
                .state(STATE)
                .responseTypes(Oauth2ResponseType.CODE, OidcResponseType.ID_TOKEN)
                .clientId(CLIENT_ID)
                .grantType(AuthorizationGrantType.MULTIPLE)
                .scopes(READ_WRITE_SCOPES)
                .build();

        String actualUrl = multipleResponseTypeFlowRedirectUrlCreationService.createRedirectUrl(request, new CombinedOauth2Token(
                Map.of(
                        MultipleResponseTypeOidcOauth2FlowHandler.ID_TOKEN_KEY, EXPECTED_ID_TOKEN_VALUE,
                        MultipleResponseTypeOidcOauth2FlowHandler.AUTHORIZATION_CODE_TOKEN_KEY, EXPECTED_AUTH_CODE_VALUE)
        ));

        MultiValueMap<String, String> params = UriComponentsBuilder.fromHttpUrl(actualUrl).build().getQueryParams();
        assertNotEquals(0, params.size());
        assertEquals(STATE, params.getFirst(STATE_QUERY_PARAM_KEY));
        assertNotNull(params.getFirst(MultipleResponseTypeOidcOauth2FlowHandler.ID_TOKEN_KEY));
        assertNotNull(params.getFirst(MultipleResponseTypeOidcOauth2FlowHandler.AUTHORIZATION_CODE_TOKEN_KEY));
        assertEquals(EXPECTED_ID_TOKEN_VALUE, params.getFirst(MultipleResponseTypeOidcOauth2FlowHandler.ID_TOKEN_KEY));
        assertEquals(EXPECTED_AUTH_CODE_VALUE, params.getFirst(MultipleResponseTypeOidcOauth2FlowHandler.AUTHORIZATION_CODE_TOKEN_KEY));
    }

    /**
     * Testing code id_token token response types and expect successful result
     */
    @Test
    @DisplayName("Create redirect url for Id Token, Authorization Code and Access Token response types and expect success")
    void createRedirectUrlWithAuthCodeWithIdTokenWithAccessTokenAndExpectSuccess() {

        AuthorizationRequest request = AuthorizationRequest
                .builder()
                .redirectUrl(REDIRECT_URL)
                .state(STATE)
                .responseTypes(OidcResponseType.ID_TOKEN, Oauth2ResponseType.CODE, Oauth2ResponseType.TOKEN)
                .clientId(CLIENT_ID)
                .grantType(AuthorizationGrantType.MULTIPLE)
                .scopes(READ_WRITE_SCOPES)
                .build();

        CombinedOauth2Token token = CombinedOauth2Token.builder()
                .addInfo(MultipleResponseTypeOidcOauth2FlowHandler.ID_TOKEN_KEY, EXPECTED_ID_TOKEN_VALUE)
                .addInfo(MultipleResponseTypeOidcOauth2FlowHandler.AUTHORIZATION_CODE_TOKEN_KEY, EXPECTED_AUTH_CODE_VALUE)
                .addInfo(Oauth2Constants.TOKEN_TYPE, EXPECTED_ACCESS_TOKEN_TYPE_VALUE)
                .tokenValue(EXPECTED_ACCESS_TOKEN_VALUE)
                .issuedAt(LocalDateTime.now().toInstant(ZoneOffset.UTC))
                .expiresIn(LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.UTC)).build();


        String actualUrl = multipleResponseTypeFlowRedirectUrlCreationService.createRedirectUrl(request, token);

        MultiValueMap<String, String> params = UriComponentsBuilder.fromHttpUrl(actualUrl).build().getQueryParams();
        assertNotEquals(0, params.size());


        assertNotNull(params.getFirst(MultipleResponseTypeOidcOauth2FlowHandler.ID_TOKEN_KEY));
        assertNotNull(params.getFirst(MultipleResponseTypeOidcOauth2FlowHandler.AUTHORIZATION_CODE_TOKEN_KEY));
        assertNotNull(params.getFirst(MultipleResponseTypeOidcOauth2FlowHandler.ACCESS_TOKEN_KEY));
        assertNotNull(params.getFirst(Oauth2Constants.TOKEN_TYPE));
        assertNotNull(params.getFirst(Oauth2Constants.EXPIRES_IN));

        assertEquals(STATE, params.getFirst(STATE_QUERY_PARAM_KEY));
        assertEquals(EXPECTED_ID_TOKEN_VALUE, params.getFirst(MultipleResponseTypeOidcOauth2FlowHandler.ID_TOKEN_KEY));
        assertEquals(EXPECTED_AUTH_CODE_VALUE, params.getFirst(MultipleResponseTypeOidcOauth2FlowHandler.AUTHORIZATION_CODE_TOKEN_KEY));
        assertEquals(EXPECTED_ACCESS_TOKEN_VALUE, params.getFirst(MultipleResponseTypeOidcOauth2FlowHandler.ACCESS_TOKEN_KEY));
        assertEquals(EXPECTED_ACCESS_TOKEN_TYPE_VALUE, params.getFirst(Oauth2Constants.TOKEN_TYPE));
        assertEquals(3600, Integer.parseInt(params.getFirst(Oauth2Constants.EXPIRES_IN)));
    }

    /**
     * Testing code id_token token response types and expect successful result
     */
    @Test
    @DisplayName("Create redirect url for Id Token and Access Token response types and expect success")
    void createRedirectUrlWithIdTokenWithAccessTokenAndExpectSuccess() {

        AuthorizationRequest request = AuthorizationRequest
                .builder()
                .redirectUrl(REDIRECT_URL)
                .state(STATE)
                .clientId(CLIENT_ID)
                .responseTypes(OidcResponseType.ID_TOKEN, Oauth2ResponseType.TOKEN)
                .grantType(AuthorizationGrantType.MULTIPLE)
                .scopes(READ_WRITE_SCOPES)
                .build();

        CombinedOauth2Token token = CombinedOauth2Token.builder()
                .addInfo(MultipleResponseTypeOidcOauth2FlowHandler.ID_TOKEN_KEY, EXPECTED_ID_TOKEN_VALUE)
                .addInfo(Oauth2Constants.TOKEN_TYPE, EXPECTED_ACCESS_TOKEN_TYPE_VALUE)
                .tokenValue(EXPECTED_ACCESS_TOKEN_VALUE)
                .issuedAt(LocalDateTime.now().toInstant(ZoneOffset.UTC))
                .expiresIn(LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.UTC)).build();


        String actualUrl = multipleResponseTypeFlowRedirectUrlCreationService.createRedirectUrl(request, token);

        MultiValueMap<String, String> params = UriComponentsBuilder.fromHttpUrl(actualUrl).build().getQueryParams();
        assertNotEquals(0, params.size());


        assertNotNull(params.getFirst(MultipleResponseTypeOidcOauth2FlowHandler.ID_TOKEN_KEY));
        assertNotNull(params.getFirst(MultipleResponseTypeOidcOauth2FlowHandler.ACCESS_TOKEN_KEY));
        assertNotNull(params.getFirst(Oauth2Constants.TOKEN_TYPE));
        assertNotNull(params.getFirst(Oauth2Constants.EXPIRES_IN));

        assertEquals(STATE, params.getFirst(STATE_QUERY_PARAM_KEY));
        assertEquals(EXPECTED_ID_TOKEN_VALUE, params.getFirst(MultipleResponseTypeOidcOauth2FlowHandler.ID_TOKEN_KEY));
        assertEquals(EXPECTED_ACCESS_TOKEN_VALUE, params.getFirst(MultipleResponseTypeOidcOauth2FlowHandler.ACCESS_TOKEN_KEY));
        assertEquals(EXPECTED_ACCESS_TOKEN_TYPE_VALUE, params.getFirst(Oauth2Constants.TOKEN_TYPE));
        assertEquals(3600, Integer.parseInt(params.getFirst(Oauth2Constants.EXPIRES_IN)));
    }

    /**
     * Testing code token code response types and expect successful result
     */
    @Test
    @DisplayName("Create redirect url for Access Token and Authorization Code response types and expect success")
    void createRedirectUrlWithAccessTokenAndAuthorizationCodeAndExpectSuccess() {

        AuthorizationRequest request = AuthorizationRequest
                .builder()
                .redirectUrl(REDIRECT_URL)
                .state(STATE)
                .clientId(CLIENT_ID)
                .responseTypes(OidcResponseType.CODE, Oauth2ResponseType.TOKEN)
                .grantType(AuthorizationGrantType.MULTIPLE)
                .scopes(READ_WRITE_SCOPES)
                .build();

        CombinedOauth2Token token = CombinedOauth2Token.builder()
                .addInfo(MultipleResponseTypeOidcOauth2FlowHandler.AUTHORIZATION_CODE_TOKEN_KEY, EXPECTED_AUTH_CODE_VALUE)
                .addInfo(Oauth2Constants.TOKEN_TYPE, EXPECTED_ACCESS_TOKEN_TYPE_VALUE)
                .tokenValue(EXPECTED_ACCESS_TOKEN_VALUE)
                .issuedAt(LocalDateTime.now().toInstant(ZoneOffset.UTC))
                .expiresIn(LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.UTC)).build();


        String actualUrl = multipleResponseTypeFlowRedirectUrlCreationService.createRedirectUrl(request, token);

        MultiValueMap<String, String> params = UriComponentsBuilder.fromHttpUrl(actualUrl).build().getQueryParams();
        assertNotEquals(0, params.size());


        assertNull(params.getFirst(MultipleResponseTypeOidcOauth2FlowHandler.ID_TOKEN_KEY));
        assertNotNull(params.getFirst(MultipleResponseTypeOidcOauth2FlowHandler.AUTHORIZATION_CODE_TOKEN_KEY));
        assertNotNull(params.getFirst(MultipleResponseTypeOidcOauth2FlowHandler.ACCESS_TOKEN_KEY));
        assertNotNull(params.getFirst(Oauth2Constants.TOKEN_TYPE));
        assertNotNull(params.getFirst(Oauth2Constants.EXPIRES_IN));

        assertEquals(STATE, params.getFirst(STATE_QUERY_PARAM_KEY));
        assertEquals(EXPECTED_AUTH_CODE_VALUE, params.getFirst(MultipleResponseTypeOidcOauth2FlowHandler.AUTHORIZATION_CODE_TOKEN_KEY));
        assertEquals(EXPECTED_ACCESS_TOKEN_VALUE, params.getFirst(MultipleResponseTypeOidcOauth2FlowHandler.ACCESS_TOKEN_KEY));
        assertEquals(EXPECTED_ACCESS_TOKEN_TYPE_VALUE, params.getFirst(Oauth2Constants.TOKEN_TYPE));
        assertEquals(3600, Integer.parseInt(params.getFirst(Oauth2Constants.EXPIRES_IN)));
    }

    /**
     * Testing with only single response types and expect exception, because MultipleResponseTypeFlowRedirectUrlCreationService does not support single response type
     */
    @Test
    @DisplayName("Create redirect url for single response type and expect exception")
    void createRedirectUrlWithAuthCodeExpectReject() {

        AuthorizationRequest request = AuthorizationRequest
                .builder()
                .redirectUrl(REDIRECT_URL)
                .state(STATE)
                .clientId(CLIENT_ID)
                .grantType(AuthorizationGrantType.MULTIPLE)
                .scopes(READ_WRITE_SCOPES)
                .build();

        CombinedOauth2Token token = CombinedOauth2Token.builder()
                .addInfo(MultipleResponseTypeOidcOauth2FlowHandler.AUTHORIZATION_CODE_TOKEN_KEY, EXPECTED_AUTH_CODE_VALUE)
                .issuedAt(LocalDateTime.now().toInstant(ZoneOffset.UTC))
                .expiresIn(LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.UTC)).build();


        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            multipleResponseTypeFlowRedirectUrlCreationService.createRedirectUrl(request, token);
        });

        String message = exception.getMessage();
        assertNotNull(message);
    }


    @Test
    @DisplayName("createRedirectUrl with wrong number of response types and expect exception")
    void createRedirectUrlWithWrongNumberResponseTypes_AndExpectException() {
        AuthorizationRequest request = AuthorizationRequest
                .builder()
                .redirectUrl(REDIRECT_URL)
                .state(STATE)
                .clientId(CLIENT_ID)
                .grantType(AuthorizationGrantType.MULTIPLE)
                .scopes(READ_WRITE_SCOPES)
                .responseTypes(Oauth2ResponseType.TOKEN)
                .build();

        CombinedOauth2Token token = CombinedOauth2Token.builder()
                .addInfo(MultipleResponseTypeOidcOauth2FlowHandler.AUTHORIZATION_CODE_TOKEN_KEY, EXPECTED_AUTH_CODE_VALUE)
                .issuedAt(LocalDateTime.now().toInstant(ZoneOffset.UTC))
                .expiresIn(LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.UTC)).build();


        assertThrows(UnsupportedOperationException.class, () -> multipleResponseTypeFlowRedirectUrlCreationService.createRedirectUrl(request, token),
                "If response types is less than 2 then the exception must be thrown");
    }


    @Test
    @DisplayName("createRedirectUrl with wrong Oauth2Token and expect exception")
    void testCreateRedirectUrlWithWrongOauth2Token_AndExpectException() {
        AuthorizationRequest request = AuthorizationRequest
                .builder()
                .redirectUrl(REDIRECT_URL)
                .state(STATE)
                .clientId(CLIENT_ID)
                .grantType(AuthorizationGrantType.MULTIPLE)
                .scopes(READ_WRITE_SCOPES)
                .responseTypes(Oauth2ResponseType.TOKEN, Oauth2ResponseType.CODE)
                .build();

        Oauth2AccessToken accessToken = Oauth2AccessToken.builder().tokenValue("token_value").issuedAt(Instant.now()).expiresIn(Instant.now().plusSeconds(60)).build();
        assertThrows(UnsupportedOperationException.class, () -> multipleResponseTypeFlowRedirectUrlCreationService.createRedirectUrl(request, accessToken));

    }

    @Test
    @DisplayName("Check supported type")
    void supportedType() {
        assertEquals(AuthorizationGrantType.MULTIPLE, multipleResponseTypeFlowRedirectUrlCreationService.supportedGrantType());
    }
}

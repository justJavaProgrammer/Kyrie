package com.odeyalo.kyrie.core.oauth2.flow;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.oauth2.CombinedOauth2Token;
import com.odeyalo.kyrie.core.oauth2.Oauth2Token;
import com.odeyalo.kyrie.core.oauth2.oidc.OidcResponseType;
import com.odeyalo.kyrie.core.oauth2.oidc.generator.OidcIdTokenGeneratorImpl;
import com.odeyalo.kyrie.core.oauth2.oidc.generator.OidcOauth2TokenGeneratorFacadeImpl;
import com.odeyalo.kyrie.core.oauth2.support.Oauth2Constants;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessToken;
import com.odeyalo.kyrie.core.oauth2.tokens.code.AuthorizationCode;
import com.odeyalo.kyrie.core.oauth2.tokens.code.AuthorizationCodeGeneratorImpl;
import com.odeyalo.kyrie.core.oauth2.tokens.code.InMemoryAuthorizationCodeStore;
import com.odeyalo.kyrie.core.oauth2.tokens.code.provider.DefaultAuthorizationCodeProvider;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.DefaultJwtOauth2AccessTokenGenerator;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.JwtTokenProviderImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DefaultMultipleResponseTypeOidcOauth2FlowHandler class.
 * @see DefaultMultipleResponseTypeOidcOauth2FlowHandler
 */
class DefaultMultipleResponseTypeOidcOauth2FlowHandlerTest {
    private final JwtTokenProviderImpl provider = new JwtTokenProviderImpl("secret");
    private final InMemoryAuthorizationCodeStore store = new InMemoryAuthorizationCodeStore();

    private final DefaultMultipleResponseTypeOidcOauth2FlowHandler handler = new DefaultMultipleResponseTypeOidcOauth2FlowHandler(
            new OidcOauth2TokenGeneratorFacadeImpl(
                    new OidcIdTokenGeneratorImpl(provider), Collections.emptyList()),
            new DefaultJwtOauth2AccessTokenGenerator(provider),
            new DefaultAuthorizationCodeProvider(new AuthorizationCodeGeneratorImpl(), store));

    private static final String USER_ID = "1";

    @Test
    @DisplayName("Handle multiple response type flow with token and id_token and expect success")
    void handleFlowWithTokenAndIdTokenResponseTypesAndExpectSuccess() {
        String[] scopes = {"read", "write"};
        AuthorizationRequest request = AuthorizationRequest
                .builder()
                .clientId("client_1235")
                .scopes(scopes)
                .responseTypes(Oauth2ResponseType.TOKEN, OidcResponseType.ID_TOKEN)
                .grantType(AuthorizationGrantType.MULTIPLE)
                .build();
        Oauth2User expectedUser = new Oauth2User(USER_ID, "Odeyalo", "password", Set.of("USER"), Collections.emptyMap());

        Oauth2Token oauth2Token = handler.handleFlow(request, expectedUser);
        assertNotNull(oauth2Token, "DefaultMultipleResponseTypeOidcOauth2FlowHandler can't return null value");
        assertTrue(oauth2Token instanceof CombinedOauth2Token, "DefaultMultipleResponseTypeOidcOauth2FlowHandler must return CombinedOauth2Token");

        CombinedOauth2Token combinedOauth2Token = (CombinedOauth2Token) oauth2Token;
        String tokenValue = combinedOauth2Token.getTokenValue();
        Instant expiresIn = combinedOauth2Token.getExpiresIn();
        Instant issuedAt = combinedOauth2Token.getIssuedAt();

        assertNotNull(tokenValue);
        assertNotNull(expiresIn);
        assertNotNull(issuedAt);

        Map<String, Object> additionalInfo = combinedOauth2Token.getAdditionalInfo();
        Object accessTokenValue = additionalInfo.get(MultipleResponseTypeOidcOauth2FlowHandler.ACCESS_TOKEN_KEY);
        Object idTokenValue = additionalInfo.get(MultipleResponseTypeOidcOauth2FlowHandler.ID_TOKEN_KEY);
        Object authCodeValue = additionalInfo.get(MultipleResponseTypeOidcOauth2FlowHandler.AUTHORIZATION_CODE_TOKEN_KEY);

        assertNull(authCodeValue);
        assertNotNull(accessTokenValue);
        assertNotNull(idTokenValue);
        assertTrue(accessTokenValue instanceof Oauth2AccessToken);

        Oauth2AccessToken accessToken = (Oauth2AccessToken) accessTokenValue;

        String scope = accessToken.getScope();

        assertEquals(String.join(" ", List.of(scopes)), scope);
        assertEquals(combinedOauth2Token.getTokenValue(), accessToken.getTokenValue());
    }


    @Test
    @DisplayName("Handle multiple response type flow with code and id_token and expect success")
    void handleFlowWithCodeAndIdTokenResponseTypesAndExpectSuccess() {
        AuthorizationRequest request = AuthorizationRequest
                .builder()
                .clientId("client_1235")
                .scopes(new String[]{"read", "write"})
                .responseTypes(Oauth2ResponseType.CODE, OidcResponseType.ID_TOKEN)
                .grantType(AuthorizationGrantType.MULTIPLE)
                .build();
        Oauth2User expectedUser = new Oauth2User(USER_ID, "Odeyalo", "password", Set.of("USER"), Collections.emptyMap());

        Oauth2Token oauth2Token = handler.handleFlow(request, expectedUser);
        assertNotNull(oauth2Token, "DefaultMultipleResponseTypeOidcOauth2FlowHandler can't return null value");
        assertTrue(oauth2Token instanceof CombinedOauth2Token, "DefaultMultipleResponseTypeOidcOauth2FlowHandler must return CombinedOauth2Token");

        CombinedOauth2Token combinedOauth2Token = (CombinedOauth2Token) oauth2Token;
        String tokenValue = combinedOauth2Token.getTokenValue();
        Instant expiresIn = combinedOauth2Token.getExpiresIn();
        Instant issuedAt = combinedOauth2Token.getIssuedAt();

        assertNull(tokenValue);
        assertNull(expiresIn);
        assertNull(issuedAt);

        Map<String, Object> additionalInfo = combinedOauth2Token.getAdditionalInfo();
        Object accessTokenValue = additionalInfo.get(MultipleResponseTypeOidcOauth2FlowHandler.ACCESS_TOKEN_KEY);
        Object idTokenValue = additionalInfo.get(MultipleResponseTypeOidcOauth2FlowHandler.ID_TOKEN_KEY);
        Object authCodeValue = additionalInfo.get(MultipleResponseTypeOidcOauth2FlowHandler.AUTHORIZATION_CODE_TOKEN_KEY);

        assertNull(accessTokenValue);
        assertNotNull(idTokenValue);
        assertNotNull(authCodeValue);

        AuthorizationCode authorizationCode = store.findByAuthorizationCodeValue((String) authCodeValue);
        assertNotNull(authorizationCode);
    }

    @Test
    @DisplayName("Handle multiple response type flow with code and token and expect success")
    void handleFlowWithCodeAndTokenResponseTypesAndExpectSuccess() {
        String[] scopes = {"read", "write"};
        AuthorizationRequest request = AuthorizationRequest
                .builder()
                .clientId("client_1235")
                .scopes(scopes)
                .responseTypes(Oauth2ResponseType.CODE, Oauth2ResponseType.TOKEN)
                .grantType(AuthorizationGrantType.MULTIPLE)
                .build();
        Oauth2User expectedUser = new Oauth2User(USER_ID, "Odeyalo", "password", Set.of("USER"), Collections.emptyMap());

        Oauth2Token oauth2Token = handler.handleFlow(request, expectedUser);
        assertNotNull(oauth2Token, "DefaultMultipleResponseTypeOidcOauth2FlowHandler can't return null value");
        assertTrue(oauth2Token instanceof CombinedOauth2Token, "DefaultMultipleResponseTypeOidcOauth2FlowHandler must return CombinedOauth2Token");

        CombinedOauth2Token combinedOauth2Token = (CombinedOauth2Token) oauth2Token;
        String tokenValue = combinedOauth2Token.getTokenValue();
        Instant expiresIn = combinedOauth2Token.getExpiresIn();
        Instant issuedAt = combinedOauth2Token.getIssuedAt();

        assertNotNull(tokenValue);
        assertNotNull(expiresIn);
        assertNotNull(issuedAt);

        Map<String, Object> additionalInfo = combinedOauth2Token.getAdditionalInfo();
        Object accessTokenValue = additionalInfo.get(MultipleResponseTypeOidcOauth2FlowHandler.ACCESS_TOKEN_KEY);
        Object tokenTypeValue = additionalInfo.get(Oauth2Constants.TOKEN_TYPE);
        Object expiresInValue = additionalInfo.get(Oauth2Constants.EXPIRES_IN);
        Object idTokenValue = additionalInfo.get(MultipleResponseTypeOidcOauth2FlowHandler.ID_TOKEN_KEY);
        Object authCodeValue = additionalInfo.get(MultipleResponseTypeOidcOauth2FlowHandler.AUTHORIZATION_CODE_TOKEN_KEY);

        assertNull(idTokenValue);
        assertNotNull(accessTokenValue);
        assertNotNull(tokenTypeValue);
        assertNotNull(expiresInValue);
        assertNotNull(authCodeValue);

        assertTrue(accessTokenValue instanceof Oauth2AccessToken);
        Oauth2AccessToken accessToken = (Oauth2AccessToken) accessTokenValue;

        String scope = accessToken.getScope();

        assertEquals(String.join(" ", List.of(scopes)), scope);
        assertEquals(combinedOauth2Token.getTokenValue(), accessToken.getTokenValue());
        AuthorizationCode authorizationCode = store.findByAuthorizationCodeValue((String) authCodeValue);
        assertNotNull(authorizationCode);
    }

    @Test
    @DisplayName("Handle multiple response type flow with code and token and id_token and expect success")
    void handleFlowWithCodeAndTokenAndIdTokenResponseTypesAndExpectSuccess() {
        String[] scopes = {"read", "write"};
        AuthorizationRequest request = AuthorizationRequest
                .builder()
                .clientId("client_1235")
                .scopes(scopes)
                .responseTypes(Oauth2ResponseType.CODE, Oauth2ResponseType.TOKEN, OidcResponseType.ID_TOKEN)
                .grantType(AuthorizationGrantType.MULTIPLE)
                .build();
        Oauth2User expectedUser = new Oauth2User(USER_ID, "Odeyalo", "password", Set.of("USER"), Collections.emptyMap());

        Oauth2Token oauth2Token = handler.handleFlow(request, expectedUser);
        assertNotNull(oauth2Token, "DefaultMultipleResponseTypeOidcOauth2FlowHandler can't return null value");
        assertTrue(oauth2Token instanceof CombinedOauth2Token, "DefaultMultipleResponseTypeOidcOauth2FlowHandler must return CombinedOauth2Token");

        CombinedOauth2Token combinedOauth2Token = (CombinedOauth2Token) oauth2Token;
        String tokenValue = combinedOauth2Token.getTokenValue();
        Instant expiresIn = combinedOauth2Token.getExpiresIn();
        Instant issuedAt = combinedOauth2Token.getIssuedAt();

        assertNotNull(tokenValue);
        assertNotNull(expiresIn);
        assertNotNull(issuedAt);

        Map<String, Object> additionalInfo = combinedOauth2Token.getAdditionalInfo();
        Object accessTokenValue = additionalInfo.get(MultipleResponseTypeOidcOauth2FlowHandler.ACCESS_TOKEN_KEY);
        Object tokenTypeValue = additionalInfo.get(Oauth2Constants.TOKEN_TYPE);
        Object expiresInValue = additionalInfo.get(Oauth2Constants.EXPIRES_IN);
        Object idTokenValue = additionalInfo.get(MultipleResponseTypeOidcOauth2FlowHandler.ID_TOKEN_KEY);
        Object authCodeValue = additionalInfo.get(MultipleResponseTypeOidcOauth2FlowHandler.AUTHORIZATION_CODE_TOKEN_KEY);

        assertNotNull(idTokenValue);
        assertNotNull(accessTokenValue);
        assertNotNull(tokenTypeValue);
        assertNotNull(expiresInValue);
        assertNotNull(authCodeValue);

        assertTrue(accessTokenValue instanceof Oauth2AccessToken);
        Oauth2AccessToken accessToken = (Oauth2AccessToken) accessTokenValue;

        String scope = accessToken.getScope();

        assertEquals(String.join(" ", List.of(scopes)), scope);
        assertEquals(combinedOauth2Token.getTokenValue(), accessToken.getTokenValue());
        AuthorizationCode authorizationCode = store.findByAuthorizationCodeValue((String) authCodeValue);
        assertNotNull(authorizationCode);
    }
}

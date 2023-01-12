package com.odeyalo.kyrie.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odeyalo.kyrie.AbstractIntegrationTest;
import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.oauth2.client.ClientCredentialsValidator;
import com.odeyalo.kyrie.core.oauth2.client.InMemoryOauth2ClientRepository;
import com.odeyalo.kyrie.core.oauth2.client.Oauth2Client;
import com.odeyalo.kyrie.core.oauth2.client.Oauth2ClientRepository;
import com.odeyalo.kyrie.core.oauth2.tokens.code.AuthorizationCode;
import com.odeyalo.kyrie.core.oauth2.tokens.code.AuthorizationCodeStore;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.Oauth2AccessTokenGenerator;
import com.odeyalo.kyrie.core.support.ValidationResult;
import com.odeyalo.kyrie.dto.AccessTokenIntrospectionResponse;
import com.odeyalo.kyrie.dto.ApiErrorMessage;
import com.odeyalo.kyrie.dto.GetAccessTokenRequestDTO;
import com.odeyalo.kyrie.dto.KyrieSuccessfulObtainTokenResponse;
import com.odeyalo.kyrie.exceptions.Oauth2ErrorType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Integration test for TokenController.
 * @see TokenController
 */
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@Import({TokenControllerTest.TokenControllerTestConfiguration.class})
@PropertySource(value = "classpath:application-test.properties")
class TokenControllerTest extends AbstractIntegrationTest {
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String TOKEN_ENDPOINT = "/token";
    public static final String REDIRECT_URL = "http://localhost:9000/callback";
    public static final String GRANT_TYPE_PARAMETER_NAME = "grant_type";
    public static final String CODE_PARAMETER_NAME = "code";
    public static final String REDIRECT_URI_PARAMETER_NAME = "redirect_uri";
    public static final String CLIENT_ID_PARAMETER_NAME = "client_id";
    public static final String CLIENT_SECRET_PARAMETER_NAME = "client_secret";
    public static final String NO_EXISTING_CLIENT_SECRET = "no_existing_client_secret";
    public static final String NO_EXISTING_CLIENT_ID = "no_existing_client_id";
    public static final String USER_ID = "1";
    public static final String INVALID_JWT_ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NzExMjA1OTgsInN1YiI6IjFhMDk3MTUxLWNmMDAtNGUxNC05MGYyLWI5ODAxZTU1NTk3MCIsImlhdCI6MTY3MTExNjk5OCwic2NvcGUiOiJyZWFkIHdyaXRlIn0.qX1GXEc8k0EAKsuZQaY2PvMsTORHSYvBq_suKNnfqyQ";
    public static final String TOKEN_PARAMETER_NAME = "token";


    @Value("${kyrie.tokens.jwt.secret.key}")
    private String secretWord;

    @Autowired
    private ObjectMapper objectMapper;

    public static final String MOCKED_EXISTING_AUTHORIZATION_CODE_VALUE = "IHATEMYSELFANDEVERYONE";
    public static final String NOT_EXISTING_AUTHORIZATION_CODE_VALUE = "notexisting";
    private static final Oauth2User MOCKED_USER = Oauth2User
            .builder()
            .username("username")
            .password("password")
            .id("1")
            .authorities(Set.of("USER"))
            .additionalInfo(Collections.emptyMap())
            .build();
    private static final AuthorizationCode MOCK_AUTHORIZATION_CODE = AuthorizationCode.builder()
            .codeValue(MOCKED_EXISTING_AUTHORIZATION_CODE_VALUE)
            .user(MOCKED_USER)
            .scopes(new String[]{"read", "write"})
            .expiresIn(LocalDateTime.now().plusMinutes(5))
            .build();

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeAll
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .alwaysDo(MockMvcResultHandlers.print()).build();
    }

    @TestConfiguration
    public static class TokenControllerTestConfiguration {
        @Bean
        @Primary
        public AuthorizationCodeStore authorizationCodeStore() {
            AuthorizationCodeStore store = Mockito.mock(AuthorizationCodeStore.class);
            Mockito.when(store.findByAuthorizationCodeValue(MOCKED_EXISTING_AUTHORIZATION_CODE_VALUE)).thenReturn(MOCK_AUTHORIZATION_CODE).getMock();
            return store;
        }

        @Bean
        @Primary
        public ClientCredentialsValidator clientCredentialsValidator() {
            return (id, secret) -> {
                if (CLIENT_ID.equals(id) && CLIENT_SECRET.equals(secret)) {
                    return ValidationResult.success();
                }
                return ValidationResult.failed("Client credentials are wrong");
            };
        }
        @Bean
        @Primary
        public Oauth2ClientRepository oauth2ClientRepository() {
            Oauth2Client client = Oauth2Client.builder()
                    .clientId(CLIENT_ID)
                    .clientSecret(CLIENT_SECRET)
                    .allowedRedirectUri("http://localhost:9000")
                    .clientType(Oauth2Client.ClientType.CONFIDENTIAL)
                    .build();
            return new InMemoryOauth2ClientRepository(client);
        }
    }

    /**
     * Test '/token' endpoint that supports only application/json content type with correct authorization code and correct client credentials
     * @throws Exception - if any exception was occurred
     */
    @Test
    @DisplayName("Obtain an access token by correct authorization code using'/token' endpoint with application/json content type with correct client credentials, test content type, status and response body and expect success")
    void resolveAccessTokenWithCorrectAuthorizationCodeUsingJsonWithCorrectClientCredentials_AndExpectSuccess() throws Exception {
        String json = objectMapper.writeValueAsString(new GetAccessTokenRequestDTO(AuthorizationGrantType.AUTHORIZATION_CODE.getGrantName(), MOCKED_EXISTING_AUTHORIZATION_CODE_VALUE,
                REDIRECT_URL, CLIENT_ID, CLIENT_SECRET));
        mockMvc.perform(post(TOKEN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpectAll(
                MockMvcResultMatchers.status().is2xxSuccessful(),
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                (result) -> {
                    String content = result.getResponse().getContentAsString();
                    KyrieSuccessfulObtainTokenResponse response = objectMapper.readValue(content, KyrieSuccessfulObtainTokenResponse.class);
                    assertNotNull(response.getExpiresIn());
                    assertNotNull(response.getToken());
                    assertNotNull(response.getScopes());
                    assertNotNull(response.getPrefix());
                }
        );
    }

    /**
     * Test '/token' endpoint that supports only application/json content type with wrong authorization code and correct client credentials and expect error
     * @throws Exception - if any exception was occurred
     */
    @Test
    @DisplayName("Obtain an access token by wrong authorization code using'/token' endpoint with application/json content type with correct client credentials, test content type, status and response body and expect error")
    void resolveAccessTokenWithWrongAuthorizationCodeUsingJsonWithCorrectClientCredentials_AndExpectBadRequest() throws Exception {
        String json = objectMapper.writeValueAsString(
                new GetAccessTokenRequestDTO(AuthorizationGrantType.AUTHORIZATION_CODE.getGrantName(), NOT_EXISTING_AUTHORIZATION_CODE_VALUE,
                REDIRECT_URL, CLIENT_ID, CLIENT_SECRET));
        mockMvc.perform(post(TOKEN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpectAll(
                MockMvcResultMatchers.status().is4xxClientError(),
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                (result) -> {
                    String content = result.getResponse().getContentAsString();
                    ApiErrorMessage response = objectMapper.readValue(content, ApiErrorMessage.class);
                    assertEquals(Oauth2ErrorType.INVALID_GRANT.getErrorName(), response.getError());
                    assertNotNull(response.getErrorDescription());
                }
        );
    }


    /**
     * Test '/token' endpoint that supports only multipart/form-data content type with correct authorization code and correct client credentials
     * @throws Exception - if any exception was occurred
     */
    @Test
    @DisplayName("Obtain an access token by correct authorization code using'/token' endpoint with multipart/form-data content type with correct client credentials, test content type, status and response body and expect success")
    void resolveAccessTokenWithCorrectAuthorizationCodeUsingFormDataWithCorrectClientCredentials_AndExpectSuccess() throws Exception {
        mockMvc.perform(post(TOKEN_ENDPOINT)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param(GRANT_TYPE_PARAMETER_NAME, AuthorizationGrantType.AUTHORIZATION_CODE.getGrantName())
                .param(CODE_PARAMETER_NAME, MOCKED_EXISTING_AUTHORIZATION_CODE_VALUE)
                .param(REDIRECT_URI_PARAMETER_NAME, REDIRECT_URL)
                .param(CLIENT_ID_PARAMETER_NAME, CLIENT_ID)
                .param(CLIENT_SECRET_PARAMETER_NAME, CLIENT_SECRET)
        ).andExpectAll(
                MockMvcResultMatchers.status().is2xxSuccessful(),
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                (result) -> {
                    String content = result.getResponse().getContentAsString();
                    KyrieSuccessfulObtainTokenResponse response = objectMapper.readValue(content, KyrieSuccessfulObtainTokenResponse.class);
                    assertNotNull(response.getExpiresIn());
                    assertNotNull(response.getToken());
                    assertNotNull(response.getScopes());
                    assertNotNull(response.getPrefix());
                }
        );
    }



    /**
     * Test '/token' endpoint that supports only multipart/form-data content type with wrong authorization code and correct client credentials
     * @throws Exception - if any exception was occurred
     */
    @Test
    @DisplayName("Obtain an access token by wrong authorization code using'/token' endpoint with multipart/form-data content type with correct client credentials, test content type, status and response body and expect success")
    void resolveAccessTokenWithWrongAuthorizationCodeUsingFormDataWithCorrectClientCredentials_AndExpectError() throws Exception {
        mockMvc.perform(post(TOKEN_ENDPOINT)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param(GRANT_TYPE_PARAMETER_NAME, AuthorizationGrantType.AUTHORIZATION_CODE.getGrantName())
                .param(CODE_PARAMETER_NAME, NOT_EXISTING_AUTHORIZATION_CODE_VALUE)
                .param(REDIRECT_URI_PARAMETER_NAME, REDIRECT_URL)
                .param(CLIENT_ID_PARAMETER_NAME, CLIENT_ID)
                .param(CLIENT_SECRET_PARAMETER_NAME, CLIENT_SECRET)
        ).andExpectAll(
                MockMvcResultMatchers.status().is4xxClientError(),
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                (result) -> {
                    String content = result.getResponse().getContentAsString();
                    ApiErrorMessage response = objectMapper.readValue(content, ApiErrorMessage.class);
                    assertEquals(Oauth2ErrorType.INVALID_GRANT.getErrorName(), response.getError());
                    assertNotNull(response.getErrorDescription());
                }
        );
    }


    /**
     * Test '/token' endpoint that supports only application/json content type with correct authorization code and correct client credentials
     * @throws Exception - if any exception was occurred
     */
    @Test
    @DisplayName("Obtain an access token by correct authorization code using'/token' endpoint with application/json content type with invalid client credentials, test content type, status and response body and expect success")
    void resolveAccessTokenWithCorrectAuthorizationCodeUsingJsonWithInvalidClientCredentials_AndExpectBadRequest() throws Exception {
        String json = objectMapper.writeValueAsString(
                new GetAccessTokenRequestDTO(AuthorizationGrantType.AUTHORIZATION_CODE.getGrantName(),
                MOCKED_EXISTING_AUTHORIZATION_CODE_VALUE,
                REDIRECT_URL, NO_EXISTING_CLIENT_ID, NO_EXISTING_CLIENT_SECRET));
        mockMvc.perform(post(TOKEN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpectAll(
                MockMvcResultMatchers.status().is4xxClientError(),
                MockMvcResultMatchers.status().isUnauthorized(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                (result) -> {
                    String content = result.getResponse().getContentAsString();
                    ApiErrorMessage response = objectMapper.readValue(content, ApiErrorMessage.class);
                    String error = response.getError();
                    assertEquals(Oauth2ErrorType.INVALID_CLIENT.getErrorName(), error);
                    assertNotNull(response.getErrorDescription());
                }
        );
    }

    /**
     * Test '/token' endpoint with multipart/form-data content type with correct authorization code and correct client credentials
     * @throws Exception - if any exception was occurred
     */
    @Test
    @DisplayName("Obtain an access token by correct authorization code using'/token' endpoint with multipart/form-data content type with invalid client credentials, test content type, status and response body and expect success")
    void resolveAccessTokenWithCorrectAuthorizationCodeUsingFormDataWithInvalidClientCredentials_AndExpectBadRequest() throws Exception {
        mockMvc.perform(post(TOKEN_ENDPOINT)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param(GRANT_TYPE_PARAMETER_NAME, AuthorizationGrantType.AUTHORIZATION_CODE.getGrantName())
                .param(CODE_PARAMETER_NAME, MOCKED_EXISTING_AUTHORIZATION_CODE_VALUE)
                .param(REDIRECT_URI_PARAMETER_NAME, REDIRECT_URL)
                .param(CLIENT_ID_PARAMETER_NAME, NO_EXISTING_CLIENT_ID)
                .param(CLIENT_SECRET_PARAMETER_NAME, NO_EXISTING_CLIENT_SECRET)
        ).andExpectAll(
                MockMvcResultMatchers.status().is4xxClientError(),
                MockMvcResultMatchers.status().isUnauthorized(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                (result) -> {
                    String content = result.getResponse().getContentAsString();
                    ApiErrorMessage response = objectMapper.readValue(content, ApiErrorMessage.class);
                    String error = response.getError();
                    assertEquals(Oauth2ErrorType.INVALID_CLIENT.getErrorName(), error);
                    assertNotNull(response.getErrorDescription());
                }
        );
    }

    /**
     * Test '/tokeninfo' endpoint with valid jwt access token and expect success
     * @throws Exception - if any exceptions was occurred
     */
    @Test
    @DisplayName("Get token info by valid access token and expect success result")
    void getTokenInfoByValidJwtAccessToken_andExpectSuccess() throws Exception {
        String scopes = "read write openid";
        String validToken = generateJwtAccessToken(scopes, 300);
        mockMvc.perform(post("/tokeninfo")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param(TOKEN_PARAMETER_NAME, validToken)
        ).andExpectAll(
                MockMvcResultMatchers.status().is2xxSuccessful(),
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                (result) ->  {
                    String content = result.getResponse().getContentAsString();
                    AccessTokenIntrospectionResponse response = objectMapper.readValue(content, AccessTokenIntrospectionResponse.class);
                    assertTrue(response.isActive());
                    assertEquals(scopes, response.getScope());
                    assertTrue(response.getExpiresIn() > System.currentTimeMillis() / 1000L);
                }
        );
    }

    /**
     * Test '/tokeninfo' endpoint with invalid jwt access token and expect Http status 200 OK with json as body with
     * <p>
     *     {
     *         "active": "false"
     *     }
     * </p>
     * @throws Exception - if any exceptions was occurred
     */
    @Test
    @DisplayName("Get token info by invalid jwt access token and expect error")
    void getTokenInfoByInvalidJwtAccessToken_andExpectError() throws Exception {
        mockMvc.perform(post("/tokeninfo")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param(TOKEN_PARAMETER_NAME, INVALID_JWT_ACCESS_TOKEN)
        ).andExpectAll(
                MockMvcResultMatchers.status().is2xxSuccessful(),
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                (result) ->  {
                    String content = result.getResponse().getContentAsString();
                    AccessTokenIntrospectionResponse response = objectMapper.readValue(content, AccessTokenIntrospectionResponse.class);
                    assertFalse(response.isActive());
                    assertNull(response.getScope());
                    assertNull(response.getExpiresIn());
                }
        );
    }

    private String generateJwtAccessToken(String scopes, long expirationSeconds) {
        Map<String, Object> copiedClaims = new HashMap<>();
        copiedClaims.put(Oauth2AccessTokenGenerator.SCOPE, scopes);
        copiedClaims.put(Claims.SUBJECT, USER_ID);
        long issuedAt = getIssuedAt();
        copiedClaims.put(Claims.ISSUED_AT, issuedAt);
        Date exp = new Date(System.currentTimeMillis() + expirationSeconds * 1000L);
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, secretWord)
                .setExpiration(exp)
                .addClaims(copiedClaims)
                .compact();
    }

    private long getIssuedAt() {
        return System.currentTimeMillis() / 1000L;
    }
}

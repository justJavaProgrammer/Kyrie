package com.odeyalo.kyrie.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odeyalo.kyrie.AbstractIntegrationTest;
import com.odeyalo.kyrie.controllers.support.AuthorizationRequestValidator;
import com.odeyalo.kyrie.controllers.support.DefaultChainAuthorizationRequestValidator;
import com.odeyalo.kyrie.controllers.support.validation.AuthorizationRequestValidationStep;
import com.odeyalo.kyrie.controllers.support.validation.ClientIdAuthorizationRequestValidationStep;
import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authentication.AuthenticationResult;
import com.odeyalo.kyrie.core.authentication.Oauth2UserAuthenticationInfo;
import com.odeyalo.kyrie.core.authentication.Oauth2UserAuthenticationService;
import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.oauth2.oidc.OidcResponseType;
import com.odeyalo.kyrie.core.oauth2.support.Oauth2Constants;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.Oauth2AccessTokenGenerator;
import com.odeyalo.kyrie.core.support.Oauth2ValidationResult;
import com.odeyalo.kyrie.dto.ApiErrorMessage;
import com.odeyalo.kyrie.dto.LoginDTO;
import com.odeyalo.kyrie.exceptions.Oauth2ErrorType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import static com.odeyalo.kyrie.controllers.KyrieOauth2Controller.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Integration Test for KyrieOauth2Controller class.
 * <p>
 * Supported test cases:
 * </p>
 * <ul>
 *  <li>Hybrid Flow for 'code' and 'id_token' response types</li>
 *  <li>Hybrid Flow for 'code', 'id_token' and 'token' response types</li>
 *  <li>Implicit Flow for 'id_token' and 'token' response types</li>
 *  <li>Hybrid Flow for 'code' and 'token' response types</li>
 *  <li>Implicit flow</li>
 *  <li>Authorization code flow</li>
 *  <li>'/authorize' endpoint with correct client id</li>
 *  <li>'/authorize' endpoint with wrong client id</li>
 *  <li>'/authorize' endpoint with multiple response types and openid scope</li>
 *  <li>'/authorize' endpoint with multiple response types and WITHOUT openid scope</li>
 * </ul>
 * @see KyrieOauth2Controller
 * @version 1.0
 */
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@Import(KyrieOauth2ControllerTest.KyrieOauth2ControllerTestConfiguration.class)
@PropertySource(value = "classpath:application-test.properties")
class KyrieOauth2ControllerTest extends AbstractIntegrationTest {
    public static final String CLIENT_ID_PARAM_VALUE = "client_id";
    public static final String RESPONSE_TYPE_PARAM_VALUE = "response_type";
    public static final String SCOPE_PARAM_VALUE = "scope";
    public static final String REDIRECT_URI_PARAM_VALUE = "redirect_uri";
    public static final String STATE_PARAM_VALUE = "state";
    public static final String MOCK_CLIENT_ID_VALUE = "client_id123";
    public static final String MOCK_STATE_VALUE = "state123";

    private static final String ADMIN_USER_ID_VALUE = "123";
    private static final String ADMIN_USERNAME_VALUE = "admin";
    private static final String ADMIN_PASSWORD_VALUE = "123";
    public static final String USERNAME_PARAMETER_KEY = "username";
    public static final String PASSWORD_PARAMETER_KEY = "password";
    public static final String OAUTH_2_LOGIN_ENDPOINT_VALUE = "/oauth2/login";
    public static final String OAUTH_2_AUTHORIZE_ENDPOINT = "/oauth2/authorize";
    public static final String NOT_EXISTED_CLIENT_ID_VALUE = "not_existed_client_id";
    public static final String ERROR_PARAMETER_NAME = "error";
    public static final String ERROR_DESCRIPTION_PARAMETER_NAME = "error_description";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final Logger logger = LoggerFactory.getLogger(KyrieOauth2ControllerTest.class);

    @BeforeAll
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).alwaysDo(MockMvcResultHandlers.print()).build();
    }


    /**
     * Test configuration to mock some beans
     */
    @TestConfiguration
    public static class KyrieOauth2ControllerTestConfiguration {
        @Autowired
        private BeanFactory beanFactory;

        @Bean
        @Primary
        public Oauth2UserAuthenticationService oauth2UserAuthenticationService() {
            return (info) -> {
                if (info.equals(new Oauth2UserAuthenticationInfo(ADMIN_USERNAME_VALUE, ADMIN_PASSWORD_VALUE))) {
                    Oauth2User user = new Oauth2User(ADMIN_USER_ID_VALUE, ADMIN_USERNAME_VALUE, ADMIN_PASSWORD_VALUE, Collections.singleton("ADMIN"), Collections.emptyMap());
                    return AuthenticationResult.success(user);
                }
                return AuthenticationResult.failed();
            };
        }

        /**
         * Return DefaultChainAuthorizationRequestValidator with mocked ClientIdAuthorizationRequestValidationStep
         *
         * @param steps - steps that will be used as validation chain
         * @return - DefaultChainAuthorizationRequestValidator with mocked ClientIdAuthorizationRequestValidationStep
         * @see DefaultChainAuthorizationRequestValidator
         * @see ClientIdAuthorizationRequestValidationStep
         */
        @Bean
        @Primary
        public AuthorizationRequestValidator authorizationRequestValidator(List<AuthorizationRequestValidationStep> steps) {
            // Remove original ClientIdAuthorizationRequestValidationStep to avoid unnecessary calls to original validator
            steps.removeIf(step -> step instanceof ClientIdAuthorizationRequestValidationStep);
            steps.add((request) -> MOCK_CLIENT_ID_VALUE.equals(request.getClientId()) ? Oauth2ValidationResult.success() : Oauth2ValidationResult.failed(Oauth2ErrorType.INVALID_CLIENT, "Client id is not found"));
            return new DefaultChainAuthorizationRequestValidator(steps);
        }
    }

    /**
     * Test Hybrid flow with 'code' and 'id_token' response types check status, request params check
     *
     * @throws Exception - if any exception was occurred
     */
    @Test
    @DisplayName("Test Hybrid flow with code and id_token response types using application/json and expect success")
    void hybridFlowWithCodeAndIdTokenResponseTypesUsingJsonAndExpectSuccess() throws Exception {
        String redirectUri = "http://localhost:9000/callback";
        String[] scopes = {"read", "write"};

        String json = objectMapper.writeValueAsString(new LoginDTO(ADMIN_USERNAME_VALUE, ADMIN_PASSWORD_VALUE));

        AuthorizationRequest request = AuthorizationRequest.builder()
                .clientId(MOCK_CLIENT_ID_VALUE)
                .grantType(AuthorizationGrantType.MULTIPLE)
                .responseTypes(Oauth2ResponseType.CODE, OidcResponseType.ID_TOKEN)
                .redirectUrl(redirectUri)
                .state(MOCK_STATE_VALUE)
                .scopes(scopes)
                .build();
        HashMap<String, Object> innerMap = new HashMap<>();
        innerMap.put(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, request);

        mockMvc.perform(
                post(OAUTH_2_LOGIN_ENDPOINT_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .sessionAttrs(new HashMap<>(Collections.singletonMap(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, innerMap)))
        ).andExpectAll(
                MockMvcResultMatchers.status().is3xxRedirection(),
                MockMvcResultMatchers.status().isFound(),
                MockMvcResultMatchers.redirectedUrlPattern(redirectUri + "**"),
                Oauth2RedirectUrlResultMatchers.oauth2().isCodeParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isIdTokenParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isStateParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isParameterEqualTo(Oauth2Constants.STATE, MOCK_STATE_VALUE),
                (result) -> {
                    HttpSession session = result.getRequest().getSession();
                    assertNotNull(session, "Session cannot be null!");
                    Enumeration<String> attributeNames = session.getAttributeNames();
                    assertNotNull(attributeNames, "Session attributes cannot be null!");
                    assertFalse(attributeNames.hasMoreElements());
                }
        );
    }

    /**
     * Test Hybrid flow with 'code' and 'id_token' response types using multipart/form-data content type check status, request params check
     *
     * @throws Exception - if any exception was occurred
     */
    @Test
    @DisplayName("Test Hybrid flow with code and id_token response types using multipart/form-data and expect success")
    void hybridFlowWithCodeAndIdTokenResponseTypesUsingFormDataAndExpectSuccess() throws Exception {
        String redirectUri = "http://localhost:9000/callback";
        String[] scopes = {"read", "write"};

        AuthorizationRequest request = AuthorizationRequest.builder()
                .clientId(MOCK_CLIENT_ID_VALUE)
                .grantType(AuthorizationGrantType.MULTIPLE)
                .responseTypes(Oauth2ResponseType.CODE, OidcResponseType.ID_TOKEN)
                .redirectUrl(redirectUri)
                .state(MOCK_STATE_VALUE)
                .scopes(scopes)
                .build();
        HashMap<String, Object> innerMap = new HashMap<>();
        innerMap.put(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, request);

        mockMvc.perform(
                post(OAUTH_2_LOGIN_ENDPOINT_VALUE)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param(USERNAME_PARAMETER_KEY, ADMIN_USERNAME_VALUE)
                        .param(PASSWORD_PARAMETER_KEY, ADMIN_PASSWORD_VALUE)
                        .sessionAttrs(new HashMap<>(Collections.singletonMap(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, innerMap)))
        ).andExpectAll(
                MockMvcResultMatchers.status().is3xxRedirection(),
                MockMvcResultMatchers.status().isFound(),
                MockMvcResultMatchers.redirectedUrlPattern(redirectUri + "**"),
                Oauth2RedirectUrlResultMatchers.oauth2().isCodeParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isIdTokenParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isStateParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isParameterEqualTo(Oauth2Constants.STATE, MOCK_STATE_VALUE),
                (result) -> {
                    HttpSession session = result.getRequest().getSession();
                    assertNotNull(session, "Session cannot be null!");
                    Enumeration<String> attributeNames = session.getAttributeNames();
                    assertNotNull(attributeNames, "Session attributes cannot be null!");
                    assertFalse(attributeNames.hasMoreElements());
                }
        );
    }

    /**
     * Test Hybrid flow with token and id_token response types check status, validate token and request params check
     *
     * @throws Exception - if any exception was occurred
     */
    @Test
    @DisplayName("Test Hybrid flow with token and id_token using application/json content type response types and expect success")
    void hybridFlowWithTokenAndIdTokenResponseTypesUsingJsonAndExpectSuccess() throws Exception {
        String redirectUri = "http://localhost:9000/callback";
        String[] scopes = {"read", "write"};

        String json = objectMapper.writeValueAsString(new LoginDTO(ADMIN_USERNAME_VALUE, ADMIN_PASSWORD_VALUE));

        AuthorizationRequest request = AuthorizationRequest.builder()
                .clientId(MOCK_CLIENT_ID_VALUE)
                .grantType(AuthorizationGrantType.MULTIPLE)
                .responseTypes(Oauth2ResponseType.CODE, OidcResponseType.ID_TOKEN)
                .redirectUrl(redirectUri)
                .state(MOCK_STATE_VALUE)
                .scopes(scopes)
                .build();
        HashMap<String, Object> innerMap = new HashMap<>();
        innerMap.put(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, request);

        mockMvc.perform(
                post(OAUTH_2_LOGIN_ENDPOINT_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .sessionAttrs(new HashMap<>(Collections.singletonMap(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, innerMap)))
        ).andExpectAll(
                MockMvcResultMatchers.status().is3xxRedirection(),
                MockMvcResultMatchers.status().isFound(),
                MockMvcResultMatchers.redirectedUrlPattern(redirectUri + "**"),
                Oauth2RedirectUrlResultMatchers.oauth2().isCodeParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isIdTokenParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isStateParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isParameterEqualTo(Oauth2Constants.STATE, MOCK_STATE_VALUE),
                (result) -> {
                    HttpSession session = result.getRequest().getSession();
                    assertNotNull(session, "Session cannot be null!");
                    Enumeration<String> attributeNames = session.getAttributeNames();
                    assertNotNull(attributeNames, "Session attributes cannot be null!");
                    assertFalse(attributeNames.hasMoreElements());
                }
        );
    }

    /**
     * Test Hybrid flow with token and id_token response types using multipart/form-data content type check status, validate token and request params check
     *
     * @throws Exception - if any exception was occurred
     */
    @Test
    @DisplayName("Test Hybrid flow with token and id_token using multipart/form-data response types and expect success")
    void hybridFlowWithTokenAndIdTokenResponseTypesUsingFormDataAndExpectSuccess() throws Exception {
        String redirectUri = "http://localhost:9000/callback";
        String[] scopes = {"read", "write"};

        AuthorizationRequest request = AuthorizationRequest.builder()
                .clientId(MOCK_CLIENT_ID_VALUE)
                .grantType(AuthorizationGrantType.MULTIPLE)
                .responseTypes(Oauth2ResponseType.CODE, OidcResponseType.ID_TOKEN)
                .redirectUrl(redirectUri)
                .state(MOCK_STATE_VALUE)
                .scopes(scopes)
                .build();
        HashMap<String, Object> innerMap = new HashMap<>();
        innerMap.put(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, request);

        mockMvc.perform(
                post(OAUTH_2_LOGIN_ENDPOINT_VALUE)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param(USERNAME_PARAMETER_KEY, ADMIN_USERNAME_VALUE)
                        .param(PASSWORD_PARAMETER_KEY, ADMIN_PASSWORD_VALUE)
                        .sessionAttrs(new HashMap<>(Collections.singletonMap(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, innerMap)))
        ).andExpectAll(
                MockMvcResultMatchers.status().is3xxRedirection(),
                MockMvcResultMatchers.status().isFound(),
                MockMvcResultMatchers.redirectedUrlPattern(redirectUri + "**"),
                Oauth2RedirectUrlResultMatchers.oauth2().isCodeParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isIdTokenParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isStateParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isParameterEqualTo(Oauth2Constants.STATE, MOCK_STATE_VALUE),
                (result) -> {
                    HttpSession session = result.getRequest().getSession();
                    assertNotNull(session, "Session cannot be null!");
                    Enumeration<String> attributeNames = session.getAttributeNames();
                    assertNotNull(attributeNames, "Session attributes cannot be null!");
                    assertFalse(attributeNames.hasMoreElements());
                }
        );
    }

    /**
     * Test Hybrid flow with code and token response types and expect success, check params, validate token
     *
     * @throws Exception - if any exception was occurred
     */
    @Test
    @DisplayName("Test Hybrid flow with code and token using application/json response types and expect success")
    void hybridFlowWithCodeAndTokenResponseTypesUsingJsonAndExpectSuccess() throws Exception {
        String redirectUri = "http://localhost:9000/callback";
        String[] scopes = {"read", "write"};

        String json = objectMapper.writeValueAsString(new LoginDTO(ADMIN_USERNAME_VALUE, ADMIN_PASSWORD_VALUE));

        AuthorizationRequest request = AuthorizationRequest.builder()
                .clientId(MOCK_CLIENT_ID_VALUE)
                .grantType(AuthorizationGrantType.MULTIPLE)
                .responseTypes(Oauth2ResponseType.CODE, Oauth2ResponseType.TOKEN)
                .redirectUrl(redirectUri)
                .state(MOCK_STATE_VALUE)
                .scopes(scopes)
                .build();
        HashMap<String, Object> innerMap = new HashMap<>();
        innerMap.put(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, request);

        mockMvc.perform(
                post(OAUTH_2_LOGIN_ENDPOINT_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .sessionAttrs(new HashMap<>(Collections.singletonMap(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, innerMap)))

        ).andExpectAll(
                MockMvcResultMatchers.status().is3xxRedirection(),
                MockMvcResultMatchers.status().isFound(),
                MockMvcResultMatchers.redirectedUrlPattern(redirectUri + "**"),
                Oauth2RedirectUrlResultMatchers.oauth2().isStateParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isCodeParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isAccessTokenPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isTokenTypeParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isExpiresInParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isParameterEqualTo("state", MOCK_STATE_VALUE),
                testJwtToken(String.join(" ", scopes)),
                (result) -> {
                    HttpSession session = result.getRequest().getSession();
                    assertNotNull(session, "Session cannot be null!");
                    Enumeration<String> attributeNames = session.getAttributeNames();
                    assertNotNull(attributeNames, "Session attributes cannot be null!");
                    assertFalse(attributeNames.hasMoreElements());
                }
        );
    }


    /**
     * Test Hybrid flow with code and token response types and expect success, check params, validate token
     *
     * @throws Exception - if any exception was occurred
     */
    @Test
    @DisplayName("Test Hybrid flow with code and token using multipart/form-data response types and expect success")
    void hybridFlowWithCodeAndTokenResponseTypesUsingFormDataAndExpectSuccess() throws Exception {
        String redirectUri = "http://localhost:9000/callback";
        String[] scopes = {"read", "write"};

        AuthorizationRequest request = AuthorizationRequest.builder()
                .clientId(MOCK_CLIENT_ID_VALUE)
                .grantType(AuthorizationGrantType.MULTIPLE)
                .responseTypes(Oauth2ResponseType.CODE, Oauth2ResponseType.TOKEN)
                .redirectUrl(redirectUri)
                .state(MOCK_STATE_VALUE)
                .scopes(scopes)
                .build();
        HashMap<String, Object> innerMap = new HashMap<>();
        innerMap.put(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, request);

        mockMvc.perform(
                post(OAUTH_2_LOGIN_ENDPOINT_VALUE)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param(USERNAME_PARAMETER_KEY, ADMIN_USERNAME_VALUE)
                        .param(PASSWORD_PARAMETER_KEY, ADMIN_PASSWORD_VALUE)
                        .sessionAttrs(new HashMap<>(Collections.singletonMap(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, innerMap)))

        ).andExpectAll(
                MockMvcResultMatchers.status().is3xxRedirection(),
                MockMvcResultMatchers.status().isFound(),
                MockMvcResultMatchers.redirectedUrlPattern(redirectUri + "**"),
                Oauth2RedirectUrlResultMatchers.oauth2().isStateParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isCodeParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isAccessTokenPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isTokenTypeParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isExpiresInParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isParameterEqualTo("state", MOCK_STATE_VALUE),
                testJwtToken(String.join(" ", scopes)),
                (result) -> {
                    HttpSession session = result.getRequest().getSession();
                    assertNotNull(session, "Session cannot be null!");
                    Enumeration<String> attributeNames = session.getAttributeNames();
                    assertNotNull(attributeNames, "Session attributes cannot be null!");
                    assertFalse(attributeNames.hasMoreElements());
                }
        );
    }


    /**
     * Test Hybrid flow with code and token and id_token response types and expect success, check params, validate access token
     *
     * @throws Exception - if any exception was occurred
     */
    @Test
    @DisplayName("Test Hybrid flow with code and token and id_token response types and expect success")
    void hybridFlowWithCodeAndTokenAndIdTokenResponseTypesUsingJsonAndExpectSuccess() throws Exception {
        String redirectUri = "http://localhost:9000/callback";
        String[] scopes = {"read", "write"};

        String json = objectMapper.writeValueAsString(new LoginDTO(ADMIN_USERNAME_VALUE, ADMIN_PASSWORD_VALUE));

        AuthorizationRequest request = AuthorizationRequest.builder()
                .clientId(MOCK_CLIENT_ID_VALUE)
                .grantType(AuthorizationGrantType.MULTIPLE)
                .responseTypes(Oauth2ResponseType.CODE, Oauth2ResponseType.TOKEN, OidcResponseType.ID_TOKEN)
                .redirectUrl(redirectUri)
                .state(MOCK_STATE_VALUE)
                .scopes(scopes)
                .build();
        HashMap<String, Object> innerMap = new HashMap<>();
        innerMap.put(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, request);

        mockMvc.perform(
                post(OAUTH_2_LOGIN_ENDPOINT_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .sessionAttrs(new HashMap<>(Collections.singletonMap(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, innerMap)))
        ).andExpectAll(
                MockMvcResultMatchers.status().is3xxRedirection(),
                MockMvcResultMatchers.status().isFound(),
                MockMvcResultMatchers.redirectedUrlPattern(redirectUri + "**"),
                Oauth2RedirectUrlResultMatchers.oauth2().isCodeParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isAccessTokenPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isTokenTypeParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isExpiresInParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isIdTokenParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isStateParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isParameterEqualTo("state", MOCK_STATE_VALUE),
                testJwtToken(String.join(" ", scopes)),
                (result) -> {
                    HttpSession session = result.getRequest().getSession();
                    assertNotNull(session, "Session cannot be null!");
                    Enumeration<String> attributeNames = session.getAttributeNames();
                    assertNotNull(attributeNames, "Session attributes cannot be null!");
                    assertFalse(attributeNames.hasMoreElements());
                }
        );
    }

    /**
     * Test Hybrid flow with code and token and id_token response types using multipart/form-data and expect success, check params, validate access token
     *
     * @throws Exception - if any exception was occurred
     */
    @Test
    @DisplayName("Test Hybrid flow with code and token and id_token response using multipart/form-data types and expect success")
    void hybridFlowWithCodeAndTokenAndIdTokenResponseTypesUsingFormDataAndExpectSuccess() throws Exception {
        String redirectUri = "http://localhost:9000/callback";
        String[] scopes = {"read", "write"};

        AuthorizationRequest request = AuthorizationRequest.builder()
                .clientId(MOCK_CLIENT_ID_VALUE)
                .grantType(AuthorizationGrantType.MULTIPLE)
                .responseTypes(Oauth2ResponseType.CODE, Oauth2ResponseType.TOKEN, OidcResponseType.ID_TOKEN)
                .redirectUrl(redirectUri)
                .state(MOCK_STATE_VALUE)
                .scopes(scopes)
                .build();

        HashMap<String, Object> innerMap = new HashMap<>();
        innerMap.put(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, request);

        mockMvc.perform(
                post(OAUTH_2_LOGIN_ENDPOINT_VALUE)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param(USERNAME_PARAMETER_KEY, ADMIN_USERNAME_VALUE)
                        .param(PASSWORD_PARAMETER_KEY, ADMIN_PASSWORD_VALUE)
                        .sessionAttrs(new HashMap<>(Collections.singletonMap(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, innerMap)))
        ).andExpectAll(
                MockMvcResultMatchers.status().is3xxRedirection(),
                MockMvcResultMatchers.status().isFound(),
                MockMvcResultMatchers.redirectedUrlPattern(redirectUri + "**"),
                Oauth2RedirectUrlResultMatchers.oauth2().isCodeParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isAccessTokenPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isTokenTypeParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isExpiresInParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isIdTokenParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isStateParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isParameterEqualTo("state", MOCK_STATE_VALUE),
                testJwtToken(String.join(" ", scopes)),
                (result) -> {
                    HttpSession session = result.getRequest().getSession();
                    assertNotNull(session, "Session cannot be null!");
                    Enumeration<String> attributeNames = session.getAttributeNames();
                    assertNotNull(attributeNames, "Session attributes cannot be null!");
                    assertFalse(attributeNames.hasMoreElements());
                }
        );
    }

    /**
     * Test authorization code flow using application/json content type.
     * Method to test that redirect url contains state and code
     *
     * @throws Exception - if any exception was occurred
     */
    @Test
    @DisplayName("Login user and do authorization_code flow")
    void authorizationCodeFlowUsingJson() throws Exception {
        String redirectUri = "http://localhost:9000/";
        String json = objectMapper.writeValueAsString(new LoginDTO(ADMIN_USERNAME_VALUE, ADMIN_PASSWORD_VALUE));
        String[] scopes = {"read", "write"};
        // Build mocked authorization request and put it to session attributes
        AuthorizationRequest request = AuthorizationRequest.builder()
                .clientId(MOCK_CLIENT_ID_VALUE)
                .grantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .responseTypes(Oauth2ResponseType.CODE)
                .redirectUrl(redirectUri)
                .state(MOCK_STATE_VALUE)
                .scopes(scopes)
                .build();

        HashMap<String, Object> innerMap = new HashMap<>();
        innerMap.put(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, request);

        mockMvc.perform(post(OAUTH_2_LOGIN_ENDPOINT_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .sessionAttrs(new HashMap<>(Collections.singletonMap(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, innerMap)))
        ).andExpectAll(
                MockMvcResultMatchers.status().is3xxRedirection(),
                MockMvcResultMatchers.status().isFound(),
                MockMvcResultMatchers.redirectedUrlPattern(redirectUri + "/**"),
                Oauth2RedirectUrlResultMatchers.oauth2().isStateParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isCodeParamPresented()
        );
    }

    /**
     * Test authorization code flow using multipart/form-data content type.
     * Method to test that redirect url contains state and code
     *
     * @throws Exception - if any exception was occurred
     */
    @Test
    @DisplayName("Login user and do authorization_code flow using multipart/form-data content type")
    void authorizationCodeFlowUsingFormDataContentType() throws Exception {
        String redirectUri = "http://localhost:9000/";
        String[] scopes = {"read", "write"};
        // Build mocked authorization request and put it to session attributes
        AuthorizationRequest request = AuthorizationRequest.builder()
                .clientId(MOCK_CLIENT_ID_VALUE)
                .grantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .responseTypes(Oauth2ResponseType.CODE)
                .redirectUrl(redirectUri)
                .state(MOCK_STATE_VALUE)
                .scopes(scopes)
                .build();

        HashMap<String, Object> innerMap = new HashMap<>();
        innerMap.put(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, request);

        mockMvc.perform(post(OAUTH_2_LOGIN_ENDPOINT_VALUE)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param(USERNAME_PARAMETER_KEY, ADMIN_USERNAME_VALUE)
                .param(PASSWORD_PARAMETER_KEY, ADMIN_PASSWORD_VALUE)
                .sessionAttrs(new HashMap<>(Collections.singletonMap(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, innerMap)))
        ).andExpectAll(
                MockMvcResultMatchers.status().is3xxRedirection(),
                MockMvcResultMatchers.status().isFound(),
                MockMvcResultMatchers.redirectedUrlPattern(redirectUri + "/**"),
                Oauth2RedirectUrlResultMatchers.oauth2().isStateParamPresented(),
                Oauth2RedirectUrlResultMatchers.oauth2().isCodeParamPresented()
        );
    }

    @Test
    @DisplayName("'/authorize' endpoint with 'authorization code' flow and expect html page")
    void authorizationEndpointWithOneResponseTypeAndExpectSuccess() throws Exception {
        String responseTypes = "code";
        String scopes = "read write";
        String redirectUri = "http://localhost:9000";

        mockMvc.perform(
                get(OAUTH_2_AUTHORIZE_ENDPOINT)
                        .param(CLIENT_ID_PARAM_VALUE, MOCK_CLIENT_ID_VALUE)
                        .param(RESPONSE_TYPE_PARAM_VALUE, responseTypes)
                        .param(SCOPE_PARAM_VALUE, scopes)
                        .param(REDIRECT_URI_PARAM_VALUE, redirectUri)
                        .param(STATE_PARAM_VALUE, MOCK_STATE_VALUE)
        ).andExpectAll(
                MockMvcResultMatchers.status().is2xxSuccessful(),
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8")
        ).andReturn();
    }

    @Test
    @DisplayName("/authorize endpoint with wrong authorization request and except error")
    void authorizeEndpointWithWrongAuthRequestAndExpectError() throws Exception {
        String responseTypes = "code";
        String scopes = "read write";
        String malformedRedirectUri = "odeyaloisworst";

        mockMvc.perform(
                get(OAUTH_2_AUTHORIZE_ENDPOINT)
                        .param(CLIENT_ID_PARAM_VALUE, CLIENT_ID_PARAM_VALUE)
                        .param(RESPONSE_TYPE_PARAM_VALUE, responseTypes)
                        .param(SCOPE_PARAM_VALUE, scopes)
                        .param(REDIRECT_URI_PARAM_VALUE, malformedRedirectUri)
                        .param(STATE_PARAM_VALUE, MOCK_STATE_VALUE))
                .andExpectAll(MockMvcResultMatchers.status().is2xxSuccessful(),
                        MockMvcResultMatchers.status().isOk(),
                        (result) -> {
                            String content = result.getResponse().getContentAsString();
                            ApiErrorMessage message = objectMapper.readValue(content, ApiErrorMessage.class);
                            String error = message.getError();
                            String errorDescription = message.getErrorDescription();
                            assertNotNull(error, "Error name must be presented!");
                            assertNotNull(errorDescription, "Error description must be presented!");
                            assertEquals(Oauth2ErrorType.INVALID_REDIRECT_URI.getErrorName(), error, "When malformed url received must be returned INVALID_REDIRECT_URI");
                        });
    }

    @Test
    @DisplayName("'/authorize' endpoint with 'hybrid' flow and expect html page")
    void authorizationEndpointWithTwoResponseTypesWithOpenidScopeAndExpectSuccess() throws Exception {
        String responseTypes = "code token";
        String scopes = "read openid write";
        String redirectUri = "http://localhost:9000";

        mockMvc.perform(
                get(OAUTH_2_AUTHORIZE_ENDPOINT)
                        .param(CLIENT_ID_PARAM_VALUE, MOCK_CLIENT_ID_VALUE)
                        .param(RESPONSE_TYPE_PARAM_VALUE, responseTypes)
                        .param(SCOPE_PARAM_VALUE, scopes)
                        .param(REDIRECT_URI_PARAM_VALUE, redirectUri)
                        .param(STATE_PARAM_VALUE, MOCK_STATE_VALUE)
        ).andExpectAll(
                MockMvcResultMatchers.status().is2xxSuccessful(),
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8")
        ).andReturn();
    }

    @Test
    @DisplayName("'/authorize' endpoint with 'hybrid' flow without openid and expect redirect with error and error_description params")
    void authorizationEndpointWithTwoResponseTypesWithoutOpenidScopeAndExpectError() throws Exception {
        String responseTypes = "code token";
        String scopes = "read write";
        String redirectUri = "http://localhost:9000";

        mockMvc.perform(
                get(OAUTH_2_AUTHORIZE_ENDPOINT)
                        .param(CLIENT_ID_PARAM_VALUE, MOCK_CLIENT_ID_VALUE)
                        .param(RESPONSE_TYPE_PARAM_VALUE, responseTypes)
                        .param(SCOPE_PARAM_VALUE, scopes)
                        .param(REDIRECT_URI_PARAM_VALUE, redirectUri)
                        .param(STATE_PARAM_VALUE, MOCK_STATE_VALUE)
        ).andExpectAll(
                MockMvcResultMatchers.status().is3xxRedirection(),
                MockMvcResultMatchers.status().isFound(),
                Oauth2RedirectUrlResultMatchers.oauth2().isParameterPresented(ERROR_PARAMETER_NAME),
                Oauth2RedirectUrlResultMatchers.oauth2().isParameterPresented(ERROR_DESCRIPTION_PARAMETER_NAME),
                Oauth2RedirectUrlResultMatchers.oauth2().isParameterEqualTo(ERROR_PARAMETER_NAME, Oauth2ErrorType.INVALID_REQUEST.getErrorName())
        ).andReturn();
    }

    @Test
    @DisplayName("Test '/authorize' endpoint with wrong client id and expert invalid_client error type and not null description")
    void testAuthorizeEndpointWithWrongClientId_AndExpectError() throws Exception {
        String responseTypes = "code";
        String scopes = "read write";
        String redirectUri = "http://localhost:9000";

        mockMvc.perform(
                get(OAUTH_2_AUTHORIZE_ENDPOINT)
                        .param(CLIENT_ID_PARAM_VALUE, NOT_EXISTED_CLIENT_ID_VALUE)
                        .param(RESPONSE_TYPE_PARAM_VALUE, responseTypes)
                        .param(SCOPE_PARAM_VALUE, scopes)
                        .param(REDIRECT_URI_PARAM_VALUE, redirectUri)
                        .param(STATE_PARAM_VALUE, MOCK_STATE_VALUE))
                .andExpectAll(MockMvcResultMatchers.status().is3xxRedirection(),
                        MockMvcResultMatchers.status().isFound(),
                        MockMvcResultMatchers.redirectedUrlPattern(redirectUri + "**"),
                        Oauth2RedirectUrlResultMatchers.oauth2().isParameterPresented(ERROR_PARAMETER_NAME),
                        Oauth2RedirectUrlResultMatchers.oauth2().isParameterEqualTo(ERROR_PARAMETER_NAME, Oauth2ErrorType.INVALID_CLIENT.getErrorName()),
                        Oauth2RedirectUrlResultMatchers.oauth2().isParameterPresented(ERROR_DESCRIPTION_PARAMETER_NAME),
                        Oauth2RedirectUrlResultMatchers.oauth2().isParameterNotNull(ERROR_DESCRIPTION_PARAMETER_NAME));
    }

    /**
     * Testing Implicit flow with application/json content type
     *
     * @throws Exception - if any exception was occurred
     */
    @Test
    @DisplayName("Login user using application/json content type and do implicit flow and expect access_token, " +
            "token_type, expires_in params in redirect uri and expect success")
    void loginCheckAndImplicitGrantTypeProcessingWithJson() throws Exception {
        String json = objectMapper.writeValueAsString(new LoginDTO(ADMIN_USERNAME_VALUE, ADMIN_PASSWORD_VALUE));
        performMockAuthRequestForImplicitFlowAndDoChecks(
                post(OAUTH_2_LOGIN_ENDPOINT_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        );
    }

    /**
     * Testing Implicit flow with multipart/form-data content type
     *
     * @throws Exception - if any exception was occurred
     */
    @Test
    @DisplayName("Login user using form-data content type and do implicit flow and expect access_token, token_type, expires_in params in redirect uri and expect success")
    void loginCheckAndImplicitGrantTypeProcessingWithFormData() throws Exception {
        performMockAuthRequestForImplicitFlowAndDoChecks(
                post("/oauth2/login")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param(USERNAME_PARAMETER_KEY, ADMIN_USERNAME_VALUE)
                        .param(PASSWORD_PARAMETER_KEY, ADMIN_PASSWORD_VALUE)
        );
    }

    /**
     * Test /login endpoint that doesn't contain an authorization request in session store and except 400 Bad Request
     * @throws Exception - if any exception occurred
     */
    @Test
    @DisplayName("Test /login endpoint using application/json content type with empty authorization request and expect bad request")
    void loginCheckWithEmptyAuthorizationRequestUsingJsonAndExpect400() throws Exception{
        LoginDTO dto = new LoginDTO(ADMIN_USERNAME_VALUE, ADMIN_PASSWORD_VALUE);
        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(post(OAUTH_2_LOGIN_ENDPOINT_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpectAll(MockMvcResultMatchers.status().is4xxClientError(),
                        MockMvcResultMatchers.status().isBadRequest(),
                        (result) -> {
                            String content = result.getResponse().getContentAsString();
                            ApiErrorMessage actual = objectMapper.readValue(content, ApiErrorMessage.class);
                            assertNotNull(actual, "If request is wrong, then 400 BAD REQUEST must be returned with ApiErrorMessage as body");
                            assertNotNull(actual.getError(), "Error name cannot be null");
                            assertNotNull(actual.getErrorDescription(), "Error description cannot be null");
                            assertEquals(actual.getError(), MISSING_AUTHORIZATION_REQUEST_ERROR_NAME, "Message must be equal to 'missing_authorization_request'");
                        });

    }
    /**
     * Test /login endpoint that doesn't contain an authorization request in session store and except 400 Bad Request
     * @throws Exception - if any exception occurred
     */
    @Test
    @DisplayName("Test /login endpoint using multipart/form-data content type with empty authorization request and expect bad request")
    void loginCheckWithEmptyAuthorizationRequestUsingFormDataAndExpect400() throws Exception{
        mockMvc.perform(post(OAUTH_2_LOGIN_ENDPOINT_VALUE)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param(USERNAME_PARAMETER_KEY, ADMIN_USERNAME_VALUE)
                .param(PASSWORD_PARAMETER_KEY, ADMIN_PASSWORD_VALUE))
                .andExpectAll(MockMvcResultMatchers.status().is4xxClientError(),
                        MockMvcResultMatchers.status().isBadRequest(),
                        (result) -> {
                            String content = result.getResponse().getContentAsString();
                            ApiErrorMessage actual = objectMapper.readValue(content, ApiErrorMessage.class);
                            assertNotNull(actual, "If request is wrong, then 400 BAD REQUEST must be returned with ApiErrorMessage as body");
                            assertNotNull(actual.getError(), "Error name cannot be null");
                            assertNotNull(actual.getErrorDescription(), "Error description cannot be null");
                            assertEquals(actual.getError(), MISSING_AUTHORIZATION_REQUEST_ERROR_NAME, "Message must be equal to 'missing_authorization_request'");
                        });

    }

    @Test
    @DisplayName("Test /login endpoint using json with wrong user credentials and expect bad request")
    void loginCheckUsingJsonWithWrongUserCredentials_AndExpectUnauthorized() throws Exception {
        String[] scopes = {"read", "write"};
        String redirectUri = "http://localhost:6666/callback";

        LoginDTO dto = new LoginDTO("odeyalo", "password");
        String json = objectMapper.writeValueAsString(dto);

        AuthorizationRequest request = AuthorizationRequest.builder()
                .clientId(MOCK_CLIENT_ID_VALUE)
                .grantType(AuthorizationGrantType.MULTIPLE)
                .responseTypes(Oauth2ResponseType.CODE, Oauth2ResponseType.TOKEN, OidcResponseType.ID_TOKEN)
                .redirectUrl(redirectUri)
                .state(MOCK_STATE_VALUE)
                .scopes(scopes)
                .build();


        HashMap<String, Object> innerMap = new HashMap<>();
        innerMap.put(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, request);

        mockMvc.perform(post(OAUTH_2_LOGIN_ENDPOINT_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .sessionAttrs(new HashMap<>(Collections.singletonMap(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, innerMap)))
        ).andExpectAll(MockMvcResultMatchers.status().is4xxClientError(),
                MockMvcResultMatchers.status().isUnauthorized(),
                (result) -> {
                    String content = result.getResponse().getContentAsString();
                    ApiErrorMessage errorMessage = objectMapper.readValue(content, ApiErrorMessage.class);
                    assertNotNull(errorMessage, "Error message cannot be null when 400 response was received");
                    assertNotNull(errorMessage.getError(), "Error name cannot be null when 400 response was received");
                    assertNotNull(errorMessage.getErrorDescription(), "Error description must be presented!");
                    assertEquals(WRONG_CREDENTIALS_ERROR_NAME, errorMessage.getError());
                });
    }


    @Test
    @DisplayName("Test /login endpoint using multipart/form-data with wrong user credentials and expect bad request")
    void loginCheckUsingFormDataWithWrongUserCredentials_AndExpectUnauthorized() throws Exception {
        String[] scopes = {"read", "write"};
        String redirectUri = "http://localhost:6666/callback";

        AuthorizationRequest request = AuthorizationRequest.builder()
                .clientId(MOCK_CLIENT_ID_VALUE)
                .grantType(AuthorizationGrantType.MULTIPLE)
                .responseTypes(Oauth2ResponseType.CODE, Oauth2ResponseType.TOKEN, OidcResponseType.ID_TOKEN)
                .redirectUrl(redirectUri)
                .state(MOCK_STATE_VALUE)
                .scopes(scopes)
                .build();


        HashMap<String, Object> innerMap = new HashMap<>();
        innerMap.put(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, request);

        mockMvc.perform(post(OAUTH_2_LOGIN_ENDPOINT_VALUE)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param(USERNAME_PARAMETER_KEY, "wrong_username")
                .param(PASSWORD_PARAMETER_KEY, "wrong_password")
                .sessionAttrs(new HashMap<>(Collections.singletonMap(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, innerMap)))
        ).andExpectAll(MockMvcResultMatchers.status().is4xxClientError(),
                MockMvcResultMatchers.status().isUnauthorized(),
                (result) -> {
                    String content = result.getResponse().getContentAsString();
                    ApiErrorMessage errorMessage = objectMapper.readValue(content, ApiErrorMessage.class);
                    assertNotNull(errorMessage, "Error message cannot be null when 400 response was received");
                    assertNotNull(errorMessage.getError(), "Error name cannot be null when 400 response was received");
                    assertNotNull(errorMessage.getErrorDescription(), "Error description must be presented!");
                    assertEquals(WRONG_CREDENTIALS_ERROR_NAME, errorMessage.getError());
                });
    }


    @Test
    @DisplayName("Test /login endpoint using json with wrong authorization request")
    void testLoginUsingJsonWithWrongAuthorizationRequest_AndExceptError() throws Exception {
        String[] scopes = {"read", "write"};
        String redirectUri = "http://localhost:6666/callback";

        AuthorizationRequest request = AuthorizationRequest.builder()
                .clientId(MOCK_CLIENT_ID_VALUE)
                .grantType(null)
                .responseTypes(Oauth2ResponseType.CODE, Oauth2ResponseType.TOKEN, OidcResponseType.ID_TOKEN)
                .redirectUrl(redirectUri)
                .state(MOCK_STATE_VALUE)
                .scopes(scopes)
                .build();

        HashMap<String, Object> innerMap = new HashMap<>();
        innerMap.put(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, request);

        LoginDTO dto = new LoginDTO(ADMIN_USERNAME_VALUE, ADMIN_PASSWORD_VALUE);
        String json = objectMapper.writeValueAsString(dto);
        mockMvc.perform(post(OAUTH_2_LOGIN_ENDPOINT_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .sessionAttrs(new HashMap<>(Collections.singletonMap(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, innerMap))))
                .andExpectAll(MockMvcResultMatchers.status().is4xxClientError(),
                        MockMvcResultMatchers.status().isBadRequest(),
                        (result) -> {
                            String content = result.getResponse().getContentAsString();
                            ApiErrorMessage message = objectMapper.readValue(content, ApiErrorMessage.class);
                            String error = message.getError();
                            String errorDescription = message.getErrorDescription();
                            assertNotNull(error, "Error name must be presented");
                            assertNotNull(errorDescription, "Error description must be presented");
                            assertEquals(UNSUPPORTED_GRANT_TYPE_ERROR_NAME,error, "Error names must be equal");
                        });
    }
    /**
     * Extracted method to perform mock request to endpoint with implicit flow and do some default checks
     *
     * @param builder            - builder that contains url and other params
     * @param additionalMatchers - additional ResultMatchers to check request
     * @throws Exception - if any exception was occurred
     */
    private void performMockAuthRequestForImplicitFlowAndDoChecks(MockHttpServletRequestBuilder builder, ResultMatcher... additionalMatchers) throws Exception {
        String redirectUri = "http://localhost:9000";

        String[] scopes = {"read", "write"};
        String scopesOauth2Spec = String.join(" ", scopes);
        // Build mocked authorization request and put it to session attributes
        AuthorizationRequest request = AuthorizationRequest.builder()
                .clientId(MOCK_CLIENT_ID_VALUE)
                .grantType(AuthorizationGrantType.IMPLICIT)
                .responseTypes(Oauth2ResponseType.TOKEN)
                .redirectUrl(redirectUri)
                .state(MOCK_STATE_VALUE)
                .scopes(scopes).build();

        HashMap<String, Object> innerMap = new HashMap<>();
        innerMap.put(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, request);

        mockMvc.perform(
                builder.sessionAttrs(new HashMap<>(Collections.singletonMap(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, innerMap)))
        )
                .andExpectAll(
                        MockMvcResultMatchers.status().is3xxRedirection(),
                        Oauth2RedirectUrlResultMatchers.oauth2().isAccessTokenPresented(),
                        Oauth2RedirectUrlResultMatchers.oauth2().isTokenTypeParamPresented(),
                        Oauth2RedirectUrlResultMatchers.oauth2().isStateParamPresented(),
                        Oauth2RedirectUrlResultMatchers.oauth2().isExpiresInParamPresented(),
                        testJwtToken(scopesOauth2Spec),
                        result -> {
                            for (ResultMatcher additionalMatcher : additionalMatchers) {
                                logger.debug("Running additional matcher: {}", additionalMatcher);
                                additionalMatcher.match(result);
                            }
                        }
                );
    }

    /**
     * Helper method to test jwt token from response
     *
     * @param scopes - scopes that must be presented in token
     * @return - ResultMatcher with required assertions
     */
    private ResultMatcher testJwtToken(String scopes) {
        return result -> {
            String redirectedUrl = result.getResponse().getRedirectedUrl();
            MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromHttpUrl(redirectedUrl).build().getQueryParams();
            String token = queryParams.getFirst(Oauth2Constants.ACCESS_TOKEN);
            JwtTokenResultMatchers.jwt().isTokenValid(token).match(result);
            JwtTokenResultMatchers.jwt().isClaimPresented(token, Oauth2AccessTokenGenerator.SCOPE).match(result);
            JwtTokenResultMatchers.jwt().isClaimTypeCorrect(token, Oauth2AccessTokenGenerator.SCOPE, String.class).match(result);
            JwtTokenResultMatchers.jwt().isClaimEqualTo(token, Oauth2AccessTokenGenerator.SCOPE, scopes).match(result);
        };
    }
}

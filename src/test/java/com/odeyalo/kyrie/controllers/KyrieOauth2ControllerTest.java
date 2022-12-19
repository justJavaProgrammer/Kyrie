package com.odeyalo.kyrie.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odeyalo.kyrie.AbstractIntegrationTest;
import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authentication.AuthenticationResult;
import com.odeyalo.kyrie.core.authentication.Oauth2UserAuthenticationInfo;
import com.odeyalo.kyrie.core.authentication.Oauth2UserAuthenticationService;
import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.oauth2.support.Oauth2Constants;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.Oauth2AccessTokenGenerator;
import com.odeyalo.kyrie.dto.LoginDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import static com.odeyalo.kyrie.controllers.KyrieOauth2Controller.AUTHORIZATION_REQUEST_ATTRIBUTE_NAME;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Integration Test for KyrieOauth2Controller class.
 *
 * @see KyrieOauth2Controller
 */
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
class KyrieOauth2ControllerTest extends AbstractIntegrationTest {
    public static final String CLIENT_ID_PARAM_VALUE = "client_id";
    public static final String RESPONSE_TYPE_PARAM_VALUE = "response_type";
    public static final String SCOPE_PARAM_VALUE = "scope";
    public static final String REDIRECT_URI_PARAM_VALUE = "redirect_uri";
    public static final String STATE_PARAM_VALUE = "state";
    public static final String MOCK_CLIENT_ID_VALUE = "client_id123";
    public static final String MOCK_STATE_VALUE = "state123";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).alwaysDo(MockMvcResultHandlers.print()).build();
    }

    @TestConfiguration
    private static class KyrieOauth2ControllerTestConfiguration {
        @Bean
        @Primary
        public Oauth2UserAuthenticationService oauth2UserAuthenticationService() {
            return (info) -> {
                if (info.equals(new Oauth2UserAuthenticationInfo("admin", "123"))) {
                    Oauth2User user = new Oauth2User(UUID.randomUUID().toString(), "admin", "123", Collections.singleton("ADMIN"), Collections.emptyMap());
                    return AuthenticationResult.success(user);
                }
                return AuthenticationResult.failed();
            };
        }
    }
    /*
        code token
        code id_token
        token id_token
        id_token token code
     */


    @Test
    void authorizationEndpoint() throws Exception {
        String responseTypes = "code token";
        String scopes = "code token";
        String redirectUri = "http://localhost:9000";

        MvcResult mvcResult = mockMvc.perform(
                get("/oauth2/authorize")
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
    void loginCheckAndImplicitGrantTypeProcessingWithJson() throws Exception {
        String redirectUri = "http://localhost:9000";
        String content = objectMapper.writeValueAsString(new LoginDTO("admin", "123"));

        String[] scopes = {"read", "write"};
        String scopesOauth2Spec = String.join(" ", scopes);
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
                post("/oauth2/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .sessionAttrs(new HashMap<>(Collections.singletonMap(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME, innerMap))
                        ))
                .andExpectAll(
                        MockMvcResultMatchers.status().is3xxRedirection(),
                        Oauth2RedirectUrlResultMatchers.oauth2().isAccessTokenPresented(),
                        Oauth2RedirectUrlResultMatchers.oauth2().isTokenTypePresented(),
                        Oauth2RedirectUrlResultMatchers.oauth2().isStateParamPresented(),
                        Oauth2RedirectUrlResultMatchers.oauth2().isExpiresInParamPresented(),
                        testJwtToken(scopesOauth2Spec));
    }

    private ResultMatcher testJwtToken(String scopesOauth2Spec) {
        return result -> {
            String redirectedUrl = result.getResponse().getRedirectedUrl();
            MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromHttpUrl(redirectedUrl).build().getQueryParams();
            String token = queryParams.getFirst(Oauth2Constants.ACCESS_TOKEN);
            JwtTokenResultMatchers.jwt().isTokenValid(token).match(result);
            JwtTokenResultMatchers.jwt().isClaimPresented(token, Oauth2AccessTokenGenerator.SCOPE).match(result);
            JwtTokenResultMatchers.jwt().isClaimTypeCorrect(token, Oauth2AccessTokenGenerator.SCOPE, String.class).match(result);
            JwtTokenResultMatchers.jwt().isClaimEqualTo(token, Oauth2AccessTokenGenerator.SCOPE, scopesOauth2Spec).match(result);
        };
    }

    @Test
    void loginCheckAndGrantTypeProcessingWithFormData() {
    }
}

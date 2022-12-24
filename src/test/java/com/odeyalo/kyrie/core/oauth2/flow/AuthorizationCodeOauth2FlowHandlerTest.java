package com.odeyalo.kyrie.core.oauth2.flow;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.oauth2.Oauth2FlowSideType;
import com.odeyalo.kyrie.core.oauth2.Oauth2Token;
import com.odeyalo.kyrie.core.oauth2.tokens.code.AuthorizationCode;
import com.odeyalo.kyrie.core.oauth2.tokens.code.AuthorizationCodeGeneratorImpl;
import com.odeyalo.kyrie.core.oauth2.tokens.code.InMemoryAuthorizationCodeStore;
import com.odeyalo.kyrie.core.oauth2.tokens.code.provider.DefaultAuthorizationCodeProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for AuthorizationCodeOauth2FlowHandler class.
 * @see AuthorizationCodeOauth2FlowHandler
 */
class AuthorizationCodeOauth2FlowHandlerTest {
    private final InMemoryAuthorizationCodeStore codeStore = new InMemoryAuthorizationCodeStore();

    private final AuthorizationCodeOauth2FlowHandler authorizationCodeOauth2FlowHandler = new AuthorizationCodeOauth2FlowHandler(new DefaultAuthorizationCodeProvider(
            new AuthorizationCodeGeneratorImpl(), codeStore
    ));
    public static final String CLIENT_ID = "client_id";
    public static final String USER_ID = "1";


    @Test
    @DisplayName("Handle the authorization_code oauth2 flow and expect authorization code as result")
    void handleFlow() {
        AuthorizationRequest request = AuthorizationRequest
                .builder()
                .clientId(CLIENT_ID)
                .scopes(new String[]{"read", "write"})
                .redirectUrl("http://localhost:9000/callback")
                .grantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .responseTypes(Oauth2ResponseType.CODE)
                .state("state123")
                .build();
        Oauth2User expectedUser = new Oauth2User(USER_ID, "Odeyalo", "password", Set.of("USER"), Collections.emptyMap());
        Oauth2Token oauth2Token = authorizationCodeOauth2FlowHandler.handleFlow(request, expectedUser);

        assertTrue(oauth2Token instanceof AuthorizationCode);
        AuthorizationCode code = (AuthorizationCode) oauth2Token;

        String codeValue = code.getCodeValue();
        Oauth2User actualUser = code.getUser();
        AuthorizationCode fromStore = codeStore.findByAuthorizationCodeValue(codeValue);
        assertEquals(code,  fromStore);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    @DisplayName("Check if correct flow name is provided by class")
    void getFlowName() {
        String actual = authorizationCodeOauth2FlowHandler.getFlowName();
        String expected = AuthorizationGrantType.AUTHORIZATION_CODE.getGrantName();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Check if correct flow side type is provided by class")
    void getFlowType() {
        Oauth2FlowSideType actual = authorizationCodeOauth2FlowHandler.getFlowType();
        Oauth2FlowSideType expected = Oauth2FlowSideType.SERVER_SIDE;
        assertEquals(expected, actual);
    }
}

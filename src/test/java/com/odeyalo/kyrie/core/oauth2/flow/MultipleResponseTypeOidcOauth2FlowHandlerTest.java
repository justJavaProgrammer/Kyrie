package com.odeyalo.kyrie.core.oauth2.flow;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.oauth2.CombinedOauth2Token;
import com.odeyalo.kyrie.core.oauth2.Oauth2FlowSideType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for default methods from MultipleResponseTypeOidcOauth2FlowHandler.
 * @see MultipleResponseTypeOidcOauth2FlowHandler
 */
class MultipleResponseTypeOidcOauth2FlowHandlerTest {

    private final AuthorizationRequest request = AuthorizationRequest.builder()
            .scopes(new String[]{"read", "write"})
            .clientId("clientid")
            .redirectUrl("http://localhost:9000/callback")
            .state("state123")
            .responseTypes(Oauth2ResponseType.TOKEN)
            .build();

    private final CombinedOauth2Token MOCKED_TOKEN = CombinedOauth2Token.builder().tokenValue("token123")
            .issuedAt(Instant.now())
            .expiresIn(Instant.now().plusSeconds(60))
            .addInfo("access_token", "token123")
            .build();

    MultipleResponseTypeOidcOauth2FlowHandler handler = (request, user) -> {
        return MOCKED_TOKEN;
    };

    @Test
    void handleMultipleResponseTypeFlow() {
        CombinedOauth2Token combinedOauth2Token = handler.handleMultipleResponseTypeFlow(request, new Oauth2User("id", "name", "pass", Set.of("USER"), Collections.emptyMap()));
        assertNotNull(combinedOauth2Token, "Method should never return null as result!");
    }

    @Test
    void getFlowName() {
        String flowName = handler.getFlowName();
        assertNotNull(flowName, "Flow name cannot be null!");
        assertEquals(AuthorizationGrantType.MULTIPLE.getGrantName(), flowName, "Flow name must be 'multiple'!");
    }

    @Test
    void getFlowType() {
        Oauth2FlowSideType flowType = handler.getFlowType();
        assertNotNull(flowType, "Flow type cannot be null!");
        assertEquals(Oauth2FlowSideType.BOTH, flowType, "MultipleResponseTypeOidcOauth2FlowHandler must support BOTH flow types!");
    }
}

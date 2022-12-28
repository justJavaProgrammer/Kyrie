package com.odeyalo.kyrie.core.oauth2.flow;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for DefaultClientSideOauth2FlowHandlerFactory
 * @see DefaultClientSideOauth2FlowHandlerFactory
 */
class DefaultClientSideOauth2FlowHandlerFactoryTest {

    DefaultClientSideOauth2FlowHandlerFactory factory = new DefaultClientSideOauth2FlowHandlerFactory(
            List.of(
                    new ImplicitFlowHandlerMock()
            )
    );

    @Test
    @DisplayName("Get implicit flow from factory and expect success")
    void getImplicitHandlerAndExpectSuccess() {
        AuthorizationRequest request = AuthorizationRequest
                .builder()
                .clientId("client_1235")
                .scopes(new String[]{"read", "write"})
                .responseTypes(Oauth2ResponseType.TOKEN)
                .grantType(AuthorizationGrantType.IMPLICIT)
                .build();
        ClientSideOauth2FlowHandler handler = factory.getHandler(request);
        assertNotNull(handler);
        assertTrue(handler instanceof ImplicitFlowHandlerMock);
    }


    private static final class ImplicitFlowHandlerMock implements ClientSideOauth2FlowHandler {
        @Override
        public Oauth2AccessToken handleFlow(AuthorizationRequest request, Oauth2User user) {
            return null;
        }

        @Override
        public String getFlowName() {
            return AuthorizationGrantType.IMPLICIT.getGrantName();
        }
    }
}

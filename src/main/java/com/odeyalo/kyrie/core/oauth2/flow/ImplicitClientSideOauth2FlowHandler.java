package com.odeyalo.kyrie.core.oauth2.flow;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessToken;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.Oauth2AccessTokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Oauth2 token flow that handles Implicit flow
 * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-1.3.2">Implict oauth2 flow</a>
 * @version 1.0
 */
@Component
public class ImplicitClientSideOauth2FlowHandler implements ClientSideOauth2FlowHandler {
    private final Oauth2AccessTokenGenerator accessTokenGenerator;

    @Autowired
    public ImplicitClientSideOauth2FlowHandler(Oauth2AccessTokenGenerator accessTokenGenerator) {
        this.accessTokenGenerator = accessTokenGenerator;
    }

    @Override
    public Oauth2AccessToken handleFlow(AuthorizationRequest request, Oauth2User user) {
        return accessTokenGenerator.generateAccessToken(user, request.getScopes());
    }

    @Override
    public String getFlowName() {
        return AuthorizationGrantType.IMPLICIT.getGrantName();
    }
}

package com.odeyalo.kyrie.core.oauth2.flow;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.oauth2.Oauth2FlowSideType;
import com.odeyalo.kyrie.core.oauth2.Oauth2Token;
import com.odeyalo.kyrie.core.oauth2.tokens.code.provider.AuthorizationCodeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Handle the authorization_code flow.
 * @version 1.0
 */
@Component
public class AuthorizationCodeOauth2FlowHandler implements Oauth2FlowHandler {
    private final AuthorizationCodeProvider authorizationCodeProvider;

    @Autowired
    public AuthorizationCodeOauth2FlowHandler(AuthorizationCodeProvider authorizationCodeProvider) {
        this.authorizationCodeProvider = authorizationCodeProvider;
    }

    @Override
    public Oauth2Token handleFlow(AuthorizationRequest authorizationRequest, Oauth2User user) {
        return authorizationCodeProvider.getAuthorizationCode(authorizationRequest.getClientId(), user, authorizationRequest.getScopes());
    }

    @Override
    public String getFlowName() {
        return AuthorizationGrantType.AUTHORIZATION_CODE.getGrantName();
    }

    @Override
    public Oauth2FlowSideType getFlowType() {
        return Oauth2FlowSideType.SERVER_SIDE;
    }
}

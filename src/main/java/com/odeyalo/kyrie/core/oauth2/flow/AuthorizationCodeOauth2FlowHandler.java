package com.odeyalo.kyrie.core.oauth2.flow;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.oauth2.Oauth2FlowSideType;
import com.odeyalo.kyrie.core.oauth2.Oauth2Token;
import com.odeyalo.kyrie.core.oauth2.tokens.code.provider.AuthorizationCodeProvider;

/**
 * Handle the authorization_code flow.
 * The Handler is used to return an authorization code that will be used to obtain an access token
 *
 * @version 1.0
 */
public class AuthorizationCodeOauth2FlowHandler implements Oauth2FlowHandler {
    private final AuthorizationCodeProvider authorizationCodeProvider;

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

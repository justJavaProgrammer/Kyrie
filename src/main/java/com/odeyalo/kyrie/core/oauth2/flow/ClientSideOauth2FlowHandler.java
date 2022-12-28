package com.odeyalo.kyrie.core.oauth2.flow;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.oauth2.Oauth2FlowSideType;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessToken;
import com.odeyalo.kyrie.exceptions.Oauth2Exception;

/**
 * Support client-side oauth2 flow.
 * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-1.3.2">Implict oauth2 flow</a>
 */
public interface ClientSideOauth2FlowHandler extends Oauth2FlowHandler {

    /**
     * Handle the client-side flow types and return Oauth2AccessToken
     * @param request - AuthorizationRequest with all fields set and request is valid
     * @param user    - user that granted permission
     * @return - Oauth2AccessToken with required fields set.
     * @throws Oauth2Exception - if any exception was occurred during flow handling
     */
    @Override
    Oauth2AccessToken handleFlow(AuthorizationRequest request, Oauth2User user) throws Oauth2Exception;

    /**
     * Handle the client-side oauth2 flow by delegating job to handleFlow(request, user) method.
     * It was written to make code more readable
     * @param request - request with authorization info
     * @param user - user that granted access
     * @return - Oauth2AccessToken
     * @see Oauth2FlowHandler#handleFlow(AuthorizationRequest, Oauth2User)
     */
    default Oauth2AccessToken handleClientSideFlow(AuthorizationRequest request, Oauth2User user) {
        return handleFlow(request, user);
    }

    @Override
    default Oauth2FlowSideType getFlowType() {
        return Oauth2FlowSideType.CLIENT_SIDE;
    }
}

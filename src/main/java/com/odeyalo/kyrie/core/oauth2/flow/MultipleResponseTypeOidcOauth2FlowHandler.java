package com.odeyalo.kyrie.core.oauth2.flow;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.oauth2.CombinedOauth2Token;
import com.odeyalo.kyrie.core.oauth2.Oauth2FlowSideType;
import com.odeyalo.kyrie.exceptions.Oauth2Exception;

/**
 * Handler to handle flows with multiple response types that was provided by OIDC specification.
 * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#HybridFlowAuth">Hybrid Flow</a>
 * @version 1.0
 */
public interface MultipleResponseTypeOidcOauth2FlowHandler extends Oauth2FlowHandler {

    /**
     * Id token field that CAN be presented in response type
     */
    String ID_TOKEN_KEY = "id_token";
    /**
     * Access token field that CAN be presented in response type
     */
    String ACCESS_TOKEN_KEY = "access_token";
    /**
     * Authorization code field that CAN be presented in response type
     */
    String AUTHORIZATION_CODE_TOKEN_KEY = "code";

    /**
     * Handle flow with multiple response types and return CombinedOauth2Token as result.
     * @param request - AuthorizationRequest with all fields set and request is valid
     * @param user    - user that granted permission
     * @return - CombinedOauth2Token with required fields set. Never null
     * @throws Oauth2Exception - if any exception was occurred during flow handling
     */
    @Override
    CombinedOauth2Token handleFlow(AuthorizationRequest request, Oauth2User user) throws Oauth2Exception;

    /**
     * Delegate job to handleFlow(request, user) method.
     * It was written to make code more readable
     * Method to handle multiple response type flow(s) from OIDC specification.
     * @param request - request with all fields set
     * @param user - user that granted permission and already logged in
     * @return - CombinedOauth2Token with required fields set
     */
    default CombinedOauth2Token handleMultipleResponseTypeFlow(AuthorizationRequest request, Oauth2User user) {
        return handleFlow(request, user);
    }

    @Override
    default String getFlowName() {
        return AuthorizationGrantType.MULTIPLE.getGrantName();
    }

    /**
     * Since Multiple response type flow supports Client and Server side flow
     * @return - Oauth2FlowType.BOTH
     */
    @Override
    default Oauth2FlowSideType getFlowType() {
        return Oauth2FlowSideType.BOTH;
    }
}

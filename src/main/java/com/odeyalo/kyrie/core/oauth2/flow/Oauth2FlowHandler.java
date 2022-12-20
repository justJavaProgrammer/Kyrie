package com.odeyalo.kyrie.core.oauth2.flow;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.oauth2.Oauth2FlowSideType;
import com.odeyalo.kyrie.core.oauth2.Oauth2Token;

/**
 * Handle specific Oauth2 flow
 */
public interface Oauth2FlowHandler {

    /**
     * Handle an oauth2 flow.
     * @param request - AuthorizationRequest with all fields set
     * @param user - user that granted permission
     * @return - Oauth2Token any implementation for given flow.
     */
    Oauth2Token handleFlow(AuthorizationRequest request, Oauth2User user);
    /**
     * Name of flow that this implementation supports
     * @return - flow name
     */
    String getFlowName();

    /**
     * Return flow side type that this implementation support.
     * @return - flow side type.
     * @see Oauth2FlowSideType
     */
    Oauth2FlowSideType getFlowType();
}

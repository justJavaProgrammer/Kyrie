package com.odeyalo.kyrie.core.oauth2.flow;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.oauth2.Oauth2FlowSideType;
import com.odeyalo.kyrie.core.oauth2.Oauth2Token;
import com.odeyalo.kyrie.exceptions.Oauth2Exception;

/**
 * Handle specific Oauth2 flow
 */
public interface Oauth2FlowHandler {

    /**
     * Handle an oauth2 flow.
     * The handleFlow SHOULD NOT make any AuthorizationRequest or Oauth2User checks
     * because AuthorizationRequest and Oauth2User params are already checked and valid.
     * @param request - AuthorizationRequest with all fields set and request is valid
     * @param user    - user that granted permission
     * @return - Oauth2Token any implementation for given flow. Implementation should NEVER return null as result.
     * @throws Oauth2Exception - if any exception was occurred during flow
     */
    Oauth2Token handleFlow(AuthorizationRequest request, Oauth2User user) throws Oauth2Exception;

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

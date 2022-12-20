package com.odeyalo.kyrie.core.oauth2.flow;

import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;

/**
 * Produce Oauth2FlowHandler based on AuthorizationRequest.
 * @see AuthorizationRequest
 * @see Oauth2FlowHandler
 */
public interface Oauth2FlowHandlerFactory {

    Oauth2FlowHandler getOauth2FlowHandler(AuthorizationRequest request);

}

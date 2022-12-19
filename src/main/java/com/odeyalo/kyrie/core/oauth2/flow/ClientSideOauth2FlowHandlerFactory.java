package com.odeyalo.kyrie.core.oauth2.flow;


import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;

/**
 * Factory to produce a ClientSideOauth2FlowHandler implementation based on AuthorizationRequest.
 */
public interface ClientSideOauth2FlowHandlerFactory {

    /**
     * Return or create a ClientSideOauth2FlowHandler
     * @param request - AuthorizationRequest with all fields
     * @return - ClientSideOauth2FlowHandler based on AuthorizationRequest
     */
    ClientSideOauth2FlowHandler getHandler(AuthorizationRequest request);

}

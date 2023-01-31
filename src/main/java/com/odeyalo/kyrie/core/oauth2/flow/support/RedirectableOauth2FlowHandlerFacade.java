package com.odeyalo.kyrie.core.oauth2.flow.support;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.oauth2.flow.Oauth2FlowHandler;
import com.odeyalo.kyrie.exceptions.Oauth2Exception;

/**
 * Facade interface for {@link Oauth2FlowHandler} and {@link com.odeyalo.kyrie.core.oauth2.support.RedirectUrlCreationService}.
 */
public interface RedirectableOauth2FlowHandlerFacade {

    /**
     * Handle the flow and return the Oauth2Token
     * @param user - user that has been authenticated
     * @param request - current AuthorizationRequest
     * @return - redirect uri to redirect user after authentication
     * @throws Oauth2Exception - if any exception has been occurred
     */
    String handleFlow(Oauth2User user, AuthorizationRequest request) throws Oauth2Exception;

}

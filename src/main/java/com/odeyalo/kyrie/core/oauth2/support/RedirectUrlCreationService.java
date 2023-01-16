package com.odeyalo.kyrie.core.oauth2.support;

import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.oauth2.Oauth2Token;

/**
 * Create a redirect url for specific grant type by Oauth2 and Oidc Specifications.
 */
public interface RedirectUrlCreationService {

    /**
     * Create redirect request by request metadata and token
     * @param request - AuthorizationRequest with all fields set
     * @param token - generated token from Oauth2FlowHandler
     * @return - redirect url required by specific flow
     * @see com.odeyalo.kyrie.core.oauth2.flow.Oauth2FlowHandler
     */
    String createRedirectUrl(AuthorizationRequest request, Oauth2Token token);


    /**
     * Grant type that specific implementation supports
     * @return - supported grant type
     */
    AuthorizationGrantType supportedGrantType();
}

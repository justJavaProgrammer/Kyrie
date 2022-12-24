package com.odeyalo.kyrie.core.oauth2.support;

import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;

/**
 * Factory to produce RedirectUrlCreationService
 */
public interface RedirectUrlCreationServiceFactory {

    /**
     * Create or return RedirectUrlCreationService based on request
     * @param request - valid AuthorizationRequest
     * @return - RedirectUrlCreationService or null
     */
    RedirectUrlCreationService getRedirectUrlCreationService(AuthorizationRequest request);

}

package com.odeyalo.kyrie.core.oauth2.support;

import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;

/**
 * More specified interface that used to only to handle implicit flow
 */
public interface ImplicitFlowRedirectUrlCreationService extends RedirectUrlCreationService {
    /**
     * All implementations must return 'implicit' grant type
     * @return - implicit grant type
     */
    @Override
    default AuthorizationGrantType supportedGrantType() {
        return AuthorizationGrantType.IMPLICIT;
    }
}

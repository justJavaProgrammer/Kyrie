package com.odeyalo.kyrie.core.oauth2.support;

import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;

/**
 * More specific interface to handle only {@link AuthorizationGrantType#MULTIPLE} flow.
 *
 * @see AuthorizationGrantType#MULTIPLE
 */
public interface MultipleResponseTypeFlowRedirectUrlCreationService extends RedirectUrlCreationService {
    /**
     * All implementation must return AuthorizationGrantType.MULTIPLE
     * @return - {@link AuthorizationGrantType#MULTIPLE} grant type
     */
    @Override
    default AuthorizationGrantType supportedGrantType() {
        return AuthorizationGrantType.MULTIPLE;
    }
}

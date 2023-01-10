package com.odeyalo.kyrie.core.oauth2.tokens;

import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.exceptions.Oauth2Exception;

/**
 * Strategy to obtain an access token for specific Oauth2 Flow
 */
public interface AccessTokenGranterStrategy {

    /**
     * Obtain an access token by {@link TokenRequest}
     * @param request - request from {@link com.odeyalo.kyrie.controllers.TokenController}
     * @return - Oauth2AccessToken that was built by TokenRequest
     * @throws Oauth2Exception - if any exception occurred
     */
    Oauth2AccessToken obtainAccessToken(TokenRequest request) throws Oauth2Exception;

    /**
     * Type of grant that this strategy supports
     * @return - supported grant type by strategy
     */
    AuthorizationGrantType grantType();

    /**
     * True if {@link TokenRequest} contains valid grant type, false otherwise
     * @param request - request that was received from controller
     * @return - true if request is valid, false otherwise
     */
    default boolean isValid(TokenRequest request) {
        return request.getGrantType() == grantType();
    }
}

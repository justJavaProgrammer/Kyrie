package com.odeyalo.kyrie.core.oauth2.tokens;

import com.odeyalo.kyrie.dto.TokensResponse;

/**
 * Return access token by authorization code
 */
public interface AccessTokenReturner {

    /**
     * Return the token if the all checks has been passed
     * @param clientId - client id
     * @param clientSecret - client secret
     * @param authorizationCode - authorization code that client got from '/oauth/authorize' endpoint
     * @return - TokensResponse with all fields included
     */
    TokensResponse getToken(String clientId, String clientSecret, String authorizationCode) throws ObtainTokenException;

}

package com.odeyalo.kyrie.core.oauth2.tokens.code.provider;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.oauth2.tokens.code.AuthorizationCode;

/**
 * Provide authorization code that will be used to obtain an access token
 * Facade interface to generate and do something with generated token
 */
public interface AuthorizationCodeProvider {

    /**
     * Return an authorization code for client
     * @param clientId - client id that requested authorization
     * @param user - user that granted access
     * @return - authorization code
     */
    AuthorizationCode getAuthorizationCode(String clientId, Oauth2User user, String[] scopes);

}

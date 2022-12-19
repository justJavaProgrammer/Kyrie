package com.odeyalo.kyrie.core.oauth2.tokens.code;

import com.odeyalo.kyrie.core.Oauth2User;

/**
 * Manager to provide one entrypoint to working with Authorization code.
 * Provides functionality to generate and save authorization code, find authorization code and delete it
 */
public interface AuthorizationCodeManager {

    /**
     * Generate authorization code
     * @param clientId - client id
     * @return - Generated AuthorizationCode
     */
    AuthorizationCode generateAuthorizationCode(String clientId, Oauth2User user, String[] scopes);

    /**
     * Find authorization code by client id
     * @param authCode - authorization code
     * @return - Authorization code, null otherwise
     */
    AuthorizationCode getAuthorizationCodeByAuthorizationCodeValue(String authCode);

    /**
     * Delete authorization code by client id
     * @param clientId - client id
     */
    void deleteAuthorizationCode(String clientId);

}

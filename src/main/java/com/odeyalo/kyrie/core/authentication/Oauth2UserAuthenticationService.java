package com.odeyalo.kyrie.core.authentication;

/**
 * Service to authenticate the user
 */
public interface Oauth2UserAuthenticationService {

    /**
     * Authenticate user and return result
     * @param info - user credentials
     * @return - AuthenticationResult
     */
    AuthenticationResult authenticate(Oauth2UserAuthenticationInfo info);
}

package com.odeyalo.kyrie.core;

import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;
import com.odeyalo.kyrie.core.oauth2.Oauth2Token;

/**
 * Facade to generate an oauth2 token based on scopes.
 */
public interface Oauth2TokenGeneratorFacade {

    /**
     * Generate the oauth2 token to user based on provided scopes
     * @param user - Oauth2User that contains info
     * @param scopes - required scopes
     * @return - generated Oauth2Token(Any implementation)
     */
    Oauth2Token generateToken(Oauth2ClientCredentials credentials, Oauth2User user, String[] scopes);
}

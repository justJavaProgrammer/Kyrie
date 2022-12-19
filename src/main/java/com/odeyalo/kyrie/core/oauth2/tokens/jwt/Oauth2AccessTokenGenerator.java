package com.odeyalo.kyrie.core.oauth2.tokens.jwt;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessToken;

/**
 * Generate an access token based on scopes for user
 */
public interface Oauth2AccessTokenGenerator {
    String SCOPE = "scope";

    /**
     * Generate an access token with specific scopes for user that granted access
     * @param user - user that granted access
     * @param scopes - scopes to include in access token
     * @return - Oauth2AccessToken
     */
    Oauth2AccessToken generateAccessToken(Oauth2User user, String[] scopes);

}

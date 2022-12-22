package com.odeyalo.kyrie.core.oauth2.tokens;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;

/**
 * Manage the oauth2 access tokens, generate, validate and return obtained access tokens.
 */
public interface Oauth2AccessTokenManager {

    /**
     * Generate an access token to user with specific scopes
     * @param user - user that granted permission
     * @param scopes - scopes that was granted
     * @return - AccessTokenMetadata
     */
    Oauth2AccessToken generateAccessToken(Oauth2User user, String[] scopes);

    /**
     * Return the access token info containing in this token
     * @param token - token to get info
     * @return - Oauth2AccessToken info
     */

    Oauth2AccessToken getTokenInfo(String token);

    /**
     * Validate a token and return validation result
     * @param token - token to validate
     * @return - TokenValidationResult
     */
    TokenValidationResult validateAccessToken(String token);


    /**
     * Obtain an access token by given authorization code.
     * Also may check client credentials
     * @param clientCredentials - client credentials that requested token obtaining
     * @param authorizationCode - authorization code that was got from '/oauth2/authorize' endpoint
     * @return - AccessTokenInformation if token obtaining was successful
     */
    Oauth2AccessToken obtainAccessTokenByAuthorizationCode(Oauth2ClientCredentials clientCredentials, String authorizationCode);
}

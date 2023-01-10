package com.odeyalo.kyrie.core.oauth2.tokens;

import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;
import com.odeyalo.kyrie.exceptions.Oauth2Exception;

/**
 * Return access token by authorization code
 */
public interface AuthorizationCodeFlowAccessTokenReturner {

    /**
     * Return the token if the all checks has been passed
     * @param clientId - client id
     * @param clientSecret - client secret
     * @param authorizationCode - authorization code that client got from '/oauth/authorize' endpoint
     * @return - TokensResponse with all fields included
     */
    Oauth2AccessToken getToken(String clientId, String clientSecret, String authorizationCode);


    default Oauth2AccessToken getToken(Oauth2ClientCredentials credentials, String authorizationCode) throws Oauth2Exception {
        return getToken(credentials.getClientId(), credentials.getClientSecret(), authorizationCode);
    }
}

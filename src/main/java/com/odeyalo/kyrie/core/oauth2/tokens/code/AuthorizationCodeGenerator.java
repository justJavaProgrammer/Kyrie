package com.odeyalo.kyrie.core.oauth2.tokens.code;

import com.odeyalo.kyrie.core.Oauth2User;

/**
 * Generate an authorization code for client after an end-user was authenticated
 */
public interface AuthorizationCodeGenerator {

    /**
     * Default required code length
     */
    Integer DEFAULT_CODE_LENGTH = 24;
    /**
     * Time to expire generated authorization code, in seconds
     */
    Integer DEFAULT_AUTHORIZATION_CODE_EXPIRE_TIME_SECONDS = 60;

    default AuthorizationCode generateAuthorizationCode(Oauth2User user, String[] scopes) {
        return generateAuthorizationCode(DEFAULT_CODE_LENGTH, DEFAULT_AUTHORIZATION_CODE_EXPIRE_TIME_SECONDS, user, scopes);
    }

    /**
     * Generate and return authorization code that will be used to obtain an access token
     *
     * @param codeLength        - authorization code length
     * @param expireTimeSeconds - live time of the code, in seconds
     * @param user              - user that grant access(End-user)
     * @param scopes            - scopes to this authorization code
     * @return - generated authorization code
     */
    AuthorizationCode generateAuthorizationCode(Integer codeLength, Integer expireTimeSeconds, Oauth2User user, String[] scopes);

}

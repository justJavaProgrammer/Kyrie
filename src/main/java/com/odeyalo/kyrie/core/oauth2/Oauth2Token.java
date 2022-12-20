package com.odeyalo.kyrie.core.oauth2;

import java.time.Instant;

/**
 * Interface to represent the oauth2 token. Token can different types, such Access token, Refresh Token, ID token, etc
 */
public interface Oauth2Token {

    /**
     * String representation of a token
     * @return - token value
     */
    String getTokenValue();

    /**
     * Time at which a token was issued
     * @return - Instant with time at which token was issued
     */
    default Instant getIssuedAt() {
        return null;
    }

    /**
     * Time when the token will expire
     * @return - Instant at which token will expire
     */
    default Instant getExpiresIn() {
        return null;
    }
}

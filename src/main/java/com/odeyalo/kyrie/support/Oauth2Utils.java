package com.odeyalo.kyrie.support;

import com.odeyalo.kyrie.core.oauth2.Oauth2Token;

import java.util.Optional;

/**
 * Utility methods to work with oauth2
 */
public abstract class Oauth2Utils {
    /**
     * Return the difference between getExpiresIn and getIssuedAt and return it in NumericDate format
     *
     * @param token - token to check
     * @return - Value wrapped in Optional or Optional.empty
     */
    public static Optional<Long> getExpiresIn(Oauth2Token token) {
        if (token.getExpiresIn() != null && token.getIssuedAt() != null) {
            return Optional.of(token.getExpiresIn().getEpochSecond() - token.getIssuedAt().getEpochSecond());
        }
        return Optional.empty();
    }
}

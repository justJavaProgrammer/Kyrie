package com.odeyalo.kyrie.core.oauth2.tokens;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * Represent metadata about token.
 */
@AllArgsConstructor
@Data
public class TokenMetadata {
    // Is token currently active.
    private final boolean active;
    // Token value
    private final String token;
    // When the token was issued in NumericDate format
    private final Long issuedAt;
    // When the token will expire in NumericDate format
    private final Long expiresIn;
    // Token claims
    private final Map<String, Object> claims;

    public static TokenMetadata invalid() {
        return new TokenMetadata(false, null, null, null, null);
    }
}

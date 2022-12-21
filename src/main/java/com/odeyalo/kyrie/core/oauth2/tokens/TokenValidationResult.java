package com.odeyalo.kyrie.core.oauth2.tokens;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Validation result of token validation
 */
@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenValidationResult {
    private boolean valid;
    private String message;

    private TokenValidationResult(boolean valid) {
        this.valid = valid;
    }

    public static TokenValidationResult valid() {
        return new TokenValidationResult(true);
    }

    public static TokenValidationResult valid(String message) {
        return new TokenValidationResult(true, message);
    }

    public static TokenValidationResult invalid() {
        return new TokenValidationResult(false);
    }

    public static TokenValidationResult invalid(String message) {
        return new TokenValidationResult(false, message);
    }
}

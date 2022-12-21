package com.odeyalo.kyrie.core.support;

import lombok.*;

/**
 * Result of the data validation
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationResult {
    protected boolean success;
    protected String message;

    protected ValidationResult(boolean success) {
        this.success = success;
    }

    public static ValidationResult success() {
        return new ValidationResult(true);
    }

    /**
     * @param message - message with description why validation has failed
     * @return - failed ValidationResult
     */
    public static ValidationResult failed(String message) {
        return new ValidationResult(false, message);
    }
}

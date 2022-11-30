package com.odeyalo.kyrie.core.oauth2.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of the data validation
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidationResult {
    private boolean success;


    public static ValidationResult success() {
        return new ValidationResult(true);
    }

    public static ValidationResult failed() {
        return new ValidationResult(false);
    }
}

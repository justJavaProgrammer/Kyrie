package com.odeyalo.kyrie.core.support;

import com.odeyalo.kyrie.exceptions.Oauth2ErrorType;
import lombok.Getter;

/**
 * ValidationResult implementation only for Oauth2 process.
 */
@Getter
public class Oauth2ValidationResult extends ValidationResult {
    private Oauth2ErrorType errorType;

    public Oauth2ValidationResult(boolean success) {
        super(success);
    }

    private Oauth2ValidationResult(boolean success, Oauth2ErrorType errorType, String description) {
        super(success, description);
        this.errorType = errorType;
    }

    public static Oauth2ValidationResult success() {
        return new Oauth2ValidationResult(true);
    }

    public static Oauth2ValidationResult failed(Oauth2ErrorType errorType, String description) {
        return new Oauth2ValidationResult(false, errorType, description);
    }
}

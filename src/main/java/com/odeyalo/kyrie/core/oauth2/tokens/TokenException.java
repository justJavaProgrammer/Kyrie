package com.odeyalo.kyrie.core.oauth2.tokens;

import com.odeyalo.kyrie.exceptions.Oauth2ErrorType;
import com.odeyalo.kyrie.exceptions.Oauth2Exception;

/**
 * Exception when any exception was occurred during parsing, generating, etc any token
 */
public class TokenException extends Oauth2Exception {

    public TokenException(String message, String description, Oauth2ErrorType errorType) {
        super(message, description, errorType);
    }

    public TokenException(String message, String description, Oauth2ErrorType errorType, Throwable cause) {
        super(message, description, errorType, cause);
    }
}

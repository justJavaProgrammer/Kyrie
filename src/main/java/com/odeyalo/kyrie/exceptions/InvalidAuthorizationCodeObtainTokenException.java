package com.odeyalo.kyrie.exceptions;

import com.odeyalo.kyrie.core.oauth2.tokens.TokenException;
import com.odeyalo.kyrie.exceptions.Oauth2ErrorType;

/**
 * Throw it when received wrong authorization code and token can't be obtained
 */
public class InvalidAuthorizationCodeObtainTokenException extends TokenException {

    public InvalidAuthorizationCodeObtainTokenException(String errorDescription) {
        super(errorDescription, errorDescription, Oauth2ErrorType.INVALID_GRANT);
    }

    public InvalidAuthorizationCodeObtainTokenException(String message, String errorDescription) {
        super(message, errorDescription, Oauth2ErrorType.INVALID_GRANT);
    }

    public InvalidAuthorizationCodeObtainTokenException(String message, Throwable cause, String errorDescription) {
        super(message, errorDescription, Oauth2ErrorType.INVALID_GRANT, cause);
    }
}

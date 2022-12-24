package com.odeyalo.kyrie.exceptions;

/**
 * Client authentication failed, such as if the request contains an invalid client ID or secret then throw it
 */
public class InvalidClientCredentialsException extends Oauth2Exception {

    public InvalidClientCredentialsException(String description) {
        super(description, description, Oauth2ErrorType.INVALID_CLIENT);
    }
    public InvalidClientCredentialsException(String message, String description) {
        super(message, description, Oauth2ErrorType.INVALID_CLIENT);
    }

    public InvalidClientCredentialsException(String message, String description, Throwable cause) {
        super(message, description, Oauth2ErrorType.INVALID_CLIENT, cause);
    }
}

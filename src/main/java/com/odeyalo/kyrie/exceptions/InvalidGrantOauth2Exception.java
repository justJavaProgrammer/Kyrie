package com.odeyalo.kyrie.exceptions;

/**
 * Used to represent invalid_grant error
 */
public class InvalidGrantOauth2Exception extends Oauth2Exception {

    public InvalidGrantOauth2Exception(String message, String description) {
        super(message, description, Oauth2ErrorType.INVALID_GRANT);
    }
}

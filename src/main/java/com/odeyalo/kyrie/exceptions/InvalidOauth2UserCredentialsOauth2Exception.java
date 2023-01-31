package com.odeyalo.kyrie.exceptions;

/**
 * Exception to throw when the user provided invalid credentials and authentication cannot be performed
 */
public class InvalidOauth2UserCredentialsOauth2Exception extends Oauth2Exception {
    public static final Oauth2ErrorType INVALID_USER_CREDENTIALS_ERROR_TYPE = new Oauth2ErrorType("ivnvalid_credentials");

    public InvalidOauth2UserCredentialsOauth2Exception(String message) {
        super(message, message, INVALID_USER_CREDENTIALS_ERROR_TYPE);
    }

    public InvalidOauth2UserCredentialsOauth2Exception(String message, String description) {
        super(message, description, INVALID_USER_CREDENTIALS_ERROR_TYPE);
    }

    public InvalidOauth2UserCredentialsOauth2Exception(String message, String description, Throwable cause) {
        super(message, description, INVALID_USER_CREDENTIALS_ERROR_TYPE, cause);
    }
}

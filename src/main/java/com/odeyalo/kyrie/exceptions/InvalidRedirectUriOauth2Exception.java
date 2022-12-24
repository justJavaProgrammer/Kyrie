package com.odeyalo.kyrie.exceptions;

/**
 * Don't extend it with RedirectUriAwareOauth2Exception since if we have wrong redirect_uri we can't redirect it and should return page with error to end-user
 */
public class InvalidRedirectUriOauth2Exception extends Oauth2Exception {

    public InvalidRedirectUriOauth2Exception(String message, String description) {
        super(message, description, Oauth2ErrorType.INVALID_REDIRECT_URI);
    }

    public InvalidRedirectUriOauth2Exception(String message, String description, Throwable cause) {
        super(message, description, Oauth2ErrorType.INVALID_REDIRECT_URI, cause);
    }
}

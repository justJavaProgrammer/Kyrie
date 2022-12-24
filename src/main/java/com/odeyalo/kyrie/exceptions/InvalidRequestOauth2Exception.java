package com.odeyalo.kyrie.exceptions;

/**
 * Represent an "invalid_request" oauth2 error.
 * <p>invalid_request: The request is missing a required parameter, includes an invalid parameter value, or is otherwise malformed.</p>
 */
public class InvalidRequestOauth2Exception extends RedirectUriAwareOauth2Exception {

    public InvalidRequestOauth2Exception(String message, String responseMessage, String redirectUri) {
        super(message, responseMessage, redirectUri, Oauth2ErrorType.INVALID_REQUEST);
    }

    public InvalidRequestOauth2Exception(String message, String responseMessage, Throwable cause, String redirectUri) {
        super(message, responseMessage, cause, redirectUri, Oauth2ErrorType.INVALID_REQUEST);
    }
}

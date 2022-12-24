package com.odeyalo.kyrie.exceptions;

import lombok.Getter;
import lombok.Setter;

/**
 * Exception that extends Oauth2Exception and contain redirectUri.
 * It useful when we need to redirect client to redirect_uri with error and error_description params
 * @see Oauth2Exception
 */
@Getter
@Setter
public class RedirectUriAwareOauth2Exception extends Oauth2Exception {
    // Client redirect uri that was provided in /oauth2/authorize request
    // This uri will be used to redirect user with error and error_description props defined in oauth2 Specification
    protected final String redirectUri;

    public RedirectUriAwareOauth2Exception(String message, String description, String redirectUri, Oauth2ErrorType errorType) {
        super(message, description, errorType);
        this.redirectUri = redirectUri;
    }

    public RedirectUriAwareOauth2Exception(String message, String description, Throwable cause, String redirectUri, Oauth2ErrorType errorType) {
        super(message, description, errorType, cause);
        this.redirectUri = redirectUri;
    }
}

package com.odeyalo.kyrie.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represent an oauth2 exception that can be occurred during Oauth2 process.
 *
 * @see <a href="https://www.oauth.com/oauth2-servers/server-side-apps/possible-errors/">Possible Oauth2 Erros</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Oauth2Exception extends RuntimeException {
    // Will be used to return in error_description param
    protected final String description;
    // Used to specify an error that was occurred.
    protected final Oauth2ErrorType errorType;

    public Oauth2Exception(String message, String description, Oauth2ErrorType errorType) {
        super(message);
        this.description = description;
        this.errorType = errorType;
    }

    public Oauth2Exception(String message, String description, Oauth2ErrorType errorType, Throwable cause) {
        super(message, cause);
        this.description = description;
        this.errorType = errorType;
    }
}

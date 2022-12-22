package com.odeyalo.kyrie.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Describe the default Oauth2 errors that can be occurred.
 *
 * @see <a href="https://www.oauth.com/oauth2-servers/server-side-apps/possible-errors/">Possible Oauth2 Errors</a>
 */
@AllArgsConstructor
@Data
public class Oauth2ErrorType {
    public static final Oauth2ErrorType INVALID_REQUEST = new Oauth2ErrorType("invalid_request");
    public static final Oauth2ErrorType INVALID_REDIRECT_URI = new Oauth2ErrorType("invalid_redirect_uri");
    public static final Oauth2ErrorType INVALID_CLIENT = new Oauth2ErrorType("invalid_client");
    public static final Oauth2ErrorType INVALID_SCOPE = new Oauth2ErrorType("invalid_scope");
    public static final Oauth2ErrorType INVALID_GRANT = new Oauth2ErrorType("invalid_grant");
    public static final Oauth2ErrorType UNAUTHORIZED_CLIENT = new Oauth2ErrorType("unauthorized_client");
    public static final Oauth2ErrorType UNSUPPORTED_RESPONSE_TYPE = new Oauth2ErrorType("unsupported_response_type");
    public static final Oauth2ErrorType SERVER_ERROR = new Oauth2ErrorType("server_error");
    public static final Oauth2ErrorType TEMPORARILY_UNAVAILABLE = new Oauth2ErrorType("temporarily_unavailable");
    // To create a custom oauth2 error
    private String errorName;
}

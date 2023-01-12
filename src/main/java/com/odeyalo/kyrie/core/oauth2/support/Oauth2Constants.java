package com.odeyalo.kyrie.core.oauth2.support;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Generic constants with different names from Oauth2 Specification
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Oauth2Constants {
    public static final String ACCESS_TOKEN = "access_token";
    public static final String STATE = "state";
    public static final String TOKEN_TYPE = "token_type";
    public static final String EXPIRES_IN = "expires_in";
    public static final String SCOPE = "scope";
    public static final String GRANT_TYPE = "grant_type";
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String ERROR_PARAMETER_NAME = "error";
    public static final String ERROR_DESCRIPTION_PARAMETER_NAME = "error_description";
}

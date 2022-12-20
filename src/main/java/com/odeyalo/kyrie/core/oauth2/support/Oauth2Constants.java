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
}

package com.odeyalo.kyrie.core.oauth2.oidc;

import java.util.Collections;
import java.util.Set;

public class OidcScopes {
    public static final String OPENID_SCOPE = "openid";
    public static final String EMAIL_SCOPE = "email";
    public static final String PROFILE_SCOPE = "profile";

    // Represent the available OpenID scopes
    public static final Set<String> AVAILABLE_SCOPES = Collections.unmodifiableSet(
            Set.of("email", "profile")
    );

    public static boolean isOpenIDScope(String scope) {
        return AVAILABLE_SCOPES.contains(scope);
    }
}

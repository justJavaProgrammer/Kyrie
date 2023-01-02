package com.odeyalo.kyrie.core.oauth2.oidc;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.oauth2.Oauth2ScopeHandler;

import java.util.Map;

/**
 * Handle an OpenID scope named 'email'. Create two claims 'email' with end-user's preferred e-mail address and
 * 'email_verified' with true if the End-User's e-mail address has been verified; otherwise false
 * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims">Standard claims</a>
 */
public class EmailOidcOauth2ScopeHandler implements Oauth2ScopeHandler {
    public static final String EMAIL_SCOPE = "email";
    public static final String EMAIL_VERIFIED_SCOPE = "email_verified";

    @Override
    public Map<String, Object> createClaims(Oauth2User user) {
        Object email = user.getAdditionalInfo().get(EMAIL_SCOPE);
        if (email == null) {
            return Map.of(EMAIL_SCOPE, "null",
                    EMAIL_VERIFIED_SCOPE, false);
        }
        return Map.of(EMAIL_SCOPE, email,
                    EMAIL_VERIFIED_SCOPE, user.getAdditionalInfo().getOrDefault(EMAIL_VERIFIED_SCOPE, true));
    }

    @Override
    public String supportedScope() {
        return EMAIL_SCOPE;
    }
}

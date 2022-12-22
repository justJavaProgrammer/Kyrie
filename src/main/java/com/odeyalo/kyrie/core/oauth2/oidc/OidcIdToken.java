package com.odeyalo.kyrie.core.oauth2.oidc;

import com.odeyalo.kyrie.core.oauth2.AbstractOauth2Token;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * Represent the OpenID ID token
 *
 * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#IDToken">ID Token</a>
 * @see AbstractOauth2Token
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
public class OidcIdToken extends AbstractOauth2Token {
    // Claims to this ID token
    @NonNull
    @Singular
    private final Map<String, Object> claims;
}


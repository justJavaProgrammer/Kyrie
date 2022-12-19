package com.odeyalo.kyrie.core.authorization;

import com.odeyalo.kyrie.core.oauth2.oidc.OidcResponseType;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Enum to represent grant type for Oauth2.
 * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-1.3">Grant Type</a>
 * @version 1.0
 */
public enum AuthorizationGrantType {
    /**
     * Authorization code grant type.
     * Refer to <a href="https://www.rfc-editor.org/rfc/rfc6749#section-1.3.1">1.3.1.  Authorization Code</a>
     */
    AUTHORIZATION_CODE("authorization_code", Oauth2ResponseType.CODE),
    /**
     * Implicit grant type
     * Refer to @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-1.3.2">Implicit flow</a>
     */
    IMPLICIT("implicit", Oauth2ResponseType.TOKEN),

    /**
     * Represent custom multiple response type. Same as hybrid but supports Implicit flow too.
     */
    MULTIPLE("multiple", Oauth2ResponseType.CODE, Oauth2ResponseType.TOKEN, OidcResponseType.ID_TOKEN);


    private final String grantName;
    private final Oauth2ResponseType[] supportedResponseType;

    public static final Map<String,AuthorizationGrantType> ALL_TYPES =
            Map.copyOf(Arrays.stream(values())
                    .collect(Collectors.toMap(authorizationGrantType -> authorizationGrantType.grantName, st -> st)));

    AuthorizationGrantType(String grantName, Oauth2ResponseType... supportedResponseType) {
        this.grantName = grantName;
        this.supportedResponseType = supportedResponseType;
    }

    public String getGrantName() {
        return grantName;
    }

    public Oauth2ResponseType[] getSupportedResponseTypes() {
        return supportedResponseType;
    }
}

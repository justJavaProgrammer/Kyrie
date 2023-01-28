package com.odeyalo.kyrie.core.authorization;

import com.odeyalo.kyrie.core.oauth2.oidc.OidcResponseType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Enum to represent grant type for Oauth2.
 * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-1.3">Grant Type</a>
 * @version 2.0
 */
public class AuthorizationGrantType {
    /**
     * Authorization code grant type.
     * Refer to <a href="https://www.rfc-editor.org/rfc/rfc6749#section-1.3.1">1.3.1.  Authorization Code</a>
     */
    public static final AuthorizationGrantType AUTHORIZATION_CODE = new AuthorizationGrantType("authorization_code", Oauth2ResponseType.CODE);
    /**
     * Implicit grant type
     * Refer to <a href="https://www.rfc-editor.org/rfc/rfc6749#section-1.3.2">Implicit flow</a>
     */
    public static final AuthorizationGrantType IMPLICIT = new AuthorizationGrantType("implicit", Oauth2ResponseType.TOKEN);

    /**
     * Represent custom multiple response type. Same as hybrid but supports Implicit flow too.
     */
    public static final AuthorizationGrantType MULTIPLE = new AuthorizationGrantType("multiple", Oauth2ResponseType.CODE, Oauth2ResponseType.TOKEN, OidcResponseType.ID_TOKEN);

    /**
     * Represent Password grant type. Refer to <a href="https://www.rfc-editor.org/rfc/rfc6749#section-4.3">Password grant</a>
     */
    public static final AuthorizationGrantType PASSWORD = new AuthorizationGrantType("password", Oauth2ResponseType.TOKEN);

    private final String grantName;
    private final Oauth2ResponseType[] supportedResponseType;

    public static Map<String,AuthorizationGrantType> types;

    public AuthorizationGrantType(String grantName, Oauth2ResponseType... supportedResponseType) {
        this.grantName = grantName;
        this.supportedResponseType = supportedResponseType;
        addType(grantName, this);
    }

    public static AuthorizationGrantType fromSimplifiedName(String name) {
        return types.get(name);
    }

    public String getGrantName() {
        return grantName;
    }

    public Oauth2ResponseType[] getSupportedResponseTypes() {
        return supportedResponseType;
    }

    public static void addType(String grantName, AuthorizationGrantType grantType) {
        if (types == null) {
            types = new LinkedHashMap<>();
        }
        types.put(grantName, grantType);
    }
}

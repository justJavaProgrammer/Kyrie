package com.odeyalo.kyrie.core.oauth2.oidc.generator;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.oauth2.oidc.OidcIdToken;

import java.util.Map;

/**
 * Generator to generate an ID token from OpenID Connect.
 * @version 1.0
 * @see OidcIdToken
 * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#IDToken">ID Token</a>
 */
public interface OidcIdTokenGenerator {

    String AUTH_TIME = "auth_time";

    /**
     * Generate an ID token for the client with only default set of claims.
     * Set of default claims:
     * iss - Issuer Identifier for the Issuer of the response. The iss value is a case sensitive URL.
     * sub - Subject Identifier. In this case Oauth2User ID will be used.
     * aud - Audience(s) that this ID Token is intended for. It MUST contain the OAuth 2.0 client_id of the Relying Party as an audience value
     * exp - Expiration time on or after which the ID Token MUST NOT be accepted for processing. NumericDate time format MUST BE used
     * iat - Time at which the JWT was issued. NumericDate MUST BE used as time format
     * auth_time - Time when the End-User authentication occurred. NumericDate MUST BE used as time format
     * @param user - user that granted permission
     * @param clientId - client id that requested id token
     * @return - OidcIdToken with all required fields set
     */
    OidcIdToken generateOidcToken(String clientId, Oauth2User user);

    /**
     * Generate an ID token for the client with default set of claims, but also add additional claims to the token.
     * @param user - user that granted permission
     * @param additionalClaims - addtional claims that MUST BE added to the token.
     * @return - OidcIdToken with all required fields set
     */
    OidcIdToken generateOidcToken(String clientId, Oauth2User user, Map<String, Object> additionalClaims);
}

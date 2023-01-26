package com.odeyalo.kyrie.core.oauth2;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * Represent the refresh token from Oauth2 specification.
 * An OAuth Refresh Token is a string that the OAuth client can use to get a new access token without the user's interaction.
 * A refresh token must not allow the client to gain any access beyond the scope of the original grant.
 * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-1.5">Refresh Token</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class RefreshToken extends AbstractOauth2Token {
    /**
     * Scopes associated with this refresh token
     */
    protected String[] scopes;
    /**
     * True if the refresh token is active and can be used to obtain new access tokens, false otherwise
     */
    protected boolean active;
    /**
     * Client id associated with this refresh token. No other client id WON'T be able to obtain new access token using this refresh token.
     */
    protected String clientId;

    public RefreshToken(String tokenValue) {
        this.tokenValue = tokenValue;
    }
}

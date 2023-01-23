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
public class RefreshOauth2Token extends AbstractOauth2Token {

    public RefreshOauth2Token(String tokenValue) {
        this.tokenValue = tokenValue;
    }
}

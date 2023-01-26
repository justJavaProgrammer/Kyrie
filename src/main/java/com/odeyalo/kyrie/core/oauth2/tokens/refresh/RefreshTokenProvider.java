package com.odeyalo.kyrie.core.oauth2.tokens.refresh;

import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;
import com.odeyalo.kyrie.core.oauth2.RefreshToken;
import org.springframework.util.Assert;

/**
 * Provides the generic methods to working with refresh tokens provided by Kyrie Oauth2 Server.
 */
public interface RefreshTokenProvider {
    /**
     * Generate the refresh token for the specific Oauth2Client.
     *
     * The refresh token that was provided can be saved in some type of store or can be self-contained string with all data inside token
     *
     * @param credentials - client credentials with required non-null client id
     * @param scopes - specific scopes that this refresh token contains
     * @return - generated refresh token
     */
    RefreshToken generateToken(Oauth2ClientCredentials credentials, String[] scopes);

    /**
     * Used to get refresh token by refresh token value.
     *
     * For example, the token can be resolved through database store(if opaque token is used) or through token value parsing(if token is self-containing)
     *
     * @param tokenValue - token value that will be used to resolve RefreshToken
     * @return - RefreshToken resolved by the token value, null otherwise
     */
    RefreshToken getTokenByValue(String tokenValue);

    /**
     * <p>Deactivate the RefreshToken that was found by this token value.</p>
     *
     * <strong>NOTE: </strong> This method SHOULD NOT delete the refresh token, the refresh token must be only marked as non-active
     * @param tokenValue - token value to deactivate
     */
    void deactivateToken(String tokenValue);

    /**
     * Same as {@link #deactivateToken(String)}
     * @param token - token to deactivate
     */
    default void deactivateToken(RefreshToken token) {
        Assert.notNull(token, "The RefreshToken must be not null!");
        deactivateToken(token.getTokenValue());
    }

    /**
     * Remove the refresh token, if token was stored somewhere
     * @param tokenValue - token value to remove from store
     */
    void removeToken(String tokenValue);

    /**
     * Same as {@link #removeToken(String)}
     * @param refreshToken - refresh token to remove
     */
    default void removeToken(RefreshToken refreshToken) {
        Assert.notNull(refreshToken, "The RefreshToken must be not null!");
        removeToken(refreshToken.getTokenValue());
    }
}

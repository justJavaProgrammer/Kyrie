package com.odeyalo.kyrie.core.oauth2.tokens;

import com.odeyalo.kyrie.core.oauth2.AbstractOauth2Token;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * Represent access token defined in Oauth2 specification
 * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-1.4">Access Token</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
public class Oauth2AccessToken extends AbstractOauth2Token {
    // Token type(Bearer in most used cases)
    private TokenType tokenType;
    /**
     * String containing a space-separated list of scopes associated with this token, in the format described in Section 3.3 of OAuth 2.0 [RFC6749].
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-3.3">Section 3.3</a>
     */
    private String scope;

    protected Oauth2AccessToken(String tokenValue, Instant issuedAt, Instant expiresIn) {
        super(tokenValue, issuedAt, expiresIn);
    }


    public boolean isExpired() {
        return expiresIn == null || expiresIn.getEpochSecond() < System.currentTimeMillis() / 1000L;
    }

    public static class TokenType {
        public static final TokenType BEARER = new TokenType("Bearer");

        @Getter
        private final String value;

        public TokenType(String value) {
            this.value = value;
        }
    }
}

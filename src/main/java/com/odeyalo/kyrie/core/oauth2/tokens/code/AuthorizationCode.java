package com.odeyalo.kyrie.core.oauth2.tokens.code;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.oauth2.AbstractOauth2Token;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Represent authorization code from Oauth2 Protocol.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class AuthorizationCode extends AbstractOauth2Token {
    @NonNull
    private final String codeValue;
    @NonNull
    private final Oauth2User user;
    /**
     * Array of the scopes associated with this authorization code
     */
    @NonNull
    private final String[] scopes;


    @Builder
    public AuthorizationCode(@NonNull String codeValue, Instant issuedAt, @NonNull Instant expiresIn, @NonNull Oauth2User user, @NonNull String[] scopes) {
        this.codeValue = codeValue;
        this.issuedAt = issuedAt;
        this.expiresIn = expiresIn;
        this.user = user;
        this.scopes = scopes;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresIn);
    }

    @Override
    public String getTokenValue() {
        return codeValue;
    }

    public static class AuthorizationCodeBuilder {
        private Instant issuedAt;
        private @NonNull Instant expiresIn;

        public AuthorizationCodeBuilder issuedAt(LocalDateTime issuedAt) {
            return issuedAt(issuedAt.toInstant(ZoneOffset.UTC));
        }

        public AuthorizationCodeBuilder issuedAt(Instant issuedAt) {
            this.issuedAt = issuedAt;
            return this;
        }

        public AuthorizationCodeBuilder expiresIn(LocalDateTime expireTime) {
            return expiresIn(expireTime.toInstant(ZoneOffset.UTC));
        }

        public AuthorizationCodeBuilder expiresIn(Instant expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }
    }
}

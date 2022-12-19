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
@Builder
@Data
@AllArgsConstructor
public class AuthorizationCode extends AbstractOauth2Token {
    @NonNull
    private final String codeValue;
    @NonNull
    private final LocalDateTime expiresIn;
    @NonNull
    private final Oauth2User user;
    /**
     * Array of the scopes to this authorization code
     */
    @NonNull
    private final String[] scopes;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresIn);
    }

    @Override
    public String getTokenValue() {
        return codeValue;
    }

    @Override
    public Instant getExpiresIn() {
        return expiresIn.toInstant(ZoneOffset.UTC);
    }
}

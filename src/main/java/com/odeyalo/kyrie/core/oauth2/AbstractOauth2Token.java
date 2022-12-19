package com.odeyalo.kyrie.core.oauth2;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractOauth2Token implements Oauth2Token {
    // Contain the token value
    protected String tokenValue;
    protected Instant issuedAt;
    protected Instant expiresIn;

    @Override
    public String getTokenValue() {
        return tokenValue;
    }

    @Override
    public Instant getIssuedAt() {
        return issuedAt;
    }

    @Override
    public Instant getExpiresIn() {
        return expiresIn;
    }
}

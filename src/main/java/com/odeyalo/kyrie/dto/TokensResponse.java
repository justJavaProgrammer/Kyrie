package com.odeyalo.kyrie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * Represents successful access token response
 */
@Data
@Builder
public class TokensResponse {
    @JsonProperty("access_token")
    @NonNull
    private final String token;
    @NonNull
    private final String prefix;
    @JsonProperty("expires_in")
    @NonNull
    private final Long expiresIn;
    @NonNull
    private final String[] scopes;
}

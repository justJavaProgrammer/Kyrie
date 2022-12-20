package com.odeyalo.kyrie.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.odeyalo.kyrie.core.oauth2.tokens.TokenMetadata;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessToken;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represent access token response for /token endpoint
 */
@AllArgsConstructor
@Data
public class Oauth2AccessTokenResponse {
    private final boolean active;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String token;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String prefix;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Long expiresIn;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String scopes;

    private Oauth2AccessTokenResponse(boolean active) {
        this.active = active;
        this.token = null;
        this.prefix = null;
        this.expiresIn = null;
        this.scopes = null;
    }

    /**
     * Construct AccessTokenMetadataResponse from AccessTokenMetadata class
     *
     * @param token - Oauth2AccessToken to construct from
     * @return - AccessTokenMetadataResponse with copied field values from metadata
     * @see TokenMetadata
     */
    public static Oauth2AccessTokenResponse from(Oauth2AccessToken token) {
        if (token.isExpired()) {
            return new Oauth2AccessTokenResponse(false);
        }
        return new Oauth2AccessTokenResponse(true,
                token.getTokenValue(),
                token.getTokenType().getValue(),
                token.getExpiresIn().getEpochSecond(),
                token.getScope()
        );
    }
}

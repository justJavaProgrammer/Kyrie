package com.odeyalo.kyrie.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.kyrie.core.oauth2.support.Oauth2Constants;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessToken;
import com.odeyalo.kyrie.core.oauth2.tokens.TokenMetadata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represent access token response for /token endpoint
 */
@AllArgsConstructor
@Data
@Builder
public class Oauth2AccessTokenResponse {
    private final boolean active;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(Oauth2Constants.ACCESS_TOKEN)
    private final String token;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("token_type")
    private final String tokenType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("expires_in")
    private final Long expiresIn;
    @JsonProperty(Oauth2Constants.SCOPE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String scopes;
    @Singular
    private final Map<String, Object> additionalParameters;

    private Oauth2AccessTokenResponse(boolean active) {
        this.active = active;
        this.token = null;
        this.tokenType = null;
        this.expiresIn = null;
        this.scopes = null;
        this.additionalParameters = new HashMap<>();
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
                token.getScope(),
                Collections.emptyMap()
        );
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalParameters() {
        return additionalParameters;
    }
}

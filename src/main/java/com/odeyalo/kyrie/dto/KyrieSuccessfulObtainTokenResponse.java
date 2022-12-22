package com.odeyalo.kyrie.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * Represents successful access token response and add the additional properties such id token
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-4.1.4">Successful access token response</a>
 * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#TokenResponse">Successful Token Response</a> by OpenID specification
 */
@Data
@Builder
@AllArgsConstructor
public class KyrieSuccessfulObtainTokenResponse {
    // Token value
    @JsonProperty("access_token")
    @NonNull
    private final String token;
    // Prefix of the token
    @NonNull
    private final String prefix;
    // Seconds of the token lifetime
    @JsonProperty("expires_in")
    @NonNull
    private final Long expiresIn;
    // Scopes to the token. Space-separated list
    @JsonProperty("scope")
    @NonNull
    private final String scopes;
    @JsonProperty("id_token")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String idToken;

    @JsonCreator
    public KyrieSuccessfulObtainTokenResponse(@NonNull String token, @NonNull String prefix, @JsonProperty("expires_in") @NonNull Long expiresIn, @NonNull String scopes) {
        this.token = token;
        this.prefix = prefix;
        this.expiresIn = expiresIn;
        this.scopes = scopes;
    }
}

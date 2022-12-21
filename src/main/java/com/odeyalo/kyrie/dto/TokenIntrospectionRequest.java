package com.odeyalo.kyrie.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Represent the token info request for endpoint.
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7662#section-2.1">Introspection request</a>
 */
@Data
public class TokenIntrospectionRequest {
    @JsonProperty("access_token")
    @JsonAlias("refresh_token")
    private String token;
    @JsonProperty("token_type_hint")
    private String tokenTypeHint;
}

package com.odeyalo.kyrie.core.oauth2.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Set;

@AllArgsConstructor
@Builder
@Data
public class Oauth2Client {
    /**
     * Unique id for this client
     */
    private final String clientId;
    /**
     * Client secret for this client that will be used to verify client
     */
    private final String clientSecret;
    /**
     * Set of allowed redirect uris provided by client.
     * If request contains redirect uri that does not contains in allowedRedirectUris, then request must be rejected
     */
    @Singular("allowedRedirectUri")
    private final Set<String> allowedRedirectUris;

    public void addAllowedRedirectUri(String redirectUri) {
        this.allowedRedirectUris.add(redirectUri);
    }
}

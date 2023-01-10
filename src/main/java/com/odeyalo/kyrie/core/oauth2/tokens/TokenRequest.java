package com.odeyalo.kyrie.core.oauth2.tokens;

import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Represent the request from {@link com.odeyalo.kyrie.controllers.TokenController}.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenRequest {
    private String clientId;
    private AuthorizationGrantType grantType;
    private String[] scopes;
    // Contain the original, unmodified parameters from the original OAuth2 request
    private Map<String, String> requestParameters;
}

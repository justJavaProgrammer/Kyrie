package com.odeyalo.kyrie.core.authorization;

import lombok.*;

import java.io.Serializable;

/**
 * Data class to store an authorization request
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AuthorizationRequest implements Serializable {
    private static final long serialVersionUID = 42L;
    private String clientId;
    private String redirectUrl;
    private String[] scopes;
    private Oauth2ResponseType[] responseTypes;
    private AuthorizationGrantType grantType;
    private String state;

    /**
     * Customize lombok builder to make building easier for arrays
     */
    public static class AuthorizationRequestBuilder {
        private Oauth2ResponseType[] responseTypes;

        public AuthorizationRequestBuilder responseTypes(Oauth2ResponseType... types) {
            this.responseTypes = types;
            return this;
        }
    }
}

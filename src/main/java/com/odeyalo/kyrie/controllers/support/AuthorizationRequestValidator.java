package com.odeyalo.kyrie.controllers.support;

import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.support.Oauth2ValidationResult;

/**
 * Validate an authorization request
 */
public interface AuthorizationRequestValidator {

    default Oauth2ValidationResult validateAuthorizationRequest(String clientId, Oauth2ResponseType[] responseTypes, String[] scopes, AuthorizationGrantType grantType, String redirectUrl, String state) {
        AuthorizationRequest request = AuthorizationRequest.builder()
                .clientId(clientId)
                .responseTypes(responseTypes)
                .grantType(grantType)
                .redirectUrl(redirectUrl)
                .scopes(scopes)
                .state(state)
                .build();
        return validateAuthorizationRequest(request);
    }

    Oauth2ValidationResult validateAuthorizationRequest(AuthorizationRequest request);
}

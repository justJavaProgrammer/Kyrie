package com.odeyalo.kyrie.controllers.support.validation;

import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.support.Oauth2ValidationResult;
import com.odeyalo.kyrie.exceptions.Oauth2ErrorType;
import org.springframework.stereotype.Component;

/**
 * AuthorizationRequestValidationStep to check client id from AuthorizationRequest
 *
 * @see AuthorizationRequest
 */
@Component
public class ClientIdAuthorizationRequestValidationStep implements AuthorizationRequestValidationStep {

    // Todo: Implement the logic
    @Override
    public Oauth2ValidationResult validate(AuthorizationRequest request) {
        String clientId = request.getClientId();
        return clientId.equals("client") ? Oauth2ValidationResult.success() : Oauth2ValidationResult.failed(Oauth2ErrorType.INVALID_CLIENT, "The client id does not exist");
    }
}

package com.odeyalo.kyrie.controllers.support.validation;

import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.oauth2.client.Oauth2Client;
import com.odeyalo.kyrie.core.oauth2.client.Oauth2ClientRepository;
import com.odeyalo.kyrie.core.support.Oauth2ValidationResult;
import com.odeyalo.kyrie.exceptions.Oauth2ErrorType;

/**
 * AuthorizationRequestValidationStep to check client id from AuthorizationRequest
 *
 * @see AuthorizationRequest
 */
public class ClientIdAuthorizationRequestValidationStep implements AuthorizationRequestValidationStep {
    private final Oauth2ClientRepository clientRepository;

    public ClientIdAuthorizationRequestValidationStep(Oauth2ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Oauth2ValidationResult validate(AuthorizationRequest request) {
        String clientId = request.getClientId();
        Oauth2Client client = clientRepository.findOauth2ClientById(clientId);
        return client != null ?
                Oauth2ValidationResult.success() :
                Oauth2ValidationResult.failed(Oauth2ErrorType.INVALID_CLIENT, "The client id does not exist");
    }
}

package com.odeyalo.kyrie.controllers.support.validation;

import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.oauth2.client.Oauth2Client;
import com.odeyalo.kyrie.core.oauth2.client.Oauth2ClientRepository;
import com.odeyalo.kyrie.core.support.Oauth2ValidationResult;
import com.odeyalo.kyrie.exceptions.Oauth2ErrorType;
import org.springframework.core.Ordered;

import java.util.Set;

/**
 * Simple {@link AuthorizationRequestValidationStep} implementation that checks the redirect uri is registered in allowed redirect uris by {@link com.odeyalo.kyrie.core.oauth2.client.Oauth2Client}
 *
 * If redirect uri is not presented, then the request will be rejected
 */
public class RegisteredRedirectUriAuthorizationRequestValidationStep implements AuthorizationRequestValidationStep, Ordered {
    private final Oauth2ClientRepository clientRepository;

    public RegisteredRedirectUriAuthorizationRequestValidationStep(Oauth2ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Oauth2ValidationResult validate(AuthorizationRequest request) {
        Oauth2Client client = clientRepository.findOauth2ClientById(request.getClientId());
        if (client == null) {
            return Oauth2ValidationResult.failed(Oauth2ErrorType.INVALID_CLIENT, "The client is null");
        }
        Set<String> allowedRedirectUris = client.getAllowedRedirectUris();
        String redirectUrl = request.getRedirectUrl();

        if (!allowedRedirectUris.contains(redirectUrl)) {
            return Oauth2ValidationResult.failed(Oauth2ErrorType.INVALID_REDIRECT_URI,
                    "The redirect uri does not registered as trusted");
        }

        return Oauth2ValidationResult.success();
    }
    // Make the RegisteredRedirectUriAuthorizationRequestValidationStep last as possible to do checks without database interactions first
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 10;
    }
}

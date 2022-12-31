package com.odeyalo.kyrie.core.oauth2.client;

import com.odeyalo.kyrie.core.support.ValidationResult;
import org.springframework.stereotype.Service;

/**
 * Default client credentials validator that checks client id and client secret and return success only when everything is correct
 * @version 1.0
 */
@Service
public class DefaultClientCredentialsValidator implements ClientCredentialsValidator {
    private final Oauth2ClientRepository oauth2ClientRepository;

    public DefaultClientCredentialsValidator(Oauth2ClientRepository oauth2ClientRepository) {
        this.oauth2ClientRepository = oauth2ClientRepository;
    }

    @Override
    public ValidationResult validateCredentials(String clientId, String clientSecret) {
        Oauth2Client client = oauth2ClientRepository.findOauth2ClientById(clientId);
        if (client == null) {
            return ValidationResult.failed("Client id or client secret is invalid or incorrect");
        }
        boolean validationResult = client.getClientId().equals(clientId) && client.getClientSecret().equals(clientSecret);

        return validationResult ? ValidationResult.success() : ValidationResult.failed("Client id or client secret is not valid or is wrong");
    }
}

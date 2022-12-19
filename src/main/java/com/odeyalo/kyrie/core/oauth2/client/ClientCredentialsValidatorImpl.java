package com.odeyalo.kyrie.core.oauth2.client;

import org.springframework.stereotype.Service;

/**
 * Default client credentials validator
 * @version 1.0
 */
@Service
public class ClientCredentialsValidatorImpl implements ClientCredentialsValidator {

    @Override
    public ValidationResult validateCredentials(String clientId, String clientSecret) {
        boolean validation = "client".equals(clientId) && "secret".equals(clientSecret);
        return new ValidationResult(validation);
    }
}

package com.odeyalo.kyrie.core.oauth2.client;

import com.odeyalo.kyrie.core.support.ValidationResult;
import org.springframework.stereotype.Service;

/**
 * Default client credentials validator that checks client id and client secret and return success only when everything is correct
 * @version 1.0
 */
@Service
public class DefaultClientCredentialsValidator implements ClientCredentialsValidator {

    @Override
    public ValidationResult validateCredentials(String clientId, String clientSecret) {
        boolean validation = "client".equals(clientId) && "secret".equals(clientSecret);
        return validation ? ValidationResult.success() : ValidationResult.failed("Client id or client secret is not valid or is wrong");
    }
}

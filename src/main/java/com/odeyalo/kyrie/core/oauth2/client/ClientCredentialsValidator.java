package com.odeyalo.kyrie.core.oauth2.client;

import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;
import com.odeyalo.kyrie.core.support.ValidationResult;

/**
 * Interface to validate provided client's credentials
 */
public interface ClientCredentialsValidator {

    /**
     * Validate client credentials
     * @param clientId - client id
     * @param clientSecret - client secret
     * @return - ValidationResult
     */
    ValidationResult validateCredentials(String clientId, String clientSecret);

    default ValidationResult validateCredentials(Oauth2ClientCredentials credentials) {
        return validateCredentials(credentials.getClientId(), credentials.getClientSecret());
    }
}

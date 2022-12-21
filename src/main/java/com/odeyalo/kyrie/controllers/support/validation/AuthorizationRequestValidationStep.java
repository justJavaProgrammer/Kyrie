package com.odeyalo.kyrie.controllers.support.validation;

import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.support.Oauth2ValidationResult;
import com.odeyalo.kyrie.core.support.ValidationResult;
import com.odeyalo.kyrie.core.support.ValidationStep;

/**
 * Represent ValidationStep only for AuthorizationRequest class.
 * @see ValidationResult
 * @see ValidationStep
 * @see AuthorizationRequest
 */
public interface AuthorizationRequestValidationStep extends ValidationStep<AuthorizationRequest> {
    /**
     * Validate the AuthorizationRequest and return Oauth2ValidationResult
     * @param request - request to validate
     * @return - Oauth2ValidationResult
     */
    @Override
    Oauth2ValidationResult validate(AuthorizationRequest request);

}

package com.odeyalo.kyrie.controllers.support;

import com.odeyalo.kyrie.controllers.support.validation.AuthorizationRequestValidationStep;
import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.oauth2.oidc.OidcScopes;
import com.odeyalo.kyrie.core.support.Oauth2ValidationResult;
import com.odeyalo.kyrie.exceptions.Oauth2ErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * AuthorizationRequestValidator implementation that uses chain of AuthorizationRequestValidationStep to validate an AuthorizationRequest.
 * <p>It automatically detects and registers AuthorizationRequestValidationStep in container and uses it every time to validate an AuthorizationRequest</p>
 * @see AuthorizationRequestValidationStep
 * @see AuthorizationRequestValidator
 * @see AuthorizationRequest
 * @version 1.0
 */
@Component
public class DefaultChainAuthorizationRequestValidator implements AuthorizationRequestValidator {
    private final List<AuthorizationRequestValidationStep> steps;
    private final Logger logger = LoggerFactory.getLogger(DefaultChainAuthorizationRequestValidator.class);

    @Autowired
    public DefaultChainAuthorizationRequestValidator(List<AuthorizationRequestValidationStep> steps) {
        this.steps = steps;
    }

    @Override
    public Oauth2ValidationResult validateAuthorizationRequest(AuthorizationRequest request) {
        Set<Oauth2ResponseType> types = Set.of(request.getResponseTypes());
        Set<String> scopes = Set.of(request.getScopes());
        AuthorizationGrantType grantType = request.getGrantType();
        if (!scopes.contains(OidcScopes.OPENID_SCOPE) && types.size() > 1) {
            return Oauth2ValidationResult.failed(Oauth2ErrorType.INVALID_REQUEST, "If " + OidcScopes.OPENID_SCOPE + " scope is not presented in scopes, then only one response type should be used.");
        }
        if (types.size() > 1 && grantType != AuthorizationGrantType.MULTIPLE) {
            return Oauth2ValidationResult.failed(Oauth2ErrorType.INVALID_REQUEST, "If response types more than 2, then Multiple flow should be used");
        }
        for (AuthorizationRequestValidationStep step : steps) {
            logger.debug("Validating the request using {} step", step.getClass().getName());
            Oauth2ValidationResult result = step.validate(request);
            logger.debug("Result check is: {}", result);
            if (!result.isSuccess()) {
                return result;
            }
        }
        return Oauth2ValidationResult.success();
    }

    /**
     * Add additional AuthorizationRequestValidationStep to chain.
     * @param validationStep - validation step to add in chain
     */
    public void addValidationStep(AuthorizationRequestValidationStep validationStep) {
        this.steps.add(validationStep);
    }

    /**
     * Remove the AuthorizationRequestValidationStep from chain.
     * @param step - step to remove
     */
    public void removeValidationStep(Class<? extends AuthorizationRequestValidationStep> step) {
        this.steps.removeIf(x -> x.getClass().equals(step));
    }
}

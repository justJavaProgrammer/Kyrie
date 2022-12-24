package com.odeyalo.kyrie.controllers.support.validation;

import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.support.Oauth2ValidationResult;
import com.odeyalo.kyrie.exceptions.Oauth2ErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Check only 'redirect_uri' parameter from oauth2 request.
 */
@Component
public class RedirectUriAuthorizationRequestValidationStep implements AuthorizationRequestValidationStep {
    private final Logger logger = LoggerFactory.getLogger(RedirectUriAuthorizationRequestValidationStep.class);

    /**
     * Validate redirect_uri parameter from request. If check is failed then INVALID_REDIRECT_URI will be returned with description
     * @param request - request to validate
     * @return
     */
    @Override
    public Oauth2ValidationResult validate(AuthorizationRequest request) {
        String redirectUrl = request.getRedirectUrl();
        logger.debug("Testing redirect uri with value: {}", redirectUrl);
        try {
            new URL(redirectUrl);
            logger.debug("Testing redirect uri with value: {} is success", redirectUrl);
            return Oauth2ValidationResult.success();
        } catch (MalformedURLException e) {
            logger.debug("Testing redirect uri with value: {} was failed", redirectUrl);
            return Oauth2ValidationResult.failed(Oauth2ErrorType.INVALID_REDIRECT_URI, "The redirect_uri parameter is not valid and can't processed");
        }
    }
}

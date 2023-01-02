package com.odeyalo.kyrie.config.configuration;

import com.odeyalo.kyrie.controllers.support.AuthorizationRequestValidator;
import com.odeyalo.kyrie.controllers.support.DefaultChainAuthorizationRequestValidator;
import com.odeyalo.kyrie.controllers.support.validation.AuthorizationRequestValidationStep;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

public class KyrieOauth2RequestValidationConfiguration {

    /**
     * Registry AuthorizationRequestValidator only if bean is missing.
     * @param steps - validation steps to validate an {@link com.odeyalo.kyrie.core.authorization.AuthorizationRequest}
     * @return - DefaultChainAuthorizationRequestValidator
     *
     * @see AuthorizationRequestValidationStep
     * @see DefaultChainAuthorizationRequestValidator
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthorizationRequestValidator authorizationRequestValidator(List<AuthorizationRequestValidationStep> steps) {
        return new DefaultChainAuthorizationRequestValidator(steps);
    }

}

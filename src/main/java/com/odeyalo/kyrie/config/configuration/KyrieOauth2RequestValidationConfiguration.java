package com.odeyalo.kyrie.config.configuration;

import com.odeyalo.kyrie.config.Oauth2ClientValidationFilter;
import com.odeyalo.kyrie.controllers.support.AuthorizationRequestValidator;
import com.odeyalo.kyrie.controllers.support.DefaultChainAuthorizationRequestValidator;
import com.odeyalo.kyrie.controllers.support.validation.AuthorizationRequestValidationStep;
import com.odeyalo.kyrie.controllers.support.validation.ClientIdAuthorizationRequestValidationStep;
import com.odeyalo.kyrie.controllers.support.validation.RedirectUriAuthorizationRequestValidationStep;
import com.odeyalo.kyrie.core.oauth2.client.ClientCredentialsValidator;
import com.odeyalo.kyrie.core.oauth2.client.Oauth2ClientRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

public class KyrieOauth2RequestValidationConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public Oauth2ClientValidationFilter oauth2ClientValidationFilter(ClientCredentialsValidator validator, Oauth2ClientRepository oauth2ClientRepository) {
        return new Oauth2ClientValidationFilter(validator, oauth2ClientRepository);
    }

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
    @Bean
    @ConditionalOnMissingBean
    public ClientIdAuthorizationRequestValidationStep clientIdAuthorizationRequestValidationStep(Oauth2ClientRepository oauth2ClientRepository) {
        return new ClientIdAuthorizationRequestValidationStep(oauth2ClientRepository);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedirectUriAuthorizationRequestValidationStep redirectUriAuthorizationRequestValidationStep() {
        return new RedirectUriAuthorizationRequestValidationStep();
    }
}

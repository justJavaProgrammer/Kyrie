package com.odeyalo.kyrie.config.configuration;

import com.odeyalo.kyrie.core.oauth2.support.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * Create beans that will be used to generate redirect_uri based on specific Oauth2 Flow.
 *
 * @see RedirectUrlCreationService
 * @version 1.0
 */
public class RedirectUriCreationServicesConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AuthorizationCodeFlowRedirectUrlCreationService authorizationCodeFlowRedirectUrlCreationService() {
        return new AuthorizationCodeFlowRedirectUrlCreationService();
    }

    @Bean
    @ConditionalOnMissingBean
    public RedirectUrlCreationServiceFactory defaultRedirectUrlCreationServiceFactory(List<RedirectUrlCreationService> redirectUrlCreationServices) {
        return new DefaultRedirectUrlCreationServiceFactory(redirectUrlCreationServices);
    }

    @Bean
    @ConditionalOnMissingBean
    public ImplicitFlowRedirectUrlCreationService implicitFlowRedirectUrlCreationService() {
        return new ImplicitFlowRedirectUrlCreationService();
    }

    @Bean
    @ConditionalOnMissingBean
    public MultipleResponseTypeFlowRedirectUrlCreationService multipleResponseTypeFlowRedirectUrlCreationService() {
        return new MultipleResponseTypeFlowRedirectUrlCreationService();
    }
}

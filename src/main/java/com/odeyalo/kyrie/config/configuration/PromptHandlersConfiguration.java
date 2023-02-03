package com.odeyalo.kyrie.config.configuration;

import com.odeyalo.kyrie.config.configurers.Oauth2ServerEndpointsConfigurer;
import com.odeyalo.kyrie.core.oauth2.flow.support.RedirectableOauth2FlowHandlerFacade;
import com.odeyalo.kyrie.core.oauth2.prompt.*;
import com.odeyalo.kyrie.core.oauth2.support.consent.ConsentPageHandler;
import com.odeyalo.kyrie.core.oauth2.support.consent.DefaultKyrieHtmlFormConsentPageHandler;
import com.odeyalo.kyrie.core.sso.RememberMeService;
import com.odeyalo.kyrie.core.support.web.TemporaryRequestAttributesRepository;
import com.odeyalo.kyrie.support.html.TemplateResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import java.util.List;

public class PromptHandlersConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ConsentPromptHandler consentPromptHandler(TemplateResolver templateResolver, RememberMeService rememberMeService) {
        return new ConsentPromptHandler(templateResolver, rememberMeService);
    }

    @Bean
    @ConditionalOnMissingBean
    public CombinedPromptHandler combinedPromptHandler(RememberMeService rememberMeService, @Lazy PromptHandlerFactory factory) {
        return new CombinedPromptHandler(rememberMeService, factory);
    }

    @Bean
    @ConditionalOnMissingBean
    public LoginPromptHandler loginPromptHandler(TemplateResolver templateResolver) {
        return new LoginPromptHandler(templateResolver);
    }

    @Bean
    @ConditionalOnMissingBean
    public NonePromptHandler nonePromptHandler(RememberMeService rememberMeService, Oauth2ServerEndpointsConfigurer.Oauth2ServerEndpointsInfo endpointsInfo) {
        return new NonePromptHandler(rememberMeService, endpointsInfo);
    }

    @Bean
    @ConditionalOnMissingBean
    public PromptHandlerFactory promptHandlerFactory(List<PromptHandler> handlers) {
        return new SimplePromptHandlerFactory(handlers);
    }

    @Bean
    @ConditionalOnMissingBean
    public ConsentPageHandler consentPageHandler(RedirectableOauth2FlowHandlerFacade redirectableOauth2FlowHandlerFacade,
                                                 TemporaryRequestAttributesRepository temporaryRequestAttributesRepository,
                                                 TemplateResolver templateResolver) {
        return new DefaultKyrieHtmlFormConsentPageHandler(redirectableOauth2FlowHandlerFacade, temporaryRequestAttributesRepository, templateResolver);
    }
}

package com.odeyalo.kyrie.config.configuration;

import com.odeyalo.kyrie.core.authentication.EventPublisherOauth2UserAuthenticationServiceDecorator;
import com.odeyalo.kyrie.core.authentication.Oauth2UserAuthenticationService;
import com.odeyalo.kyrie.core.events.KyrieEventPublisher;
import com.odeyalo.kyrie.core.oauth2.tokens.AccessTokenGranterStrategyFactory;
import com.odeyalo.kyrie.core.oauth2.tokens.AuthorizationCodeExchangeAccessTokenGranterStrategy;
import com.odeyalo.kyrie.core.oauth2.tokens.AuthorizationCodeFlowAccessTokenReturner;
import com.odeyalo.kyrie.core.oauth2.tokens.PasswordFlowAccessTokenGranterStrategy;
import com.odeyalo.kyrie.core.oauth2.tokens.customizer.Oauth2TokenCustomizerProcessorRegistry;
import com.odeyalo.kyrie.core.oauth2.tokens.facade.AccessTokenGranterStrategyFacadeWrapper;
import com.odeyalo.kyrie.core.oauth2.tokens.facade.SimpleAccessTokenGranterStrategyFacadeWrapper;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.Oauth2AccessTokenGenerator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

@AutoConfigureAfter(GenericKyrieOauth2Configuration.class)
public class AccessTokenGrantersConfiguration {

    @Bean
    public AccessTokenGranterStrategyFacadeWrapper accessTokenGranterStrategyFacadeWrapper(AccessTokenGranterStrategyFactory factory, Oauth2TokenCustomizerProcessorRegistry registry) {
        return new SimpleAccessTokenGranterStrategyFacadeWrapper(factory, registry);
    }
//todo rewrite this bean with BPP
    @Bean
    @Primary
    @DependsOn("oauth2UserAuthenticationService")
    public EventPublisherOauth2UserAuthenticationServiceDecorator eventPublisherOauth2UserAuthenticationServiceDecorator(Oauth2UserAuthenticationService authenticationService,
                                                                                                                         KyrieEventPublisher publisher) {
        return new EventPublisherOauth2UserAuthenticationServiceDecorator(authenticationService, publisher);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthorizationCodeExchangeAccessTokenGranterStrategy authorizationCodeExchangeAccessTokenGranterStrategy(AuthorizationCodeFlowAccessTokenReturner authorizationCodeFlowAccessTokenReturner) {
        return new AuthorizationCodeExchangeAccessTokenGranterStrategy(authorizationCodeFlowAccessTokenReturner);
    }

    @Bean
    @ConditionalOnMissingBean
    public PasswordFlowAccessTokenGranterStrategy passwordFlowAccessTokenGranterStrategy(Oauth2AccessTokenGenerator generator,
                                                                                         Oauth2UserAuthenticationService authenticationService) {
        return new PasswordFlowAccessTokenGranterStrategy(generator, authenticationService);
    }
}

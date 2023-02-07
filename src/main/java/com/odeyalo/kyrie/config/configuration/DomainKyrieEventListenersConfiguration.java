package com.odeyalo.kyrie.config.configuration;

import com.odeyalo.kyrie.core.events.listener.domain.ConditionalRememberMeUserLoginAuthenticationGrantedKyrieEventListener;
import com.odeyalo.kyrie.core.events.listener.domain.Oauth2UserStoreUserLoginAuthenticationGrantedKyrieEventListener;
import com.odeyalo.kyrie.core.events.listener.domain.RequestAttributesClearAuthorizationRequestProcessingFinishedKyrieEventListener;
import com.odeyalo.kyrie.core.sso.RememberMeService;
import com.odeyalo.kyrie.core.support.condition.PropertyConditionalRememberUserCondition;
import com.odeyalo.kyrie.core.support.condition.RememberUserCondition;
import com.odeyalo.kyrie.core.support.web.TemporaryRequestAttributesRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

public class DomainKyrieEventListenersConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RememberUserCondition rememberUserCondition() {
        return new PropertyConditionalRememberUserCondition();
    }

    @Bean
    @ConditionalOnMissingBean
    public ConditionalRememberMeUserLoginAuthenticationGrantedKyrieEventListener conditionalRememberMeUserLoginAuthenticationGrantedKyrieEventListener(
            RememberUserCondition rememberUserCondition,
            RememberMeService rememberMeService
    ) {
        return new ConditionalRememberMeUserLoginAuthenticationGrantedKyrieEventListener(rememberUserCondition, rememberMeService);
    }

    @Bean
    @ConditionalOnMissingBean
    public Oauth2UserStoreUserLoginAuthenticationGrantedKyrieEventListener oauth2UserStoreUserLoginAuthenticationGrantedKyrieEventListener(TemporaryRequestAttributesRepository temporaryRequestAttributesRepository) {
        return new Oauth2UserStoreUserLoginAuthenticationGrantedKyrieEventListener(temporaryRequestAttributesRepository);
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestAttributesClearAuthorizationRequestProcessingFinishedKyrieEventListener requestProcessingFinishedKyrieEventListener(TemporaryRequestAttributesRepository repository) {
        return new RequestAttributesClearAuthorizationRequestProcessingFinishedKyrieEventListener(repository);
    }
}

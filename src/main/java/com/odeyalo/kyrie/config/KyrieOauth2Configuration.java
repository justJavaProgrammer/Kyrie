package com.odeyalo.kyrie.config;

import com.odeyalo.kyrie.core.oauth2.client.ClientCredentialsValidator;
import com.odeyalo.kyrie.core.oauth2.client.DefaultClientCredentialsValidator;
import com.odeyalo.kyrie.core.oauth2.client.Oauth2ClientRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KyrieOauth2Configuration {

    @Bean
    @ConditionalOnClass(name = "org.springframework.security.web.DefaultSecurityFilterChain")
    public FilterRegistrationBean<Oauth2ClientValidationFilter> oauth2ClientValidationFilterFilterRegistrationBean(Oauth2ClientValidationFilter oauth2ClientValidationFilter) {
        FilterRegistrationBean<Oauth2ClientValidationFilter> filterRegistration = new FilterRegistrationBean<>();
        filterRegistration.addUrlPatterns("/oauth2/token");
        filterRegistration.setFilter(oauth2ClientValidationFilter);
        return filterRegistration;
    }

    @Bean
    public Oauth2ClientValidationFilter oauth2ClientValidationFilter(ClientCredentialsValidator validator) {
        return new Oauth2ClientValidationFilter(validator);
    }

    @Bean
    @ConditionalOnMissingBean
    public ClientCredentialsValidator clientCredentialsValidator(Oauth2ClientRepository repository) {
        return new DefaultClientCredentialsValidator(repository);
    }
}

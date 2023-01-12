package com.odeyalo.kyrie.config.configuration;

import com.odeyalo.kyrie.config.KyrieOauth2Configurer;
import com.odeyalo.kyrie.config.KyrieOauth2ConfigurerComposite;
import com.odeyalo.kyrie.config.Oauth2ClientValidationFilter;
import com.odeyalo.kyrie.config.configurers.Oauth2ServerEndpointsConfigurer;
import com.odeyalo.kyrie.config.support.UnauthorizedOauth2ClientAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Configuration class that used to configure HTTP security for Kyrie Oauth2 Server.
 * The configuration registry {@link DefaultSecurityFilterChain} bean with custom filters and custom {@link RequestMatcher}.
 * It also can be configured through {@link KyrieOauth2Configurer}
 *
 * @see KyrieOauth2Configurer
 * @see Oauth2ClientValidationFilter
 * @see DefaultSecurityFilterChain
 */
@AutoConfigureAfter(value = KyrieOauth2ServerEndpointsMappingConfiguration.class)
public class KyrieOauth2ServerWebSecurityConfiguration {
    private final Oauth2ClientValidationFilter filter;
    private final KyrieOauth2ConfigurerComposite configurer = new KyrieOauth2ConfigurerComposite();

    private Oauth2ServerEndpointsConfigurer.Oauth2ServerEndpointsInfo info;

    @Autowired(required = false)
    public void setConfigurers(List<KyrieOauth2Configurer> configurers) {
        this.configurer.addAll(configurers);
    }

    @PostConstruct
    public void configurerInitialize() {
        Oauth2ServerEndpointsConfigurer configurer = new Oauth2ServerEndpointsConfigurer();
        this.configurer.configureEndpoints(configurer);
        this.info = configurer.buildOauth2ServerEndpointsInfo();
    }

    public KyrieOauth2ServerWebSecurityConfiguration(Oauth2ClientValidationFilter filter) {
        this.filter = filter;
    }

    /**
     * Create DefaultSecurityFilterChain bean that will be used when {@link KyrieOauth2RequestMatcher#matches(HttpServletRequest)} returns true.
     *
     * The bean has 0 order to make it first in {@link org.springframework.security.web.FilterChainProxy}.
     *
     * @param security - http security that was configured by Spring Security
     * @return - DefaultSecurityFilterChain that will be registered in {@link org.springframework.security.web.FilterChainProxy}
     * @throws Exception - if any exception was occurred during bean configuration
     *
     * @see org.springframework.security.web.FilterChainProxy
     * @see DefaultSecurityFilterChain
     * @see KyrieOauth2RequestMatcher
     */
    @Bean
    @Order(0)
    public DefaultSecurityFilterChain kyrieAuthorizationServerSecurityFilterChain(HttpSecurity security, UnauthorizedOauth2ClientAuthenticationEntryPoint authenticationEntryPoint) throws Exception {
        security.requestMatcher(new KyrieOauth2RequestMatcher());
        return security.csrf().disable()
                .authorizeRequests()
                .antMatchers(info.getTokenEndpointName())
                .authenticated()
                .requestMatchers(new KyrieOauth2RequestMatcher())
                .permitAll()
                .and()
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .build();

    }

    @Bean
    public UnauthorizedOauth2ClientAuthenticationEntryPoint unauthorizedOauth2ClientAuthenticationEntryPoint() {
        return new UnauthorizedOauth2ClientAuthenticationEntryPoint();
    }

    /**
     * Simple RequestMatcher that matches only endpoints that were provided by {@link Oauth2ServerEndpointsConfigurer.Oauth2ServerEndpointsInfo}
     */
    private class KyrieOauth2RequestMatcher implements RequestMatcher {

        @Override
        public boolean matches(HttpServletRequest request) {
            String requestURI = request.getRequestURI();
            return requestURI.startsWith(info.getPrefix()) || requestURI.startsWith(info.getTokenEndpointName());
        }
    }
}

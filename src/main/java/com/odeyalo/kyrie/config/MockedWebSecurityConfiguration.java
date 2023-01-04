package com.odeyalo.kyrie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

public class MockedWebSecurityConfiguration {
    private final Oauth2ClientValidationFilter filter;

    public MockedWebSecurityConfiguration(Oauth2ClientValidationFilter filter) {
        this.filter = filter;
    }

    //todo
    @Bean
    @Order(0)
    public DefaultSecurityFilterChain kyrieAuthorizationServerSecurityFilterChain(HttpSecurity security) throws Exception {
        security.requestMatcher(new KyrieOauth2RequestMatcher());
        DefaultSecurityFilterChain build = security.csrf().disable()
                .authorizeRequests()
                .antMatchers("/token")
                .authenticated()
                .requestMatchers(new KyrieOauth2RequestMatcher())
                .permitAll()
                .and()
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
        return build;

    }

    private static class KyrieOauth2RequestMatcher implements RequestMatcher {

        @Override
        public boolean matches(HttpServletRequest request) {
            String requestURI = request.getRequestURI();
            return requestURI.startsWith("/oauth2") || requestURI.startsWith("/token");
        }
    }
}

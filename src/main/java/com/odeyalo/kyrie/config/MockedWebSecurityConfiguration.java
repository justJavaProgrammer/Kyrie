package com.odeyalo.kyrie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class MockedWebSecurityConfiguration {
private final Oauth2ClientValidationFilter filter;

    public MockedWebSecurityConfiguration(Oauth2ClientValidationFilter filter) {
        this.filter = filter;
    }
//todo
    @Bean
    public DefaultSecurityFilterChain defaultSecurityFilterChain(HttpSecurity security) throws Exception {
        return security.csrf().disable()
                .authorizeRequests()
                .antMatchers("/token")
                .authenticated()
                .anyRequest().permitAll()
                .and()
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class).build();

    }
}

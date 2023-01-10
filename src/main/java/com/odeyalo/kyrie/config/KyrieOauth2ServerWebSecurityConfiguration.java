package com.odeyalo.kyrie.config;

import com.odeyalo.kyrie.config.configuration.KyrieOauth2ServerEndpointsMappingConfiguration;
import com.odeyalo.kyrie.config.configurers.Oauth2ServerEndpointsConfigurer;
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

    @Bean
    @Order(0)
    public DefaultSecurityFilterChain kyrieAuthorizationServerSecurityFilterChain(HttpSecurity security) throws Exception {
        security.requestMatcher(new KyrieOauth2RequestMatcher());
        return security.csrf().disable()
                .authorizeRequests()
                .antMatchers(info.getTokenEndpointName())
                .authenticated()
                .requestMatchers(new KyrieOauth2RequestMatcher())
                .permitAll()
                .and()
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }

    private class KyrieOauth2RequestMatcher implements RequestMatcher {

        @Override
        public boolean matches(HttpServletRequest request) {
            String requestURI = request.getRequestURI();
            return requestURI.startsWith(info.getPrefix()) || requestURI.startsWith(info.getTokenEndpointName());
        }
    }
}

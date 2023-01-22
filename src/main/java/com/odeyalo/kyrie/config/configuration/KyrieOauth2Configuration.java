package com.odeyalo.kyrie.config.configuration;

import com.odeyalo.kyrie.config.Oauth2ClientCredentialsResolver;
import com.odeyalo.kyrie.core.events.DefaultSpringKyrieEventMulticaster;
import com.odeyalo.kyrie.core.events.KyrieEventPublisher;
import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;
import com.odeyalo.kyrie.support.ClientId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * Configuration class that uses to import the common beans and uses only if
 * {@link com.odeyalo.kyrie.config.annotation.EnableKyrieAuthorizationServer} annotation is presented on some Spring bean
 * The configuration beans can be easily  overridden by user's custom configs.
 * </p>
 *
 * @version 1.0
 */
@Import(value = {
        GenericKyrieOauth2Configuration.class,
        Oauth2FlowHandlersConfiguration.class,
        RedirectUriCreationServicesConfiguration.class,
        KyrieOauth2RequestValidationConfiguration.class,
        KyrieOauth2ServerEndpointsMappingConfiguration.class,
        AccessTokenGrantersConfiguration.class,
        CustomizersConfiguration.class,
        KyrieOauth2ServerWebSecurityConfiguration.class
})
public class KyrieOauth2Configuration {
    private final Logger logger = LoggerFactory.getLogger(KyrieOauth2Configuration.class);
    private final Oauth2ClientCredentialsResolver resolverHelper;

    public KyrieOauth2Configuration(Oauth2ClientCredentialsResolver resolverHelper) {
        this.resolverHelper = resolverHelper;
    }

    @PostConstruct
    void setup() {
        logger.info("The Authorization Server is enabled, starting Kyrie bootstrap");
    }

    @Bean
    @ConditionalOnMissingBean
    public KyrieEventPublisher kyrieEventPublisher(ApplicationEventMulticaster multicaster) {
        return new DefaultSpringKyrieEventMulticaster(multicaster);
    }

    /**
     * {@link ClientId} bean that creates only on request.
     * The ClientId can be inject through {@link com.odeyalo.kyrie.support.ClientIdAware}
     * @return - ClientId or null if request does not contain client_id parameter
     */
    @Bean
    @RequestScope
    public ClientId clientId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        Oauth2ClientCredentials clientCredentials = resolverHelper.resolveCredentials(request, false);

        if (clientCredentials == null) {
            return null;
        }
        ClientId wrap = ClientId.wrap(clientCredentials.getClientId());
        logger.debug("Created client id: {}", wrap);
        return wrap;
    }
}

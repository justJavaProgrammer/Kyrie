package com.odeyalo.kyrie.config.configuration;

import com.odeyalo.kyrie.config.MockedWebSecurityConfiguration;
import com.odeyalo.kyrie.config.Oauth2ClientCredentialsResolverHelper;
import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authentication.InMemoryOauth2UserAuthenticationService;
import com.odeyalo.kyrie.core.authentication.Oauth2UserAuthenticationService;
import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;
import com.odeyalo.kyrie.core.oauth2.client.ClientCredentialsValidator;
import com.odeyalo.kyrie.core.oauth2.client.DefaultClientCredentialsValidator;
import com.odeyalo.kyrie.core.oauth2.client.Oauth2ClientRepository;
import com.odeyalo.kyrie.support.ClientId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
        Oauth2FlowHandlersConfiguration.class,
        RedirectUriCreationServicesConfiguration.class,
        KyrieOauth2RequestValidationConfiguration.class,
        KyrieOauth2ServerEndpointsMappingConfiguration.class,
        MockedWebSecurityConfiguration.class
})
public class KyrieOauth2Configuration {
    private final Logger logger = LoggerFactory.getLogger(KyrieOauth2Configuration.class);
    private final Oauth2ClientCredentialsResolverHelper resolverHelper;

    public KyrieOauth2Configuration(Oauth2ClientCredentialsResolverHelper resolverHelper) {
        this.resolverHelper = resolverHelper;
    }

    @PostConstruct
    void setup() {
        logger.info("The Authorization Server is enabled, starting Kyrie bootstrap");
    }

    @Bean
    @ConditionalOnMissingBean
    public ClientCredentialsValidator clientCredentialsValidator(Oauth2ClientRepository repository) {
        return new DefaultClientCredentialsValidator(repository);
    }

    @Bean
    @ConditionalOnMissingBean
    @Autowired(required = false)
    public Oauth2UserAuthenticationService authenticationService(List<Oauth2User> users) {
        return new InMemoryOauth2UserAuthenticationService(users);
    }

    @Bean
    public List<Oauth2User> users() {
        Oauth2User user = Oauth2User.builder().username("admin").password("123").authorities(Set.of("USER")).additionalInfo(Collections.emptyMap()).build();
        return List.of(user);
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

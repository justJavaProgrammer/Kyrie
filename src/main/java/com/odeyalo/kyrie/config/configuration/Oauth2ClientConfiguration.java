package com.odeyalo.kyrie.config.configuration;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authentication.InMemoryOauth2UserAuthenticationService;
import com.odeyalo.kyrie.core.authentication.Oauth2UserAuthenticationService;
import com.odeyalo.kyrie.core.oauth2.client.InMemoryOauth2ClientRepository;
import com.odeyalo.kyrie.core.oauth2.client.Oauth2Client;
import com.odeyalo.kyrie.core.oauth2.client.Oauth2ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;

@Configuration
public class Oauth2ClientConfiguration {

    /**
     * Registry the InMemoryOauth2ClientRepository bean with 'oauth2ClientRepository' name.
     * Search for list of Oauth2Client that is optional. If nothing was found,
     * then InMemoryOauth2ClientRepository without any clients will be created
     * @param clients - optional clients to registry in repository
     * @return - default Oauth2ClientRepository implementation bean
     * @see Oauth2ClientRepository
     * @see InMemoryOauth2ClientRepository
     */
    @Bean
    @ConditionalOnMissingBean
    @Autowired(required = false)
    public Oauth2ClientRepository oauth2ClientRepository(List<Oauth2Client> clients) {
        return new InMemoryOauth2ClientRepository(clients);
    }

    @Bean
    @ConditionalOnMissingBean
    @Autowired(required = false)
    public Oauth2UserAuthenticationService oauth2UserAuthenticationService(List<Oauth2User> users) {
        return new InMemoryOauth2UserAuthenticationService(users);
    }

    @Bean
    @ConditionalOnMissingBean
    public List<Oauth2User> users() {
        Oauth2User user = Oauth2User.builder()
                .id("1")
                .username("Miku")
                .password("password")
                .authorities(Set.of("USER"))
                .build();
        return List.of(user);
    }

    @Bean
    @ConditionalOnMissingBean
    public List<Oauth2Client> clients() {
        Oauth2Client client = Oauth2Client.builder()
                .clientId("odeyalo")
                .clientSecret("password")
                .clientType(Oauth2Client.ClientType.CONFIDENTIAL)
                .allowedRedirectUri("https://oauth.pstmn.io/v1/callback")
                .build();
        return List.of(client);
    }
}

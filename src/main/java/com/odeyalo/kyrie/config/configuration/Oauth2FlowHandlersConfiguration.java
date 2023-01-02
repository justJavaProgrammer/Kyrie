package com.odeyalo.kyrie.config.configuration;

import com.odeyalo.kyrie.core.oauth2.Oauth2ScopeHandler;
import com.odeyalo.kyrie.core.oauth2.client.ClientCredentialsValidator;
import com.odeyalo.kyrie.core.oauth2.flow.*;
import com.odeyalo.kyrie.core.oauth2.oidc.EmailOidcOauth2ScopeHandler;
import com.odeyalo.kyrie.core.oauth2.oidc.generator.OidcIdTokenGenerator;
import com.odeyalo.kyrie.core.oauth2.oidc.generator.OidcIdTokenGeneratorImpl;
import com.odeyalo.kyrie.core.oauth2.oidc.generator.OidcOauth2TokenGeneratorFacade;
import com.odeyalo.kyrie.core.oauth2.oidc.generator.OidcOauth2TokenGeneratorFacadeImpl;
import com.odeyalo.kyrie.core.oauth2.support.grant.AuthorizationGrantTypeResolver;
import com.odeyalo.kyrie.core.oauth2.support.grant.AuthorizationGrantTypeResolverImpl;
import com.odeyalo.kyrie.core.oauth2.tokens.AccessTokenReturner;
import com.odeyalo.kyrie.core.oauth2.tokens.DefaultAccessTokenReturner;
import com.odeyalo.kyrie.core.oauth2.tokens.DefaultJwtOauth2AccessTokenManager;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessTokenManager;
import com.odeyalo.kyrie.core.oauth2.tokens.code.*;
import com.odeyalo.kyrie.core.oauth2.tokens.code.provider.AuthorizationCodeProvider;
import com.odeyalo.kyrie.core.oauth2.tokens.code.provider.DefaultStoringAuthorizationCodeProvider;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.DefaultJwtOauth2AccessTokenGenerator;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.DefaultSecretWordJwtTokenProvider;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.JwtTokenProvider;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.Oauth2AccessTokenGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * Configuration class that used to create Oauth2FlowHandler-type beans and all required beans for them
 *
 * @version 1.0
 * @see Oauth2FlowHandler
 */
public class Oauth2FlowHandlersConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public Oauth2AccessTokenGenerator oauth2AccessTokenGenerator(JwtTokenProvider jwtTokenProvider) {
        return new DefaultJwtOauth2AccessTokenGenerator(jwtTokenProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtTokenProvider jwtTokenProvider(@Value("${kyrie.tokens.jwt.secret.key}") String secretWord) {
        return new DefaultSecretWordJwtTokenProvider(secretWord);
    }

    @Bean
    @ConditionalOnMissingBean
    public AccessTokenReturner accessTokenReturner(ClientCredentialsValidator clientCredentialsValidator, AuthorizationCodeManager authorizationCodeManager, Oauth2AccessTokenGenerator oauth2AccessTokenGenerator) {
        return new DefaultAccessTokenReturner(clientCredentialsValidator, authorizationCodeManager, oauth2AccessTokenGenerator);
    }


    @Bean
    @ConditionalOnMissingBean
    public ClientSideOauth2FlowHandlerFactory clientSideOauth2FlowHandlerFactory(List<ClientSideOauth2FlowHandler> handlers) {
        return new DefaultClientSideOauth2FlowHandlerFactory(handlers);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthorizationCodeOauth2FlowHandler authorizationCodeOauth2FlowHandler(AuthorizationCodeProvider authorizationCodeProvider) {
        return new AuthorizationCodeOauth2FlowHandler(authorizationCodeProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    public MultipleResponseTypeOidcOauth2FlowHandler multipleResponseTypeOidcOauth2FlowHandler(OidcOauth2TokenGeneratorFacade generatorFacade,
                                                                                               Oauth2AccessTokenGenerator oauth2AccessTokenGenerator,
                                                                                               AuthorizationCodeProvider provider) {
        return new DefaultMultipleResponseTypeOidcOauth2FlowHandler(generatorFacade, oauth2AccessTokenGenerator, provider);
    }

    @Bean
    @ConditionalOnMissingBean
    public Oauth2FlowHandlerFactory oauth2FlowHandlerFactory(List<Oauth2FlowHandler> handlers) {
        return new GenericOauth2FlowHandlerFactory(handlers);
    }

    @Bean
    @ConditionalOnMissingBean
    public ImplicitClientSideOauth2FlowHandler implicitClientSideOauth2FlowHandler(Oauth2AccessTokenGenerator oauth2AccessTokenGenerator) {
        return new ImplicitClientSideOauth2FlowHandler(oauth2AccessTokenGenerator);
    }

    @Bean
    @ConditionalOnMissingBean
    public EmailOidcOauth2ScopeHandler emailOidcOauth2ScopeHandler() {
        return new EmailOidcOauth2ScopeHandler();
    }
    //todo Add configurers to some beans to make it more flexible

    @Bean
    @ConditionalOnMissingBean
    public OidcIdTokenGenerator oidcIdTokenGenerator(JwtTokenProvider provider, @Value("${kyrie.tokens.common.issuer}") String issuer) {
        return new OidcIdTokenGeneratorImpl(provider, issuer);
    }

    @Bean
    @ConditionalOnMissingBean
    public OidcOauth2TokenGeneratorFacade oidcOauth2TokenGeneratorFacade(OidcIdTokenGenerator tokenGenerator, List<Oauth2ScopeHandler> handlers) {
        return new OidcOauth2TokenGeneratorFacadeImpl(tokenGenerator, handlers);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthorizationGrantTypeResolver authorizationGrantTypeResolver() {
        return new AuthorizationGrantTypeResolverImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthorizationCodeGenerator authorizationCodeGenerator() {
        return new AuthorizationCodeGeneratorImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthorizationCodeProvider defaultStoringAuthorizationCodeProvider(AuthorizationCodeGenerator authorizationCodeGenerator,
                                                                                           AuthorizationCodeStore codeStore) {
        return new DefaultStoringAuthorizationCodeProvider(authorizationCodeGenerator, codeStore);
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultStoringAuthorizationCodeManager defaultStoringAuthorizationCodeManager(AuthorizationCodeProvider authorizationCodeProvider, AuthorizationCodeStore codeStore) {
        return new DefaultStoringAuthorizationCodeManager(authorizationCodeProvider, codeStore);
    }
    @Bean
    @ConditionalOnMissingBean
    public AuthorizationCodeStore authorizationCodeStore() {
        return new InMemoryAuthorizationCodeStore();
    }

    @Bean
    @ConditionalOnMissingBean
    public Oauth2AccessTokenManager oauth2AccessTokenManager(JwtTokenProvider jwtTokenProvider, AccessTokenReturner returner, Oauth2AccessTokenGenerator oauth2AccessTokenGenerator) {
        return new DefaultJwtOauth2AccessTokenManager(jwtTokenProvider, returner, oauth2AccessTokenGenerator);
    }
}

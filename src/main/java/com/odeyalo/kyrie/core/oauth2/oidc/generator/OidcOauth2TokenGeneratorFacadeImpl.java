package com.odeyalo.kyrie.core.oauth2.oidc.generator;

import com.odeyalo.kyrie.core.oauth2.Oauth2ScopeHandler;
import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;
import com.odeyalo.kyrie.core.oauth2.oidc.OidcIdToken;
import com.odeyalo.kyrie.core.oauth2.oidc.OidcScopes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Generate an OidcIdToken based on scopes
 * @see OidcIdToken
 * @see OidcOauth2TokenGeneratorFacade
 */
@Component
public class OidcOauth2TokenGeneratorFacadeImpl implements OidcOauth2TokenGeneratorFacade {
    private final OidcIdTokenGenerator oidcIdTokenGenerator;
    private final Map<String, Oauth2ScopeHandler> handlers;
    private final Logger logger = LoggerFactory.getLogger(OidcOauth2TokenGeneratorFacadeImpl.class);

    @Autowired
    public OidcOauth2TokenGeneratorFacadeImpl(OidcIdTokenGenerator oidcIdTokenGenerator, List<Oauth2ScopeHandler> handlers) {
        this.oidcIdTokenGenerator = oidcIdTokenGenerator;
        this.handlers = handlers.stream().collect(Collectors.toMap(Oauth2ScopeHandler::supportedScope, Function.identity()));
    }

    @Override
    public OidcIdToken generateToken(Oauth2ClientCredentials credentials, Oauth2User user, String[] scopes) {
        Map<String, Object> resultClaims = new HashMap<>();
        // Iterate through scopes and resolve only scopes that support OpenId Connect.
        // Create claims by delegating job to Oauth2ScopeHandler and put the resolved claims in the resultClaims.
        Arrays.stream(scopes).filter(OidcScopes::isOpenIDScope).forEach(scope -> {
            Map<String, Object> claims = handlers.get(scope).createClaims(user);
            logger.info("Claims resolved {} for scope: {}", claims, scope);
            resultClaims.putAll(claims);
        });
        return this.oidcIdTokenGenerator.generateOidcToken(credentials.getClientId(), user, resultClaims);
    }
}

package com.odeyalo.kyrie.core.oauth2.oidc.generator;

import com.odeyalo.kyrie.core.oauth2.Oauth2TokenGeneratorFacade;
import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;
import com.odeyalo.kyrie.core.oauth2.oidc.OidcIdToken;

/**
 * Facade to generate and do something with OidcIdToken.
 * @see OidcIdToken
 * @see Oauth2TokenGeneratorFacade
 */
public interface OidcOauth2TokenGeneratorFacade extends Oauth2TokenGeneratorFacade {
    /**
     * Generate OidcIdToken and return it. Oauth2ClientCredentials can contain ONLY client id and client secret can be null
     * @param credentials - client credentials with clientId field not null, clientSecret can be null
     * @param user - Oauth2User that contains info
     * @param scopes - required scopes
     * @return - Generated OidcIdToken
     */
    @Override
    OidcIdToken generateToken(Oauth2ClientCredentials credentials, Oauth2User user, String[] scopes);
}

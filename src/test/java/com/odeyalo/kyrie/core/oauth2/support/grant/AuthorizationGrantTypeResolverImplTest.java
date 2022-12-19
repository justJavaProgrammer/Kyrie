package com.odeyalo.kyrie.core.oauth2.support.grant;

import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.oauth2.oidc.OidcResponseType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for AuthorizationGrantTypeResolverImpl
 * @see AuthorizationGrantTypeResolverImpl
 */
class AuthorizationGrantTypeResolverImplTest {
    private final AuthorizationGrantTypeResolverImpl resolver = new AuthorizationGrantTypeResolverImpl();

    @Test
    @DisplayName("Resolve grant type by Oauth2ResponseType.CODE and expect AUTHORIZATION_CODE")
    void resolveGrantTypeAndExpectAuthorizationCode() {
        AuthorizationGrantType expected = AuthorizationGrantType.AUTHORIZATION_CODE;

        AuthorizationGrantType type = resolver.resolveGrantType(Oauth2ResponseType.CODE);

        assertEquals(expected, type);
    }

    @Test
    @DisplayName("Resolve grant type by Oauth2ResponseType.TOKEN and expect AuthorizationGrantType.IMPLICIT")
    void resolveGrantTypeByTokenAndExpectImplicit() {
        AuthorizationGrantType expected = AuthorizationGrantType.IMPLICIT;

        AuthorizationGrantType type = resolver.resolveGrantType(Oauth2ResponseType.TOKEN);

        assertEquals(expected, type);
    }

    @Test
    @DisplayName("Resolve grant type by ID_TOKEN and CODE and expect AuthorizationGrantType.HYBRID")
    void resolveGrantTypeByIdTokenWithCodeAndExpectHybrid() {
        AuthorizationGrantType expected = AuthorizationGrantType.MULTIPLE;

        AuthorizationGrantType type = resolver.resolveGrantType(OidcResponseType.ID_TOKEN, Oauth2ResponseType.CODE);

        assertEquals(expected, type);
    }

    @Test
    @DisplayName("Resolve grant type by ID_TOKEN and CODE and TOKEN and expect AuthorizationGrantType.HYBRID")
    void resolveGrantTypeByIdTokenWithCodeWithTokenAndExpectHybrid() {
        AuthorizationGrantType expected = AuthorizationGrantType.MULTIPLE;

        AuthorizationGrantType type = resolver.resolveGrantType(OidcResponseType.ID_TOKEN, Oauth2ResponseType.CODE, Oauth2ResponseType.TOKEN);

        assertEquals(expected, type);
    }
}

package com.odeyalo.kyrie.core.oauth2.support.grant;

import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.oauth2.oidc.OidcResponseType;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for AuthorizationGrantTypeResolverImpl
 *
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


    @Test
    @DisplayName("Resolve grant type from cache and expect not null")
    void resolveGrantTypeFromCacheAndExpectSuccess() {
        HashMap<Oauth2ResponseType[], AuthorizationGrantType> cache = new HashMap<>();

        Oauth2ResponseType[] cacheTypes1 = Arrays.array(Oauth2ResponseType.CODE, Oauth2ResponseType.TOKEN);
        Oauth2ResponseType[] cacheTypes2 = Arrays.array(Oauth2ResponseType.CODE);
        Oauth2ResponseType[] cacheTypes3 = Arrays.array(Oauth2ResponseType.TOKEN);

        AuthorizationGrantType cacheGrant1 = AuthorizationGrantType.MULTIPLE;
        AuthorizationGrantType cacheGrant2 = AuthorizationGrantType.AUTHORIZATION_CODE;
        AuthorizationGrantType cacheGrant3 = AuthorizationGrantType.IMPLICIT;

        cache.put(cacheTypes1, cacheGrant1);
        cache.put(cacheTypes2, cacheGrant2);
        cache.put(cacheTypes3, cacheGrant3);

        AuthorizationGrantTypeResolverImpl resolver = new AuthorizationGrantTypeResolverImpl(cache);
        AuthorizationGrantType actual1 = resolver.resolveGrantType(cacheTypes1);
        AuthorizationGrantType actual2 = resolver.resolveGrantType(cacheTypes2);
        AuthorizationGrantType actual3 = resolver.resolveGrantType(cacheTypes3);

        assertNotNull(actual1, "If types are cached, then not null value must be returned");
        assertNotNull(actual2, "If types are cached, then not null value must be returned");
        assertNotNull(actual3, "If types are cached, then not null value must be returned");

        assertEquals(cacheGrant1, actual1, "Grant types from cache must be equal");
        assertEquals(cacheGrant2, actual2, "Grant types from cache must be equal");
        assertEquals(cacheGrant3, actual3, "Grant types from cache must be equal");
    }

    @Test
    @DisplayName("Test AuthorizationGrantTypeResolverImpl constructors")
    void testConstructors() {
        assertDoesNotThrow(() -> new AuthorizationGrantTypeResolverImpl());
        HashMap<Oauth2ResponseType[], AuthorizationGrantType> cacheWithNull = new HashMap<>();

        cacheWithNull.put(Arrays.array(Oauth2ResponseType.CODE), null);
        assertThrows(IllegalStateException.class, () -> new AuthorizationGrantTypeResolverImpl(cacheWithNull), "If cache contains null then exception must be thrown");

        ArrayList<AuthorizationGrantType> sortedWithNull = new ArrayList<>();
        sortedWithNull.add(AuthorizationGrantType.AUTHORIZATION_CODE);
        sortedWithNull.add(null);

        assertThrows(IllegalStateException.class, () -> new AuthorizationGrantTypeResolverImpl(sortedWithNull, cacheWithNull), "If cache contains null then exception must be thrown");

        Oauth2ResponseType[] cacheTypes1 = Arrays.array(Oauth2ResponseType.CODE, Oauth2ResponseType.TOKEN);
        Oauth2ResponseType[] cacheTypes2 = Arrays.array(Oauth2ResponseType.CODE);

        HashMap<Oauth2ResponseType[], AuthorizationGrantType> cache = new HashMap<>();

        AuthorizationGrantType cacheGrant1 = AuthorizationGrantType.MULTIPLE;
        AuthorizationGrantType cacheGrant2 = AuthorizationGrantType.AUTHORIZATION_CODE;

        cache.put(cacheTypes1, cacheGrant1);
        cache.put(cacheTypes2, cacheGrant2);

        List<AuthorizationGrantType> sortedGrantTypes = new ArrayList<>();

        sortedGrantTypes.add(AuthorizationGrantType.AUTHORIZATION_CODE);
        sortedGrantTypes.add(AuthorizationGrantType.IMPLICIT);
        sortedGrantTypes.add(AuthorizationGrantType.MULTIPLE);


        assertThrows(IllegalStateException.class, () -> {
            new AuthorizationGrantTypeResolverImpl(sortedGrantTypes, cacheWithNull);
        }, "Exception must be thrown when cache contains null");

        assertThrows(IllegalStateException.class, () -> {
            new AuthorizationGrantTypeResolverImpl(sortedWithNull, cache);
        }, "Exception must be thrown when sorted types list contains null");

        assertDoesNotThrow(() -> {
            new AuthorizationGrantTypeResolverImpl(sortedGrantTypes, cache);
        }, "Nothing must be thrown when no null presented");
    }
}

package com.odeyalo.kyrie.core.oauth2.tokens.code.provider;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.oauth2.tokens.code.AuthorizationCode;
import com.odeyalo.kyrie.core.oauth2.tokens.code.AuthorizationCodeGeneratorImpl;
import com.odeyalo.kyrie.core.oauth2.tokens.code.AuthorizationCodeStore;
import com.odeyalo.kyrie.core.oauth2.tokens.code.InMemoryAuthorizationCodeStore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DefaultAuthorizationCodeProvider class.
 * @see DefaultAuthorizationCodeProvider
 */
class DefaultAuthorizationCodeProviderTest {
    private final AuthorizationCodeStore store = new InMemoryAuthorizationCodeStore();
    private final DefaultAuthorizationCodeProvider defaultAuthorizationCodeProvider = new DefaultAuthorizationCodeProvider(
            new AuthorizationCodeGeneratorImpl(), store);

    @Test
    @DisplayName("Get authorization code and check that code is valid ")
    void getAuthorizationCodeAndExpectValidCode() {
        String clientId = "clientId123";
        String[] scopes = {"read", "write"};

        Oauth2User expectedUser = new Oauth2User("1", "odeyalo", "innocentdays", Set.of("USER"), Collections.emptyMap());
        AuthorizationCode code = defaultAuthorizationCodeProvider.getAuthorizationCode(clientId, expectedUser, scopes);

        String codeValue = code.getCodeValue();
        Instant expiresIn = code.getExpiresIn();
        Instant issuedAt = code.getIssuedAt();
        Oauth2User actualUser = code.getUser();
        assertNotNull(codeValue, "Code value must be not null");
        assertNotNull(expiresIn, "expiresIn can't be null");
        AuthorizationCode fromStore = store.findByAuthorizationCodeValue(codeValue);
        assertEquals(code, fromStore, "Code returned from method and code that is stored in AuthorizationCodeStore must be equal");
        assertEquals(expectedUser, actualUser, "Users must be equal");
        if (issuedAt != null) {
            assertTrue(expiresIn.isAfter(issuedAt), "expires_in must be greater than issued_at");
        }
    }
}

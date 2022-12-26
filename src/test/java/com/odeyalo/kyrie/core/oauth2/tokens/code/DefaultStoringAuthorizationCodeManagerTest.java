package com.odeyalo.kyrie.core.oauth2.tokens.code;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.oauth2.tokens.code.provider.AuthorizationCodeProvider;
import com.odeyalo.kyrie.core.oauth2.tokens.code.provider.DefaultAuthorizationCodeProvider;
import org.junit.jupiter.api.*;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DefaultStoringAuthorizationCodeManager class.
 * @see DefaultStoringAuthorizationCodeManager
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultStoringAuthorizationCodeManagerTest {
    private final AuthorizationCodeStore store = new InMemoryAuthorizationCodeStore();
    private final AuthorizationCodeProvider authorizationCodeProvider = new DefaultAuthorizationCodeProvider(new AuthorizationCodeGeneratorImpl(), store);
    private final DefaultStoringAuthorizationCodeManager manager = new DefaultStoringAuthorizationCodeManager(authorizationCodeProvider, store);
    private static final String EXISTING_AUTHORIZATION_CODE_ID = "code_id";
    private static final String EXISTING_AUTHORIZATION_CODE_VALUE = "noextended";

    private final Oauth2User defaultUser = new Oauth2User("2", "C.C.", "alone", Set.of("USER"), Collections.emptyMap());
    private final AuthorizationCode EXPECTED_AUTHORIZATION_CODE = AuthorizationCode
            .builder()
            .codeValue(EXISTING_AUTHORIZATION_CODE_VALUE)
            .scopes(new String[]{"read", "write"})
            .issuedAt(Instant.now())
            .expiresIn(Instant.now().plusSeconds(80))
            .user(defaultUser)
            .build();
    private Long elementsCountInManager = 0L;

    @BeforeEach
    void init() {
        store.save(EXISTING_AUTHORIZATION_CODE_ID, EXPECTED_AUTHORIZATION_CODE);
        elementsCountInManager += 1;
    }

    @AfterEach
    void clear() {
        store.deleteALl();
        elementsCountInManager = 0L;
    }

    @Test
    @DisplayName("Generate authorization code and expect that code will be saved to store and code is valid")
    void generateAuthorizationCode() {
        String clientId = "Vi Britannia";
        Oauth2User expectedUser = new Oauth2User("1", "odeyalo", "quietside", Set.of("USER"), Collections.emptyMap());
        String[] scopes = {"read", "write"};
        AuthorizationCode generatedCode = manager.generateAuthorizationCode(clientId, expectedUser, scopes);

        String codeValue = generatedCode.getCodeValue();
        Instant expiresIn = generatedCode.getExpiresIn();
        Instant issuedAt = generatedCode.getIssuedAt();
        Oauth2User actualUser = generatedCode.getUser();

        assertNotNull(codeValue, "Code value must be not null");
        assertNotNull(expiresIn, "expiresIn can't be null");

        AuthorizationCode fromStore = store.findByAuthorizationCodeValue(codeValue);
        assertNotNull(fromStore, "Generated code must be stored when using DefaultStoringAuthorizationCodeManager");
        assertEquals(generatedCode, fromStore, "Code returned from method and code that is stored in AuthorizationCodeStore must be equal");
        assertEquals(expectedUser, actualUser, "Users must be equal");
        if (issuedAt != null) {
            assertTrue(expiresIn.isAfter(issuedAt), "expires_in must be greater than issued_at");
        }
    }

    @Test
    @DisplayName("Get auth code by code value that does not exist and except null")
    void getAuthorizationCodeByNotExistingAuthorizationCodeValue() {
        String notExistingCode = "miku";
        AuthorizationCode code = manager.getAuthorizationCodeByAuthorizationCodeValue(notExistingCode);
        assertNull(code, "DefaultStoringAuthorizationCodeManager must return null if value does not presented in store!");
    }

    @Test
    @DisplayName("Get auth code by code value that exist and except success")
    void getAuthorizationCodeByExistingAuthorizationCodeValue() {
        AuthorizationCode code = manager.getAuthorizationCodeByAuthorizationCodeValue(EXISTING_AUTHORIZATION_CODE_VALUE);
        assertNotNull(code, "If code is exist in store, then Manager must return code and NOT return null as result");
        assertEquals(EXPECTED_AUTHORIZATION_CODE, code, "Manager must return same code that was provided");
    }

    @Test
    @DisplayName("Delete not existing code and expect nothing to delete")
    void deleteNotExistingAuthorizationCodeAndExpectNothing() {
        String notExistingCode = "whatisjustice";
        manager.deleteAuthorizationCode(notExistingCode);
        Long actual = store.count();
        // Assert that element that was added in BeforeAll method does not deleted and everything presented
        assertEquals(elementsCountInManager, actual, "If element is not present in store then nothing must be deleted");
        AuthorizationCode code = manager.getAuthorizationCodeByAuthorizationCodeValue(EXISTING_AUTHORIZATION_CODE_VALUE);
        assertEquals(EXPECTED_AUTHORIZATION_CODE, code, "If element is not present in store then nothing must be deleted");
    }
    @Test
    @DisplayName("Delete existing code and expect code to delete")
    void deleteExistingAuthorizationCodeAndExpectDeletion() {
        manager.deleteAuthorizationCode(EXISTING_AUTHORIZATION_CODE_ID);
        Long actual = store.count();
        // Assert that element that was added in BeforeEach method has been removed
        assertEquals(elementsCountInManager - 1, actual, "If element is in store then element must be deleted");
        AuthorizationCode code = manager.getAuthorizationCodeByAuthorizationCodeValue(EXISTING_AUTHORIZATION_CODE_VALUE);
        assertNull(code, "If element was deleted then null must be returned");
    }
}

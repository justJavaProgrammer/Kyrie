package com.odeyalo.kyrie.core.oauth2.tokens.code;

import com.odeyalo.kyrie.core.Oauth2User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for InMemoryAuthorizationCodeStore class.
 * @see InMemoryAuthorizationCodeStore
 */
class InMemoryAuthorizationCodeStoreTest {
    private final InMemoryAuthorizationCodeStore store = new InMemoryAuthorizationCodeStore();

    private static final String EXISTING_AUTHORIZATION_CODE_ID = "loadtheammo";
    private static final String EXISTING_AUTHORIZATION_CODE_VALUE = "code_value";


    private final Oauth2User defaultUser = new Oauth2User("2", "L.L", "ifkingdoesnotmove", Set.of("USER"), Collections.emptyMap());
    private final AuthorizationCode EXPECTED_AUTHORIZATION_CODE = AuthorizationCode
            .builder()
            .codeValue(EXISTING_AUTHORIZATION_CODE_VALUE)
            .scopes(new String[]{"read", "write"})
            .issuedAt(Instant.now())
            .expiresIn(Instant.now().plusSeconds(80))
            .user(defaultUser)
            .build();
    private Long elementsCountInStore = 0L;

    @BeforeEach
    void init() {
        store.save(EXISTING_AUTHORIZATION_CODE_ID, EXPECTED_AUTHORIZATION_CODE);
        elementsCountInStore += 1;
    }

    @AfterEach
    void clear() {
        store.deleteALl();
        elementsCountInStore = 0L;
    }


    @Test
    @DisplayName("Save valid authorization code and expect success")
    void saveValidCodeAndExpectSuccess() {
        String id = "scarydreams";
        String codeValue = "stephany";

        Oauth2User defaultUser = new Oauth2User("2", "Shirley", "youcant", Set.of("USER"), Collections.emptyMap());
        AuthorizationCode code = AuthorizationCode
                .builder()
                .codeValue(codeValue)
                .scopes(new String[]{"read", "write"})
                .issuedAt(Instant.now())
                .expiresIn(Instant.now().plusSeconds(80))
                .user(defaultUser)
                .build();
        store.save(id, code);
        assertTrue(store.count() != 0, "If element was saved to store, then store size must be greater than 0!");
        AuthorizationCode fromStore = store.findByAuthorizationCodeValue(codeValue);
        assertNotNull(code, "Code that was saved cannot be null!");
        assertEquals(code, fromStore, "Code must be equal to code that was saved");
    }

    @Test
    @DisplayName("Find authorization code by existing id and expect valid code")
    void findByExistingIdAndExpectSuccess() {
        AuthorizationCode code = store.findById(EXISTING_AUTHORIZATION_CODE_ID);
        assertNotNull(code, "Code that is presented in store can't be null!");
        assertEquals(EXPECTED_AUTHORIZATION_CODE, code, "Actual code and code from store must be equal!");
    }

    @Test
    @DisplayName("Find authorization code by not existing id and expect null as result")
    void findByNotExistingIdAndExpectSuccess() {
        String notExistingId = "theangelnextdoor";
        AuthorizationCode code = store.findById(notExistingId);
        assertNull(code, "Code that is not stored in must be null!");
        assertEquals(elementsCountInStore, store.count(), "If element wasn't deleted from store then elements count must be same!");
    }

    @Test
    @DisplayName("Find auth code by existing authorization code value and expect success")
    void findByExistingAuthorizationCodeValue() {
        AuthorizationCode actualCode = store.findByAuthorizationCodeValue(EXISTING_AUTHORIZATION_CODE_VALUE);
        assertEquals(elementsCountInStore, store.count(), "If using findBy method then elements count must be same as before the call");
        assertEquals(EXPECTED_AUTHORIZATION_CODE, actualCode);
    }

    @Test
    @DisplayName("Find auth code by not existing authorization code value and expect null")
    void findByNotExistingAuthorizationCodeValue() {
        String notExistingId = "not_existing";
        AuthorizationCode actualCode = store.findByAuthorizationCodeValue(notExistingId);
        assertEquals(elementsCountInStore, store.count(), "If using findBy method then elements count must be same as before the call");
        assertNull(actualCode, "If store does not contain element by value, then null must be returned as result");
    }

    @Test
    @DisplayName("Delete by existing id and expect success")
    void deleteByExistingCodeIdAndExpectSuccess() {
        long beforeDelete = store.count();
        store.delete(EXISTING_AUTHORIZATION_CODE_ID);
        long afterDelete = store.count();
        assertEquals(afterDelete, beforeDelete - 1, "If element was deleted by existing id, then count must be not equal");
        AuthorizationCode code = store.findById(EXISTING_AUTHORIZATION_CODE_ID);
        assertNull(code, "If code was deleted by id then store must return null");
        AuthorizationCode byValue = store.findById(EXISTING_AUTHORIZATION_CODE_VALUE);
        assertNull(byValue, "If code was deleted by id then store must return null even if call method using code value");
    }

    @Test
    @DisplayName("Delete by existing code value and expect success")
    void deleteByExistingCodeValueAndExpectSuccess() {
        long beforeDelete = store.count();
        store.delete(EXPECTED_AUTHORIZATION_CODE);
        long afterDelete = store.count();
        assertEquals(afterDelete, beforeDelete - 1, "If element was deleted by existing id, then count must be not equal");
        AuthorizationCode byValue = store.findById(EXISTING_AUTHORIZATION_CODE_VALUE);
        assertNull(byValue, "If code was deleted by id then store must return null even if call method using code value");
        AuthorizationCode byId = store.findById(EXISTING_AUTHORIZATION_CODE_ID);
        assertNull(byId, "If code was deleted by id then store must return null");
    }

    @Test
    @DisplayName("Delete by not existing code id and expect nothing")
    void deleteByNotExistingCodeIdAndExpectNothing() {
        String notExistingId = "not_existing";
        long beforeDelete = store.count();
        store.delete(notExistingId);
        long afterDelete = store.count();
        assertEquals(afterDelete, beforeDelete, "Count must be equal if code was not deleted by id");
    }

    @Test
    @DisplayName("Delete by not existing code value and expect nothing")
    void deleteByNotExistingCodeValueAndExpectNothing() {
        String notExistingValue = "not_existing";
        long beforeDelete = store.count();
        store.delete(notExistingValue);
        long afterDelete = store.count();
        assertEquals(afterDelete, beforeDelete, "Count must be equal if code was not deleted by code value");
    }

    @Test
    @DisplayName("Delete all elements from store and expect 0 elements after deletion")
    void deleteAllAndExpect0AsResult() {
        Long count = store.count();
        Long size = store.deleteALl();
        assertEquals(count, size, "Count and size of deleted elements  must be equal");
        Long afterDelete = store.count();
        assertEquals(0, afterDelete, "After delete count must be 0");
    }

    @Test
    @DisplayName("Count all elements in store")
    void count() {
        assertEquals(elementsCountInStore, store.count(), "Element count in store and store.count must be equal if no element was added");
    }
}

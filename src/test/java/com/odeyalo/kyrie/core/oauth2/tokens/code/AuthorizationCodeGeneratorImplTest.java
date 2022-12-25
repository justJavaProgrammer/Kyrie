package com.odeyalo.kyrie.core.oauth2.tokens.code;

import com.odeyalo.kyrie.core.Oauth2User;
import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AuthorizationCodeGeneratorImpl class
 * @see AuthorizationCodeGeneratorImpl
 */
class AuthorizationCodeGeneratorImplTest {
    private final AuthorizationCodeGeneratorImpl generator = new AuthorizationCodeGeneratorImpl();

    @Test
    @DisplayName("Generate authorization code and expect all checks pass")
    void generateAuthorizationCode() {
        Integer requiredLength = 10;
        int expireTime = 60;
        Oauth2User user = new Oauth2User("1", "odeyalo", "stephany", Set.of("USER"), Collections.emptyMap());
        String[] scopes = {"read", "write"};
        AuthorizationCode code = generator.generateAuthorizationCode(requiredLength, expireTime, user, scopes);
        assertNotNull(code, "Generator can't return null as result!");
        String codeValue = code.getCodeValue();
        String[] codeScopes = code.getScopes();
        Oauth2User codeUser = code.getUser();
        assertNotNull(codeValue, "Code value must be presented");
        assertEquals(requiredLength, codeValue.length(), "Code length must be equal to length that was provided in method params");
        assertNotNull(scopes, "Scopes can't be null");
        assertArrayEquals(scopes, codeScopes, "Code must contain scopes that was provided in method params!");
        assertEquals(user, codeUser, "User from AuthorizationCode must be equal to user that was provided");
    }
}

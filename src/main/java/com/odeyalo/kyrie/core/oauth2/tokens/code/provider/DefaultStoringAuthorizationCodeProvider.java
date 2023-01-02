package com.odeyalo.kyrie.core.oauth2.tokens.code.provider;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.oauth2.tokens.code.AuthorizationCode;
import com.odeyalo.kyrie.core.oauth2.tokens.code.AuthorizationCodeGenerator;
import com.odeyalo.kyrie.core.oauth2.tokens.code.AuthorizationCodeStore;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Default AuthorizationCodeProvider implementation that just generate and save an AuthorizationCode
 */
public class DefaultStoringAuthorizationCodeProvider implements AuthorizationCodeProvider {
    private final AuthorizationCodeGenerator codeGenerator;
    private final AuthorizationCodeStore codeStore;

    @Autowired
    public DefaultStoringAuthorizationCodeProvider(AuthorizationCodeGenerator codeGenerator, AuthorizationCodeStore codeStore) {
        this.codeGenerator = codeGenerator;
        this.codeStore = codeStore;
    }

    /**
     * Generate an authorization code and save it to AuthorizationCodeStore
     * @param clientId - client id that requested authorization
     * @param user - user that granted access
     * @param scopes - scopes that will be applied to authorization code
     * @return - AuthorizationCode that can be used to obtain access token
     */
    @Override
    public AuthorizationCode getAuthorizationCode(String clientId, Oauth2User user, String[] scopes) {
        AuthorizationCode authorizationCode = codeGenerator.generateAuthorizationCode(user, scopes);
        codeStore.save(authorizationCode.getCodeValue(), authorizationCode);
        return authorizationCode;
    }
}

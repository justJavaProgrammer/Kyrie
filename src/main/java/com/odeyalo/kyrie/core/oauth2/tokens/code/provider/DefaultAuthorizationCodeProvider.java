package com.odeyalo.kyrie.core.oauth2.tokens.code.provider;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.oauth2.tokens.code.AuthorizationCode;
import com.odeyalo.kyrie.core.oauth2.tokens.code.AuthorizationCodeGenerator;
import com.odeyalo.kyrie.core.oauth2.tokens.code.AuthorizationCodeStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Default AuthorizationCodeProvider implementation that just generate and save an AuthorizationCode
 */
@Service
public class DefaultAuthorizationCodeProvider implements AuthorizationCodeProvider {
    private final AuthorizationCodeGenerator codeGenerator;
    private final AuthorizationCodeStore codeStore;

    @Autowired
    public DefaultAuthorizationCodeProvider(AuthorizationCodeGenerator codeGenerator, AuthorizationCodeStore codeStore) {
        this.codeGenerator = codeGenerator;
        this.codeStore = codeStore;
    }

    @Override
    public AuthorizationCode getAuthorizationCode(String clientId, Oauth2User user, String[] scopes) {
        AuthorizationCode authorizationCode = codeGenerator.generateAuthorizationCode(user, scopes);
        codeStore.save(clientId, authorizationCode);
        return authorizationCode;
    }
}

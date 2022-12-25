package com.odeyalo.kyrie.core.oauth2.tokens.code;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.oauth2.tokens.code.provider.AuthorizationCodeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultAuthorizationCodeManager implements AuthorizationCodeManager {
    private final AuthorizationCodeProvider authorizationCodeProvider;
    private final AuthorizationCodeStore authorizationCodeStore;

    @Autowired
    public DefaultAuthorizationCodeManager(AuthorizationCodeProvider authorizationCodeProvider, AuthorizationCodeStore authorizationCodeStore) {
        this.authorizationCodeProvider = authorizationCodeProvider;
        this.authorizationCodeStore = authorizationCodeStore;
    }

    @Override
    public AuthorizationCode generateAuthorizationCode(String clientId, Oauth2User user, String[] scopes) {
        return authorizationCodeProvider.getAuthorizationCode(clientId, user, scopes);
    }

    @Override
    public AuthorizationCode getAuthorizationCodeByAuthorizationCodeValue(String authCode) {
        return authorizationCodeStore.findByAuthorizationCodeValue(authCode);
    }

    @Override
    public void deleteAuthorizationCode(String clientId) {
        authorizationCodeStore.delete(clientId);
    }
}

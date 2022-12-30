package com.odeyalo.kyrie.core.oauth2.tokens.code;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.oauth2.tokens.code.provider.AuthorizationCodeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AuthorizationCodeManager implementation that always use a code store to manipulate with authorization codes.
 *
 * @version 1.0
 * @see AuthorizationCodeStore
 */
@Service
public class DefaultStoringAuthorizationCodeManager implements AuthorizationCodeManager {
    private final AuthorizationCodeProvider authorizationCodeProvider;
    private final AuthorizationCodeStore authorizationCodeStore;

    @Autowired
    public DefaultStoringAuthorizationCodeManager(AuthorizationCodeProvider authorizationCodeProvider, AuthorizationCodeStore authorizationCodeStore) {
        this.authorizationCodeProvider = authorizationCodeProvider;
        this.authorizationCodeStore = authorizationCodeStore;
    }

    /**
     * Generate an authorization code and save it if code wasn't saved by AuthorizationCodeProvider
     *
     * @param clientId - client id
     * @param user     - user that granted access
     * @param scopes   - scopes to authorization code
     * @return - generated authorization code that saved in AuthorizationCodeStore
     */
    @Override
    public AuthorizationCode generateAuthorizationCode(String clientId, Oauth2User user, String[] scopes) {
        AuthorizationCode code = authorizationCodeProvider.getAuthorizationCode(clientId, user, scopes);
        authorizationCodeStore.save(code.getCodeValue(), code);
        return code;
    }

    @Override
    public AuthorizationCode getAuthorizationCodeByAuthorizationCodeValue(String authCode) {
        return authorizationCodeStore.findByAuthorizationCodeValue(authCode);
    }

    @Override
    public void deleteAuthorizationCode(String id) {
        authorizationCodeStore.delete(id);
    }
}

package com.odeyalo.kyrie.core.oauth2.support;

import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;

public interface AuthorizationCodeFlowRedirectUrlCreationService extends RedirectUrlCreationService {

    @Override
    default AuthorizationGrantType supportedGrantType() {
        return AuthorizationGrantType.AUTHORIZATION_CODE;
    }
}

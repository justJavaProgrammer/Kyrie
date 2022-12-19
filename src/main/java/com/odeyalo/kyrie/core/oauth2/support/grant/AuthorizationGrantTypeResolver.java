package com.odeyalo.kyrie.core.oauth2.support.grant;

import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;

/**
 * Resolve an authorization grant type by provided response types
 */
public interface AuthorizationGrantTypeResolver {

    AuthorizationGrantType resolveGrantType(Oauth2ResponseType... types);

}

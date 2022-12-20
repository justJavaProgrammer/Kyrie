package com.odeyalo.kyrie.core.oauth2.flow;

import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.oauth2.OidcOauth2FlowType;

/**
 * Interface to resolve name of flow by provided array of Oauth2ResponseType.
 * @see OidcOauth2FlowType
 * @version 1.0
 */
public interface MultipleResponseTypeOidcFlowTypeResolver {
    /**
     * Resolve flow type by given array of types
     * @param types - array of Oauth2ResponseType requested by a client
     * @return - resolved OidcOauth2FlowType, null otherwise
     */
    OidcOauth2FlowType resolveFlowType(Oauth2ResponseType[] types);

}

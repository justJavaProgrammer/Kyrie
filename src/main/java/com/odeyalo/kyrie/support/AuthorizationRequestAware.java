package com.odeyalo.kyrie.support;

import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import org.springframework.beans.factory.Aware;

/**
 * {@link Aware} implementation that uses to set {@link AuthorizationRequest} in AuthorizationRequestAware implementations through
 * {@link AuthorizationRequestAware#setAuthorizationRequest(AuthorizationRequest)}.
 * <p>
 * NOTE: AuthorizationRequest isn't a Spring Bean and will be injected through Proxy.
 * </p>
 *
 * @version 1.0
 * @see Aware
 * @see AuthorizationRequest
 * @see ClientIdAwarePostProcessor
 */
public interface AuthorizationRequestAware {
    /**
     * Set the AuthorizationRequest
     *
     * @param request - AuthorizationRequest to inject
     */
    void setAuthorizationRequest(AuthorizationRequest request);
}

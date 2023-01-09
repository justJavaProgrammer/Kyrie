package com.odeyalo.kyrie.core.authorization.support;

import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Simple class that used to store the {@link AuthorizationRequest}
 *
 * @see AuthorizationRequest
 */
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationRequestContext {
    private AuthorizationRequest request;

    /**
     * Returns the {@link AuthorizationRequest}, null if request was not set
     *
     * @return - AuthorizationRequest, null otherwise
     */
    public AuthorizationRequest getRequest() {
        return request;
    }

    /**
     * Set the {@link AuthorizationRequest} in context
     *
     * @param request - request to set
     */
    public void setRequest(AuthorizationRequest request) {
        this.request = request;
    }
}

package com.odeyalo.kyrie.core.authorization.support;

/**
 *  A strategy for storing authorization request information against a thread.
 *
 *  <p>The preferred strategy is loaded by {@link AuthorizationRequestContextHolder}</p>
 */
public interface AuthorizationRequestContextHolderStrategy {

    /**
     * Clears the context for the given thread
     */
    void clearContext();

    /**
     * Set the current context.
     * @param context - context to set
     */
    void setContext(AuthorizationRequestContext context);

    /**
     * Obtains the current context.
     * @return - AuthorizationRequestContext that was previously set, null otherwise
     */
    AuthorizationRequestContext getContext();


    AuthorizationRequestContext createEmptyContext();
}

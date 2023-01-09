package com.odeyalo.kyrie.core.authorization.support;

import org.springframework.util.Assert;

/**
 * {@link AuthorizationRequestContextHolderStrategy} implementation that uses ThreadLocal to store the {@link AuthorizationRequestContext}.
 *
 *
 * @see AuthorizationRequestContextHolderStrategy
 * @see AuthorizationRequestContext
 * @see ThreadLocal
 */
public class ThreadLocalAuthorizationRequestContextHolderStrategy implements AuthorizationRequestContextHolderStrategy {
    private static final ThreadLocal<AuthorizationRequestContext> holder = new ThreadLocal<>();

    @Override
    public void clearContext() {
        holder.remove();
    }

    @Override
    public void setContext(AuthorizationRequestContext context) {
        Assert.notNull(context, "The AuthorizationRequestContext must be not null");
        holder.set(context);
    }

    @Override
    public AuthorizationRequestContext getContext() {
        AuthorizationRequestContext context = holder.get();
        return context != null ? context : createEmptyContext();
    }

    @Override
    public AuthorizationRequestContext createEmptyContext() {
        return new AuthorizationRequestContext();
    }
}

package com.odeyalo.kyrie.core.support;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Holds the AuthorizationRequestContext.
 * The class provides static methods to create, get and modify the AuthorizationRequestContext that just delegate job to {@link AuthorizationRequestContextHolderStrategy}.
 */
public class AuthorizationRequestContextHolder {
    public static final String MODE_THREAD_LOCAL = "THREAD_LOCAL";
    public static final String MODE_PRE_INITIALIZED = "PRE_INITIALIZED";

    private static AuthorizationRequestContextHolderStrategy strategy;
    private static String strategyName;

    static {
        initialize();
    }

    private static void initialize() {
        // Set default if strategy name was not set
        if (!StringUtils.hasText(strategyName)) {
            strategyName = MODE_THREAD_LOCAL;
        }

        if (MODE_PRE_INITIALIZED.equals(strategyName)) {
            Assert.state(strategy != null, "MODE_PRE_INITIALIZED requires fully constructed strategy!");
            return;
        }

        if (MODE_THREAD_LOCAL.equals(strategyName)) {
            AuthorizationRequestContextHolder.strategy = new ThreadLocalAuthorizationRequestContextHolderStrategy();
            return;
        }
    }

    /**
     * Changes the preferred strategy. Do <em>NOT</em> all this method more than once for
     * a given JVM, as it will re-initialize the strategy and adversely affect any
     * existing threads using the old strategy.
     * @param strategy - fully constructed strategy.
     */
    public static void setStrategy(AuthorizationRequestContextHolderStrategy strategy) {
        AuthorizationRequestContextHolder.strategy = strategy;
        AuthorizationRequestContextHolder.strategyName = MODE_PRE_INITIALIZED;
        initialize();
    }

    /**
     * Changes the preferred strategy. Do <em>NOT</em> call this method more than once for
     * a given JVM, as it will re-initialize the strategy and adversely affect any
     * existing threads using the old strategy.
     *
     * @param strategyName - strategy name from available.
     */
    public static void setStrategyName(String strategyName) {
        AuthorizationRequestContextHolder.strategyName = strategyName;
        initialize();
    }

    public static void setContext(AuthorizationRequestContext context) {
        strategy.setContext(context);
    }

    public static AuthorizationRequestContext getContext() {
        return strategy.getContext();
    }

    public static AuthorizationRequestContextHolderStrategy getHolderStrategy() {
        return strategy;
    }
}

package com.odeyalo.kyrie.core.oauth2.tokens;

/**
 * Factory to produce {@link AccessTokenGranterStrategy} implementations based on conditions
 */
public interface AccessTokenGranterStrategyFactory {

    /**
     * Returns an {@link AccessTokenGranterStrategy} implementation based on {@link TokenRequest}
     * @param request - request from controller
     * @return - AccessTokenGranterStrategy if any condition matched, null otherwise
     */
    AccessTokenGranterStrategy getGranter(TokenRequest request);

}

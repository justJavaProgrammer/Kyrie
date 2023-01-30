package com.odeyalo.kyrie.core.oauth2.support.callbacks;

import lombok.Data;

/**
 * Callback that will be invoked when the user authentication is failed
 */
@FunctionalInterface
public interface AuthenticationFailedCallback extends Callback<AuthenticationFailedCallback.AuthenticationFailedCallbackData> {

    void handleAuthenticationFailedCallback(AuthenticationFailedCallbackData data);

    @Override
    default void handleCallback(AuthenticationFailedCallbackData data) {
        handleAuthenticationFailedCallback(data);
    }

    @Data
    class AuthenticationFailedCallbackData {
        private final String reason;
    }
}

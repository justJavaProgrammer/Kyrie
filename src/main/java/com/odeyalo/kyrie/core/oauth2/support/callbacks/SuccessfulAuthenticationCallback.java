package com.odeyalo.kyrie.core.oauth2.support.callbacks;

import com.odeyalo.kyrie.core.Oauth2User;
import lombok.Data;

/**
 * Callback that will be invoked when the user has been successfully authenticated
 */
@FunctionalInterface
public interface SuccessfulAuthenticationCallback extends Callback<SuccessfulAuthenticationCallback.SuccessfulAuthenticationCallbackData> {


    void handleAuthenticationSuccessCallback(SuccessfulAuthenticationCallbackData data);

    @Override
    default void handleCallback(SuccessfulAuthenticationCallbackData data) {
        handleAuthenticationSuccessCallback(data);
    }

    @Data
    class SuccessfulAuthenticationCallbackData {
        // User that authenticated
        private final Oauth2User user;
    }
}

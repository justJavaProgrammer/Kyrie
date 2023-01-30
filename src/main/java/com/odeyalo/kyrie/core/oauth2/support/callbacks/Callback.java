package com.odeyalo.kyrie.core.oauth2.support.callbacks;

/**
 * Callback interface for all callbacks provided by Kyrie
 * @param <T> - type of the callback parameter
 */
@FunctionalInterface
public interface Callback<T> {

    void handleCallback(T data);

}

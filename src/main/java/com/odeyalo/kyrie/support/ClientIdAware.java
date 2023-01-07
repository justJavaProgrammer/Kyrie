package com.odeyalo.kyrie.support;

import org.springframework.beans.factory.Aware;

/**
 * {@link Aware} implementation that uses to set {@link ClientId} in ClientIdAware implementations through {@link ClientIdAware#setClientId(ClientId)}.
 * Note: Since ClientId is request-scoped bean instead real ClientId will be injected Proxy.
 *
 * @version 1.0
 * @see Aware
 * @see ClientId
 * @see ClientIdAwarePostProcessor
 */
public interface ClientIdAware extends Aware {

    /**
     * Set ClientId in bean
     * @param clientId - ClientId with fields set or null, if container does not contain ClientId bean
     */
    void setClientId(ClientId clientId);

}

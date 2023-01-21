package com.odeyalo.kyrie.core.events;

/**
 * A simple facade interface to unite the event registry and event publishing
 *
 * @see KyrieEventPublisher
 * @see KyrieEventListenersRegistry
 */
public interface KyrieEventMulticaster extends KyrieEventListenersRegistry, KyrieEventPublisher {

}

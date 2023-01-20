package com.odeyalo.kyrie.core.events.listener;

import com.odeyalo.kyrie.core.events.KyrieEventListenersRegistry;
import com.odeyalo.kyrie.core.events.KyrieEventPublisher;

/**
 * A simple facade interface to unite the event registry and event publishing
 *
 * @see KyrieEventPublisher
 * @see KyrieEventListenersRegistry
 */
public interface KyrieEventMulticaster extends KyrieEventListenersRegistry, KyrieEventPublisher {

}

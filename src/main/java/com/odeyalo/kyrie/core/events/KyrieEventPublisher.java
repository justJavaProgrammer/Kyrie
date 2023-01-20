package com.odeyalo.kyrie.core.events;

/**
 * A contract for publishing Kyrie events
 *
 * @see KyrieEvent
 */
@FunctionalInterface
public interface KyrieEventPublisher {
    /**
     * Publish the given event
     * @param event - event to publish
     */
    void publishEvent(KyrieEvent event);
}

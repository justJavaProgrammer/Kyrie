package com.odeyalo.kyrie.core.events;

import java.time.Instant;

/**
 * Represent an event that can be thrown by Kyrie
 */
public interface KyrieEvent {

    /**
     * An ID for the event
     * @return - an id for this event, can be null but it doesn't recommended
     */
    String id();

    /**
     * Time when the event was thrown
     * @return - when event was thrown, never null
     */
    Instant time();
}

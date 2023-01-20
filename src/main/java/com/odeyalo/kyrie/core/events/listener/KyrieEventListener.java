package com.odeyalo.kyrie.core.events.listener;

import com.odeyalo.kyrie.core.events.AbstractKyrieEvent;
import com.odeyalo.kyrie.core.events.KyrieEvent;
import org.springframework.context.ApplicationListener;

/**
 * Simple functional interface that used to handle events published by Kyrie.
 * @param <E> - type of the {@link KyrieEvent} to handle
 */
@FunctionalInterface
public interface KyrieEventListener<E extends AbstractKyrieEvent> extends ApplicationListener<E> {

    /**
     * Method that will be invoked everytime when the event was published
     * @param event - published event
     */
    void onEvent(E event);

    @Override
    default void onApplicationEvent(E event) {
        onEvent(event);
    }
}

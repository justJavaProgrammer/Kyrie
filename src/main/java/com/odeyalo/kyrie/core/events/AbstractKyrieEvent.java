package com.odeyalo.kyrie.core.events;

import org.springframework.context.ApplicationEvent;

import java.time.Instant;

/**
 * Represent an abstract {@link KyrieEvent}, also supports {@link ApplicationEvent} provided by Spring to make possible publish events through {@link org.springframework.context.ApplicationEventPublisher}.
 *
 * <p>Abstract as it doesn't make sense for generic events to be published directly.</p>
 *
 * @see ApplicationEvent
 * @see org.springframework.context.ApplicationEventPublisher
 */
public abstract class AbstractKyrieEvent extends ApplicationEvent implements KyrieEvent {
    private final String id;
    private final Instant time;

    public AbstractKyrieEvent(Object source, String eventId) {
        super(source);
        this.id = eventId;
        this.time = Instant.now();
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Instant time() {
        return time;
    }
}

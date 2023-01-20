package com.odeyalo.kyrie.core.events.listener;

import com.odeyalo.kyrie.core.events.AbstractKyrieEvent;
import com.odeyalo.kyrie.core.events.KyrieEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Default {@link KyrieEventMulticaster} implementation that publish events through {@link ApplicationEventMulticaster}.
 */
public class DefaultSpringKyrieEventMulticaster implements KyrieEventMulticaster {
    private final List<KyrieEventListener<? extends KyrieEvent>> listeners;
    private final Logger logger = LoggerFactory.getLogger(DefaultSpringKyrieEventMulticaster.class);
    private final ApplicationEventMulticaster multicaster;

    public DefaultSpringKyrieEventMulticaster(ApplicationEventMulticaster multicaster) {
        this.multicaster = multicaster;
        this.listeners = new ArrayList<>();
        this.logger.debug("Initialized listeners with empty map");
    }

    public DefaultSpringKyrieEventMulticaster(ApplicationEventMulticaster multicaster, List<KyrieEventListener<? extends KyrieEvent>> listeners) {
        this.multicaster = multicaster;
        Assert.notNull(listeners, "The listeners must be not null!");
        this.listeners = listeners;
        for (KyrieEventListener<? extends KyrieEvent> listener : listeners) {
            multicaster.addApplicationListener(listener);
        }
        this.logger.debug("Initialized listeners with {} size", listeners.size());
    }

    @Override
    @SuppressWarnings("unckecked")
    public void registryListener(KyrieEventListener<? extends KyrieEvent> listener) {
        multicaster.addApplicationListener(listener);
        this.logger.debug("Registered the listener: {}", listener.getClass().getName());
    }

    @Override
    public void removeListener(KyrieEventListener<? extends KyrieEvent> listener) {
        multicaster.removeApplicationListener(listener);
        this.logger.debug("Removed the element");
    }

    @Override
    public List<KyrieEventListener<? extends KyrieEvent>> getListeners() {
        return listeners;
    }

    @Override
    public void publishEvent(KyrieEvent event) {
        multicaster.multicastEvent((AbstractKyrieEvent) event);
    }
}

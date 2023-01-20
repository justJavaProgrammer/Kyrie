package com.odeyalo.kyrie.core.events;

import com.odeyalo.kyrie.core.events.listener.KyrieEventListener;

import java.util.List;

/**
 * Registry that used to registry {@link KyrieEvent} listeners.
 */
public interface KyrieEventListenersRegistry {

    void registryListener(KyrieEventListener<? extends KyrieEvent> listener);

    void removeListener(KyrieEventListener<? extends KyrieEvent> listener);

    List<KyrieEventListener<? extends KyrieEvent>> getListeners();
}

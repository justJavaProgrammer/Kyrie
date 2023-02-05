package com.odeyalo.kyrie.core.events.listener;

import com.odeyalo.kyrie.core.events.AuthorizationRequestProcessingFinishedKyrieEvent;

/**
 * Listener interface for {@link AuthorizationRequestProcessingFinishedKyrieEvent} class
 */
public interface AuthorizationRequestProcessingFinishedKyrieEventListener extends KyrieEventListener<AuthorizationRequestProcessingFinishedKyrieEvent> {
    @Override
    void onEvent(AuthorizationRequestProcessingFinishedKyrieEvent event);
}

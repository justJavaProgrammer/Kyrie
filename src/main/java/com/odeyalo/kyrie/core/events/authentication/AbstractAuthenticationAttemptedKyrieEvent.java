package com.odeyalo.kyrie.core.events.authentication;

import com.odeyalo.kyrie.core.events.authentication.support.AttemptedLoginAuthentication;

/**
 * Abstract event that should be extended by events that occurred when login is attempted
 */
public abstract class AbstractAuthenticationAttemptedKyrieEvent extends AbstractAuthenticationKyrieEvent {

    public AbstractAuthenticationAttemptedKyrieEvent(AttemptedLoginAuthentication authentication) {
        super(authentication);
    }

    public AbstractAuthenticationAttemptedKyrieEvent(AttemptedLoginAuthentication authentication, String eventId) {
        super(authentication, eventId);
    }
}

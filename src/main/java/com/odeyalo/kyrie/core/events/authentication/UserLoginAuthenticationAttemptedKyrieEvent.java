package com.odeyalo.kyrie.core.events.authentication;

import com.odeyalo.kyrie.core.events.authentication.support.AttemptedLoginAuthentication;
import org.apache.commons.lang.RandomStringUtils;

/**
 * The event will be occurred every time when the user tries to login
 */
public class UserLoginAuthenticationAttemptedKyrieEvent extends AbstractAuthenticationAttemptedKyrieEvent {
    /**
     * Create a new UserLoginAuthenticationAttemptedKyrieEvent
     * @param authentication- {@link AttemptedLoginAuthentication} with required fields set
     */
    public UserLoginAuthenticationAttemptedKyrieEvent(AttemptedLoginAuthentication authentication) {
        super(authentication, RandomStringUtils.randomAlphanumeric(20));
    }
    /**
     * Create a new UserLoginAuthenticationAttemptedKyrieEvent with custom event id
     * @param eventId - custom event id
     */
    public UserLoginAuthenticationAttemptedKyrieEvent(AttemptedLoginAuthentication authentication, String eventId) {
        super(authentication, eventId);
    }
}

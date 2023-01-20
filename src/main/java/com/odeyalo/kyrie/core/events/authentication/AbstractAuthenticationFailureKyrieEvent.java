package com.odeyalo.kyrie.core.events.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

/**
 * Abstract event for all event classes that provided info about authentication that was failed
 */
public abstract class AbstractAuthenticationFailureKyrieEvent extends AbstractAuthenticationKyrieEvent {
    private final AuthenticationException authenticationException;

    /**
     * Create AbstractAuthenticationKyrieEvent with random string as id.
     * @param authentication - will be wrapped into {@link com.odeyalo.kyrie.core.events.authentication.AbstractAuthenticationKyrieEvent.NullAuthentication} if null
     * @param authenticationException - exception with description
     */
    public AbstractAuthenticationFailureKyrieEvent(Authentication authentication, AuthenticationException authenticationException) {
        super(authentication);
        Assert.notNull(authenticationException, "AuthenticationException must be not null when authentication is failed!");
        this.authenticationException = authenticationException;
    }

    /**
     * Create AbstractAuthenticationKyrieEvent with provided event id.
     * @param eventId - custom event id
     * @param authentication - will be wrapped into {@link com.odeyalo.kyrie.core.events.authentication.AbstractAuthenticationKyrieEvent.NullAuthentication} if null
     * @param authenticationException - exception with description what is wrong
     */
    public AbstractAuthenticationFailureKyrieEvent(String eventId, Authentication authentication, AuthenticationException authenticationException) {
        super(authentication, eventId);
        Assert.notNull(authenticationException, "AuthenticationException must be not null when authentication is failed!");
        this.authenticationException = authenticationException;
    }

    public AuthenticationException getAuthenticationException() {
        return authenticationException;
    }
}

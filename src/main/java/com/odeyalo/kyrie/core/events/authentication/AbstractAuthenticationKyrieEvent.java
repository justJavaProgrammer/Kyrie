package com.odeyalo.kyrie.core.events.authentication;

import com.odeyalo.kyrie.core.events.AbstractKyrieEvent;
import com.odeyalo.kyrie.core.events.authentication.support.NullAuthentication;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.security.core.Authentication;

/**
 * <p>Represent ab abstract authentication event that can be published by Kyrie.</p>
 *
 * <strong>Note:</strong>
 * Kyrie does not use {@link org.springframework.security.authentication.event.AbstractAuthenticationEvent} to separate Spring Security events with events provided by Kyrie
 */
public abstract class AbstractAuthenticationKyrieEvent extends AbstractKyrieEvent {
    private final Authentication authentication;
    /**
     * <p>Create AbstractAuthenticationKyrieEvent with random string as id.</p>
     * <strong>Note:</strong> The Authentication will be automatically wrapped to {@link NullAuthentication} to avoid NPE exception in {@link org.springframework.context.ApplicationEvent}
     * @param authentication - authentication that was performed, if authentication attempt was failed - null
     */
    public AbstractAuthenticationKyrieEvent(Authentication authentication) {
        super(authentication == null ? new NullAuthentication() : authentication, RandomStringUtils.randomAlphanumeric(20));
        this.authentication = authentication == null ? new NullAuthentication() : authentication;
    }

    /**
     * Create AbstractAuthenticationKyrieEvent with provided event id.
     * @param authentication - authentication that was performed, if authentication attempt was failed - null
     */
    public AbstractAuthenticationKyrieEvent(Authentication authentication, String eventId) {
        super(authentication, eventId);
        this.authentication = authentication == null ? new NullAuthentication() : authentication;
    }

    public Authentication getAuthentication() {
        return authentication;
    }
}

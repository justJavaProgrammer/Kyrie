package com.odeyalo.kyrie.core.events.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * Event that will be occurred when wrong credentials were provided.
 */
public class AuthenticationFailureBadCredentialsKyrieEvent extends AbstractAuthenticationFailureKyrieEvent {

    public AuthenticationFailureBadCredentialsKyrieEvent(Authentication authentication, AuthenticationException authenticationException) {
        super(authentication, authenticationException);
    }

    public AuthenticationFailureBadCredentialsKyrieEvent(String eventId, Authentication authentication, AuthenticationException authenticationException) {
        super(eventId, authentication, authenticationException);
    }
}

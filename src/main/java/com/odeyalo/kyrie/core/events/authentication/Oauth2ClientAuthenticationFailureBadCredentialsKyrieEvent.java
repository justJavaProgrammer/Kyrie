package com.odeyalo.kyrie.core.events.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * Event that will be occurred when the oauth2 client provided wrong credentials and login cannot be performed
 */
public class Oauth2ClientAuthenticationFailureBadCredentialsKyrieEvent extends AbstractAuthenticationFailureKyrieEvent {

    public Oauth2ClientAuthenticationFailureBadCredentialsKyrieEvent(Authentication authentication, AuthenticationException authenticationException) {
        super(authentication, authenticationException);
    }

    public Oauth2ClientAuthenticationFailureBadCredentialsKyrieEvent(String eventId, Authentication authentication, AuthenticationException authenticationException) {
        super(eventId, authentication, authenticationException);
    }
}

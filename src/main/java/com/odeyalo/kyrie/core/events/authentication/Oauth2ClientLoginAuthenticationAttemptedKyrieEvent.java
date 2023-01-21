package com.odeyalo.kyrie.core.events.authentication;

import com.odeyalo.kyrie.core.events.authentication.support.AttemptedLoginAuthentication;

/**
 * Event that will be occurred when the {@link com.odeyalo.kyrie.core.oauth2.client.Oauth2Client} attempted to login
 */
public class Oauth2ClientLoginAuthenticationAttemptedKyrieEvent extends AbstractAuthenticationAttemptedKyrieEvent {

    public Oauth2ClientLoginAuthenticationAttemptedKyrieEvent(AttemptedLoginAuthentication authentication) {
        super(authentication);
    }

    public Oauth2ClientLoginAuthenticationAttemptedKyrieEvent(AttemptedLoginAuthentication authentication, String eventId) {
        super(authentication, eventId);
    }
}

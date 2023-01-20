package com.odeyalo.kyrie.core.events.authentication;

import org.springframework.security.core.Authentication;

/**
 * <p>The event will be occurred everytime when user has been successfully authenticated.</p>
 *
 * <strong>NOTE:</strong> The event WON'T be occurred when {@link com.odeyalo.kyrie.core.oauth2.client.Oauth2Client} authenticated. Use {@link Oauth2ClientLoginAuthenticationGrantedKyrieEvent} for this purpose
 */
public class UserLoginAuthenticationGrantedKyrieEvent extends AbstractAuthenticationKyrieEvent {

    public UserLoginAuthenticationGrantedKyrieEvent(Authentication authentication) {
        super(authentication);
    }

    public UserLoginAuthenticationGrantedKyrieEvent(Authentication authentication, String eventId) {
        super(authentication, eventId);
    }
}

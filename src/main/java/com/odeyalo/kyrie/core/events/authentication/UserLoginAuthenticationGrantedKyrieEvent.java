package com.odeyalo.kyrie.core.events.authentication;

import com.odeyalo.kyrie.core.Oauth2User;
import org.springframework.security.core.Authentication;

/**
 * <p>The event will be occurred every time when user has been successfully authenticated.</p>
 *
 * <strong>NOTE:</strong> The event WON'T be occurred when {@link com.odeyalo.kyrie.core.oauth2.client.Oauth2Client} has been authenticated.
 * Use {@link Oauth2ClientLoginAuthenticationGrantedKyrieEvent} for this purpose
 */
public class UserLoginAuthenticationGrantedKyrieEvent extends AbstractAuthenticationKyrieEvent {
    // User that granted access
    protected final Oauth2User oauth2User;

    public UserLoginAuthenticationGrantedKyrieEvent(Authentication authentication, Oauth2User oauth2User) {
        super(authentication);
        this.oauth2User = oauth2User;
    }

    public UserLoginAuthenticationGrantedKyrieEvent(Authentication authentication, String eventId, Oauth2User oauth2User) {
        super(authentication, eventId);
        this.oauth2User = oauth2User;
    }

    public Oauth2User getOauth2User() {
        return oauth2User;
    }
}

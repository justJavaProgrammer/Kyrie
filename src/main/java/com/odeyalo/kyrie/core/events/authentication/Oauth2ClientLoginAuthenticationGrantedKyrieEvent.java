package com.odeyalo.kyrie.core.events.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

/**
 * Event that will be occurred every time when the {@link com.odeyalo.kyrie.core.oauth2.client.Oauth2Client} has been successfully authenticated by Kyrie.
 * @see com.odeyalo.kyrie.core.oauth2.client.Oauth2Client
 * @see AbstractAuthenticationFailureKyrieEvent
 */
public class Oauth2ClientLoginAuthenticationGrantedKyrieEvent extends AbstractAuthenticationKyrieEvent {
    /**
     * Create a new Oauth2ClientLoginAuthenticationGrantedKyrieEvent with provided Authentication
     * @param authentication - authenticated Oauth2Client, never null. Authentication principal MUST contain Oauth2User as principal
     */
    public Oauth2ClientLoginAuthenticationGrantedKyrieEvent(Authentication authentication) {
        super(nullCheck(authentication));
    }
    /**
     * Create a new Oauth2ClientLoginAuthenticationGrantedKyrieEvent with provided Authentication and provided event id
     * @param authentication - authenticated Oauth2Client, never null. Authentication principal MUST contain Oauth2User as principal
     */
    public Oauth2ClientLoginAuthenticationGrantedKyrieEvent(Authentication authentication, String eventId) {
        super(nullCheck(authentication), eventId);
    }

    /**
     * Checks the given Authentication for null and returns it. If authentication is null, then IllegalArgumentException will be thrown
     * @param authentication - authentication to check
     * @return - provided Authentication without any modifications
     */
    private static Authentication nullCheck(Authentication authentication) {
        Assert.notNull(authentication, "The Authentication must be not null!");
        return authentication;
    }
}

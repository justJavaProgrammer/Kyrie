package com.odeyalo.kyrie.core.events.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * Event that will be occurred when wrong credentials were provided.
 * <p>
 * Abstract since Kyrie provide 2 different entities for authentication:
 * {@link com.odeyalo.kyrie.core.Oauth2User} and {@link com.odeyalo.kyrie.core.oauth2.client.Oauth2Client}
 * </p>
 */
public abstract class AbstractAuthenticationFailureBadCredentialsKyrieEvent extends AbstractAuthenticationFailureKyrieEvent {

    public AbstractAuthenticationFailureBadCredentialsKyrieEvent(Authentication authentication, AuthenticationException authenticationException) {
        super(authentication, authenticationException);
    }

    public AbstractAuthenticationFailureBadCredentialsKyrieEvent(String eventId, Authentication authentication, AuthenticationException authenticationException) {
        super(eventId, authentication, authenticationException);
    }
}

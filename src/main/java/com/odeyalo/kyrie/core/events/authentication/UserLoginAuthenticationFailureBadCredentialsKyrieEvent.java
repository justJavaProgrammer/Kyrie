package com.odeyalo.kyrie.core.events.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * Event that will be occurred when user login is failed and reason is bad credentials provided by user.
 */
public class UserLoginAuthenticationFailureBadCredentialsKyrieEvent extends AbstractAuthenticationFailureBadCredentialsKyrieEvent {

    public UserLoginAuthenticationFailureBadCredentialsKyrieEvent(Authentication authentication, AuthenticationException authenticationException) {
        super(authentication, authenticationException);
    }
}

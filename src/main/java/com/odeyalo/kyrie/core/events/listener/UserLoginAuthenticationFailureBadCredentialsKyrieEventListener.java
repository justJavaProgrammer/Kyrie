package com.odeyalo.kyrie.core.events.listener;

import com.odeyalo.kyrie.core.events.authentication.UserLoginAuthenticationFailureBadCredentialsKyrieEvent;

/**
 * Interface that should be implemented by listener that wants to listen to {@link UserLoginAuthenticationFailureBadCredentialsKyrieEvent}
 *
 * @see com.odeyalo.kyrie.core.events.KyrieEvent
 * @see KyrieEventListener
 */
public interface UserLoginAuthenticationFailureBadCredentialsKyrieEventListener extends KyrieEventListener<UserLoginAuthenticationFailureBadCredentialsKyrieEvent> {
}

package com.odeyalo.kyrie.core.events.listener;

import com.odeyalo.kyrie.core.events.authentication.UserLoginAuthenticationGrantedKyrieEvent;

/**
 * Interface that can be implemented by listener that wants to listen to {@link UserLoginAuthenticationGrantedKyrieEvent}
 *
 * @see UserLoginAuthenticationGrantedKyrieEvent
 */
public interface UserLoginAuthenticationGrantedKyrieEventListener extends KyrieEventListener<UserLoginAuthenticationGrantedKyrieEvent> {
}

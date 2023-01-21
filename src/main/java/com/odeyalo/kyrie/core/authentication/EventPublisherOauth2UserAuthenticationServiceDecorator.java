package com.odeyalo.kyrie.core.authentication;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.events.KyrieEventPublisher;
import com.odeyalo.kyrie.core.events.authentication.UserLoginAuthenticationAttemptedKyrieEvent;
import com.odeyalo.kyrie.core.events.authentication.UserLoginAuthenticationFailureBadCredentialsKyrieEvent;
import com.odeyalo.kyrie.core.events.authentication.UserLoginAuthenticationGrantedKyrieEvent;
import com.odeyalo.kyrie.core.events.authentication.support.AttemptedLoginAuthentication;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link Oauth2UserAuthenticationService} implementation that used as wrapper to publish the authentication events.
 *
 * @see Oauth2UserAuthenticationService
 * @see com.odeyalo.kyrie.core.events.KyrieEvent
 */
public class EventPublisherOauth2UserAuthenticationServiceDecorator implements Oauth2UserAuthenticationService {
    private final Oauth2UserAuthenticationService delegate;
    private final KyrieEventPublisher eventPublisher;

    /**
     * Create new EventPublisherOauth2UserAuthenticationServiceDecorator
     *
     * @param delegate       = original {@link Oauth2UserAuthenticationService} that will be used to delegate actual job
     * @param eventPublisher - event publisher that will be used to publish events
     */
    public EventPublisherOauth2UserAuthenticationServiceDecorator(Oauth2UserAuthenticationService delegate, KyrieEventPublisher eventPublisher) {
        this.delegate = delegate;
        this.eventPublisher = eventPublisher;
    }

    /**
     * <p>Publish the events BEFORE and AFTER authentication.</p>
     * <p>
     * Events published before authentication:
     * {@link UserLoginAuthenticationAttemptedKyrieEvent} with username and password that was provided in
     * {@link Oauth2UserAuthenticationInfo}
     * </p>
     * <p>
     * Events published after authentication:
     * {@link UserLoginAuthenticationGrantedKyrieEvent} with original user that wrapped into {@link UsernamePasswordAuthenticationToken}
     * if the original {@link Oauth2UserAuthenticationService} returns the {@link AuthenticationResult#success(Oauth2User)}
     *
     * <p>{@link UserLoginAuthenticationFailureBadCredentialsKyrieEvent} with null authentication and BadCredentialsException
     * if the {@link Oauth2UserAuthenticationService} returns the {@link AuthenticationResult#failed()}</p>
     * </p>
     *
     * @param info - user credentials
     * @return - unmodified AuthenticationResult from original  Oauth2UserAuthenticationService
     */
    @Override
    public AuthenticationResult authenticate(Oauth2UserAuthenticationInfo info) {
        eventPublisher.publishEvent(new UserLoginAuthenticationAttemptedKyrieEvent(AttemptedLoginAuthentication.of(info.getUsername(), info.getPassword())));

        AuthenticationResult result = delegate.authenticate(info);
        //todo
        if (result.isSuccess()) {
            eventPublisher.publishEvent(new UserLoginAuthenticationGrantedKyrieEvent(new UsernamePasswordAuthenticationToken(result.getUser(), result.getUser().getPassword(), getAuthorities(result))));
        } else {
            eventPublisher.publishEvent(new UserLoginAuthenticationFailureBadCredentialsKyrieEvent(null, new BadCredentialsException("The provided credentials is wrong")));
        }
        return result;
    }

    private Set<GrantedAuthority> getAuthorities(AuthenticationResult result) {
        return result.getUser().getAuthorities().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }
}

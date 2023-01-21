package com.odeyalo.kyrie.core.events.authentication.support;

import lombok.Builder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;

/**
 * Simple {@link Authentication} implementation that used to store info about user that tries to authenticate.
 */
@Builder
public class AttemptedLoginAuthentication implements Authentication {
    private final String username;
    private final String password;
    private final Object details;
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * Create new AttemptedLoginAuthentication with username and password
     *
     * @param username - username provided by user/client
     * @param password - password provided by user/client
     */
    public AttemptedLoginAuthentication(String username, String password) {
        this.username = username;
        this.password = password;
        this.details = null;
        this.authorities = new HashSet<>();
    }

    /**
     * Create new AttemptedLoginAuthentication with username, password and authorities
     *
     * @param username    - username provided by user/client
     * @param password    - password provided by user/client
     * @param authorities - authorities associated with this attempt
     */
    public AttemptedLoginAuthentication(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.details = null;
        this.authorities = authorities;
    }

    /**
     * Create new AttemptedLoginAuthentication with username, password, details and authorities
     *
     * @param username    - username provided by user/client
     * @param password    - password provided by user/client
     * @param authorities - authorities associated with this attempt
     * @param principal   - user principal, will be used as details too
     */
    public AttemptedLoginAuthentication(String username, String password, Object principal, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.details = principal;
        this.authorities = authorities;
    }

    public static AttemptedLoginAuthentication of(String username, String password) {
        return new AttemptedLoginAuthentication(username, password);
    }

    public static AttemptedLoginAuthentication of(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        return new AttemptedLoginAuthentication(username, password, authorities);
    }

    public static AttemptedLoginAuthentication of(String username, String password, Object principal, Collection<? extends GrantedAuthority> authorities) {
        return new AttemptedLoginAuthentication(username, password, principal, authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return password;
    }

    @Override
    public Object getDetails() {
        return details;
    }

    @Override
    public Object getPrincipal() {
        return details;
    }

    /**
     * Since login is only attempted method will always return false
     *
     * @return - always false
     */
    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return null;
    }
}


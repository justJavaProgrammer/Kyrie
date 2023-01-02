package com.odeyalo.kyrie.core.authentication;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.oauth2.client.Oauth2Client;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Oauth2UserAuthenticationService implementation that store users in memory.
 * NOTE: InMemoryOauth2UserAuthenticationService should be used only for development
 * @see Oauth2User
 * @see Oauth2UserAuthenticationService
 */
public class InMemoryOauth2UserAuthenticationService implements Oauth2UserAuthenticationService {

    private final Map<String, Oauth2User> users;

    /**
     * Initialize repository with array of users
     * In this case as id will be used user's id property
     * @param users - array of users to register in authentication service
     */
    public InMemoryOauth2UserAuthenticationService(Oauth2User... users) {
        this(Arrays.asList(users));
    }

    /**
     * Initialize repository with existing users
     *
     * @param users - default users to set
     */
    public InMemoryOauth2UserAuthenticationService(Map<String, Oauth2User> users) {
        this.users = users;
    }

    /**
     * Initialize repository with list of users
     * In this case user's id will be used as id for 'users'
     * @param users - users to register in authentication service
     */
    public InMemoryOauth2UserAuthenticationService(@Nonnull List<Oauth2User> users) {
        this.users = users.stream().collect(Collectors.toMap(Oauth2User::getId, Function.identity()));
    }



    @Override
    public AuthenticationResult authenticate(Oauth2UserAuthenticationInfo info) {
        for (Oauth2User user : users.values()) {
            if (user.getUsername().equals(info.getUsername()) && user.getPassword().equals(info.getPassword())) {
                return AuthenticationResult.success(user);
            }
        }
        return AuthenticationResult.failed();
    }
}

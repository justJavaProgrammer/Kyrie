package com.odeyalo.kyrie.core.sso;

import com.odeyalo.kyrie.core.Oauth2User;
import io.jsonwebtoken.lang.Assert;
import lombok.Value;

import java.util.Collections;
import java.util.List;

/**
 * Container that used to store the logged and remembered user accounts per session.
 *
 * Commonly used for remember-me functions to store users in token or session
 */
@Value
public class RememberedLoggedUserAccountsContainer {
    private final List<Oauth2User> users;

    /**
     * Construct the container with specified list of the users
     * @param users - user accounts to remember
     */
    public RememberedLoggedUserAccountsContainer(List<Oauth2User> users) {
        Assert.notNull(users, "The users must be not null!");
        this.users = users;
    }

    /**
     * Create an empty RememberedLoggedUserAccountsContainer
     * @return - empty RememberedLoggedUserAccountsContainer
     */
    public static RememberedLoggedUserAccountsContainer empty() {
        return new RememberedLoggedUserAccountsContainer(Collections.emptyList());
    }

    public boolean isEmpty() {
        return users.isEmpty();
    }
}

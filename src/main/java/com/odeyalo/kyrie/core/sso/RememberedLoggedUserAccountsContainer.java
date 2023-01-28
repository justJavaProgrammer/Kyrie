package com.odeyalo.kyrie.core.sso;

import com.odeyalo.kyrie.core.Oauth2User;
import io.jsonwebtoken.lang.Assert;
import lombok.Value;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Container that used to store the logged and remembered user accounts per session.
 * <p>
 * Commonly used for remember-me functions to store users in token or session
 */
@Value
public class RememberedLoggedUserAccountsContainer {
    // Key - user id, value - user
    private final Map<String, Oauth2User> users;

    /**
     * Construct the container with specified list of the users
     *
     * @param users - user accounts to remember
     */
    public RememberedLoggedUserAccountsContainer(List<Oauth2User> users) {
        Assert.notNull(users, "The users must be not null!");
        this.users = users.stream().collect(Collectors.toMap(Oauth2User::getId, Function.identity()));
    }

    /**
     * Construct the container with specified Set of the users
     *
     * @param users - user accounts to remember
     */
    public RememberedLoggedUserAccountsContainer(Set<Oauth2User> users) {
        this.users = users.stream().collect(Collectors.toMap(Oauth2User::getId, Function.identity()));
    }

    /**
     * Construct the container with specified Map of the users
     *
     * @param users - user accounts to remember
     */
    public RememberedLoggedUserAccountsContainer(Map<String, Oauth2User> users) {
        this.users = users;
    }

    /**
     * Create an empty RememberedLoggedUserAccountsContainer
     *
     * @return - empty RememberedLoggedUserAccountsContainer
     */
    public static RememberedLoggedUserAccountsContainer empty() {
        return new RememberedLoggedUserAccountsContainer(Collections.emptyList());
    }


    public List<Oauth2User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public Map<String, Oauth2User> getUsersMap() {
        return users;
    }

    public boolean isEmpty() {
        return users.isEmpty();
    }

    /**
     * Add the user to the container, use user's id as key
     * @param user - user to store in container
     */
    public void add(Oauth2User user) {
        users.put(user.getId(), user);
    }

    /**
     * Add the user to the container
     * @param key - key that will be used to associate the value
     * @param user - user to store in container
     */
    public void add(String key, Oauth2User user) {
        users.put(key, user);
    }

    public Oauth2User get(String key) {
        return users.get(key);
    }

    public Oauth2User getFirst() {
        return users.values().stream().findFirst().orElse(null);
    }
}

package com.odeyalo.kyrie.core.support.condition;

import com.odeyalo.kyrie.core.Oauth2User;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface to determine should be user be remembered after authentication
 */
@FunctionalInterface
public interface RememberUserCondition {

    /**
     * Should be this user be remembered?
     * @param oauth2User - user that authenticated
     * @param request - current request
     * @return - true if the user MUST be remembered by Kyrie, false otherwise
     */
    boolean shouldRememberUser(Oauth2User oauth2User, HttpServletRequest request);

}

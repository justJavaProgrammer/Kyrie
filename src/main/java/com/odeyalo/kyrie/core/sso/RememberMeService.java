package com.odeyalo.kyrie.core.sso;

import com.odeyalo.kyrie.core.Oauth2User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interface that used to remember the current user and associate it with the session or other data to avoid credentials re-enter.
 */
public interface RememberMeService {

    /**
     * Attempt to login the user, by cookie or something else.
     * The method used to auto login multiple users
     * @param currentRequest - received request
     * @return - {@link RememberedLoggedUserAccountsContainer} with users or empty list. NEVER NULL
     */
    RememberedLoggedUserAccountsContainer autoLogin(HttpServletRequest currentRequest);

    /**
     * Remember the current user to avoid credentials re-enter
     * @param oauth2User - authenticated Oauth2User
     * @param currentRequest - current request
     * @param currentResponse - response to return to end-user
     */
    void rememberMe(Oauth2User oauth2User, HttpServletRequest currentRequest, HttpServletResponse currentResponse);

}

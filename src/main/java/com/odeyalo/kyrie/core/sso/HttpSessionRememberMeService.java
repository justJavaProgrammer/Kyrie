package com.odeyalo.kyrie.core.sso;

import com.odeyalo.kyrie.core.Oauth2User;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * {@link RememberMeService} implementation that uses HttpSession to store the users logged accounts
 */
@Component
public class HttpSessionRememberMeService implements RememberMeService {
    /**
     * Used as key to store the logged accounts in one session
     */
    public static final String LOGGED_ACCOUNTS_HTTP_SESSION_PROPERTY_NAME = "accounts";

    @Override
    public RememberedLoggedUserAccountsContainer autoLogin(HttpServletRequest currentRequest) {
        HttpSession session = currentRequest.getSession();
        if (session == null) {
            return null;
        }
        Object accounts = session.getAttribute(LOGGED_ACCOUNTS_HTTP_SESSION_PROPERTY_NAME);
        if (accounts == null) {
            return RememberedLoggedUserAccountsContainer.empty();
        }
        return ((RememberedLoggedUserAccountsContainer) accounts);
    }

    @Override
    public void rememberMe(Oauth2User oauth2User, HttpServletRequest currentRequest, HttpServletResponse currentResponse) {
        HttpSession session = currentRequest.getSession();
        session.setAttribute(LOGGED_ACCOUNTS_HTTP_SESSION_PROPERTY_NAME, new RememberedLoggedUserAccountsContainer(List.of(oauth2User)));
    }
}

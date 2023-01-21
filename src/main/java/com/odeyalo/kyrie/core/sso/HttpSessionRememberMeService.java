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

    @Override
    public RememberedLoggedUserAccountsContainer autoLogin(HttpServletRequest currentRequest) {
        HttpSession session = currentRequest.getSession();
        if (session == null) {
            return null;
        }
        Object accounts = session.getAttribute("accounts");
        if (accounts == null) {
            return RememberedLoggedUserAccountsContainer.empty();
        }
        return ((RememberedLoggedUserAccountsContainer) accounts);
    }

    @Override
    public void rememberMe(Oauth2User oauth2User, HttpServletRequest currentRequest, HttpServletResponse currentResponse) {
        HttpSession session = currentRequest.getSession(true);
        session.setAttribute("accounts", new RememberedLoggedUserAccountsContainer(List.of(oauth2User)));
    }
}

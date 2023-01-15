package com.odeyalo.kyrie.core.sso;

import com.odeyalo.kyrie.core.Oauth2User;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;


@Component
public class HttpSessionRememberMeService implements RememberMeService {

    @Override
    public List<Oauth2User> login(HttpServletRequest currentRequest) {
        HttpSession session = currentRequest.getSession();
        if (session == null) {
            return null;
        }
        Object accounts = session.getAttribute("accounts");
        if (accounts == null) {
            return Collections.emptyList();
        }
        return ((LoggedUsersHolder) accounts).getUsers();
    }

    @Override
    public void rememberMe(Oauth2User oauth2User, HttpServletRequest currentRequest, HttpServletResponse currentResponse) {
        HttpSession session = currentRequest.getSession(true);
        session.setAttribute("accounts", new LoggedUsersHolder(oauth2User));
    }

    @Getter
    private static class LoggedUsersHolder {
        private final List<Oauth2User> users;

        public LoggedUsersHolder(Oauth2User... user) {
            this.users = List.of(user);
        }
    }
}

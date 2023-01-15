package com.odeyalo.kyrie.core.sso;

import com.odeyalo.kyrie.core.Oauth2User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface RememberMeService {

    List<Oauth2User> login(HttpServletRequest currentRequest);

    void rememberMe(Oauth2User oauth2User, HttpServletRequest currentRequest, HttpServletResponse currentResponse);

}

package com.odeyalo.kyrie.core.events.listener.domain;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.events.authentication.UserLoginAuthenticationGrantedKyrieEvent;
import com.odeyalo.kyrie.core.events.listener.UserLoginAuthenticationGrantedKyrieEventListener;
import com.odeyalo.kyrie.core.sso.RememberMeService;
import com.odeyalo.kyrie.core.support.condition.RememberUserCondition;
import io.jsonwebtoken.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Domain Kyrie event listener, used to remember the user using {@link com.odeyalo.kyrie.core.sso.RememberMeService}
 * only if the {@link RememberUserCondition} returns true
 */
public class ConditionalRememberMeUserLoginAuthenticationGrantedKyrieEventListener implements UserLoginAuthenticationGrantedKyrieEventListener {
    private final RememberUserCondition rememberUserCondition;
    private final RememberMeService rememberMeService;
    private final Logger logger = LoggerFactory.getLogger(ConditionalRememberMeUserLoginAuthenticationGrantedKyrieEventListener.class);

    public ConditionalRememberMeUserLoginAuthenticationGrantedKyrieEventListener(RememberUserCondition rememberUserCondition, RememberMeService rememberMeService) {
        this.rememberUserCondition = rememberUserCondition;
        this.rememberMeService = rememberMeService;
    }

    /**
     * Remember the user if and only if the {@link RememberUserCondition#shouldRememberUser(Oauth2User, HttpServletRequest)} returns true, do nothing in other case
     * @param event - published event
     */
    @Override
    public void onEvent(UserLoginAuthenticationGrantedKyrieEvent event) {
        this.logger.debug("Received {}", event);

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        Assert.state(requestAttributes != null, "The ConditionalRememberMeUserLoginAuthenticationGrantedKyrieEventListener works only with web interfaces and requires ServletRequestAttributes");

        HttpServletRequest request = requestAttributes.getRequest();
        HttpServletResponse response = requestAttributes.getResponse();
        Oauth2User oauth2User = event.getOauth2User();

        if (rememberUserCondition.shouldRememberUser(oauth2User, request)) {
            this.logger.debug("The RememberUserCondition returns true");
            rememberMeService.rememberMe(oauth2User, request, response);
        }
    }
}

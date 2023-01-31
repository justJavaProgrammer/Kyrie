package com.odeyalo.kyrie.core.oauth2.support.grant;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authentication.Oauth2UserAuthenticationService;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.oauth2.flow.support.RedirectableOauth2FlowHandlerFacade;
import com.odeyalo.kyrie.core.sso.RememberMeService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Default {@link AbstractRedirectableAuthenticationGrantHandlerFacade} implementation, that authenticate the user and redirect user to client application WITHOUT consent page
 *
 */
//@Component
public class DefaultRedirectableAuthenticationGrantHandlerFacade extends AbstractRedirectableAuthenticationGrantHandlerFacade {
    private final RedirectableOauth2FlowHandlerFacade redirectableOauth2FlowHandlerFacade;
//todo: Rewrite to Java config
    @Autowired
    private RememberMeService rememberMeService;

    public DefaultRedirectableAuthenticationGrantHandlerFacade(Oauth2UserAuthenticationService oauth2UserAuthenticationService, RedirectableOauth2FlowHandlerFacade redirectableOauth2FlowHandlerFacade){
        super(oauth2UserAuthenticationService);
        this.redirectableOauth2FlowHandlerFacade = redirectableOauth2FlowHandlerFacade;
    }

    @Override
    protected HandleResult doHandleGrant(Oauth2User user, AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            String redirectUri = redirectableOauth2FlowHandlerFacade.handleFlow(user, authorizationRequest);
            rememberMeService.rememberMe(user, request, response);
            return HandleResult.success(true, redirectUri);
        } catch (Exception ex) {
            logger.error("Failed to create redirect uri", ex);
            return HandleResult.failed(true, "REDIRECT_URI_CREATION_ERROR_TYPE_TYPE");
        }
    }
}

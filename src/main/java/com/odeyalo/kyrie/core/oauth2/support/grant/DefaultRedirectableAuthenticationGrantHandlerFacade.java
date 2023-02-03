package com.odeyalo.kyrie.core.oauth2.support.grant;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authentication.Oauth2UserAuthenticationService;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.oauth2.flow.support.RedirectableOauth2FlowHandlerFacade;
import com.odeyalo.kyrie.core.sso.RememberMeService;
import com.odeyalo.kyrie.exceptions.Oauth2ErrorType;
import com.odeyalo.kyrie.exceptions.Oauth2Exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Default {@link AbstractRedirectableAuthenticationGrantHandlerFacade} implementation, that authenticate the user and redirect user to client application WITHOUT consent page
 *
 */
public class DefaultRedirectableAuthenticationGrantHandlerFacade extends AbstractRedirectableAuthenticationGrantHandlerFacade {
    private final RedirectableOauth2FlowHandlerFacade redirectableOauth2FlowHandlerFacade;
    private final RememberMeService rememberMeService;

    public DefaultRedirectableAuthenticationGrantHandlerFacade(Oauth2UserAuthenticationService oauth2UserAuthenticationService,
                                                               RedirectableOauth2FlowHandlerFacade redirectableOauth2FlowHandlerFacade,
                                                               RememberMeService rememberMeService){
        super(oauth2UserAuthenticationService);
        this.redirectableOauth2FlowHandlerFacade = redirectableOauth2FlowHandlerFacade;
        this.rememberMeService = rememberMeService;
    }

    @Override
    protected HandleResult doHandleGrant(Oauth2User user, AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            String redirectUri = redirectableOauth2FlowHandlerFacade.handleFlow(user, authorizationRequest);
            rememberMeService.rememberMe(user, request, response);
            return HandleResult.success(true, redirectUri);
        } catch (Oauth2Exception ex) {
            logger.error("Failed to create redirect uri", ex);
            Oauth2ErrorType errorType = ex.getErrorType();

            if (Oauth2ErrorType.INVALID_GRANT.equals(errorType)) {
                return HandleResult.UNSUPPORTED_GRANT_TYPE_HANDLE_RESULT;
            }
            return HandleResult.failed(true, errorType.getErrorName());
        }
    }
}

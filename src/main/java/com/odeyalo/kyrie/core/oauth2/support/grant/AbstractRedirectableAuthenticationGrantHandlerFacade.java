package com.odeyalo.kyrie.core.oauth2.support.grant;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authentication.AuthenticationResult;
import com.odeyalo.kyrie.core.authentication.Oauth2UserAuthenticationInfo;
import com.odeyalo.kyrie.core.authentication.Oauth2UserAuthenticationService;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.authorization.support.AuthorizationRequestContextHolder;
import com.odeyalo.kyrie.core.events.AuthorizationRequestProcessingFinishedKyrieEvent;
import com.odeyalo.kyrie.core.events.KyrieEventPublisher;
import com.odeyalo.kyrie.core.oauth2.support.callbacks.AuthenticationFailedCallback;
import com.odeyalo.kyrie.core.oauth2.support.callbacks.SuccessfulAuthenticationCallback;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Abstract base class that used to authenticate the user and add callbacks before, after authentication and after all.
 */
public abstract class AbstractRedirectableAuthenticationGrantHandlerFacade implements RedirectableAuthenticationGrantHandlerFacade {
    protected final Oauth2UserAuthenticationService oauth2UserAuthenticationService;
    protected final KyrieEventPublisher publisher;
    protected final Logger logger = LoggerFactory.getLogger(AbstractRedirectableAuthenticationGrantHandlerFacade.class);

    protected AbstractRedirectableAuthenticationGrantHandlerFacade(Oauth2UserAuthenticationService oauth2UserAuthenticationService,
                                                                   KyrieEventPublisher publisher) {
        this.oauth2UserAuthenticationService = oauth2UserAuthenticationService;
        this.publisher = publisher;
    }

    protected abstract HandleResult doHandleGrant(Oauth2User user, AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response);

    /**
     * Authenticate the user using {@link Oauth2UserAuthenticationService}
     * and delegate all the job to {@link #doHandleGrant(Oauth2User, AuthorizationRequest, HttpServletRequest, HttpServletResponse)},
     * Trigger the callbacks based on authentication result.
     *
     * @param authenticationInfo - provided credentials by user
     * @param authorizationRequest - current AuthorizationRequest
     * @param request - current http request
     * @param response - response associated with this request
     * @return - result from {{@link #doHandleGrant(Oauth2User, AuthorizationRequest, HttpServletRequest, HttpServletResponse)}}
     *
     * @see #handleAuthenticationSuccessCallback(SuccessfulAuthenticationCallback.SuccessfulAuthenticationCallbackData)
     * @see #handleAuthenticationFailedCallback(AuthenticationFailedCallback.AuthenticationFailedCallbackData)
     */
    @Override
    public HandleResult handleGrant(Oauth2UserAuthenticationInfo authenticationInfo, AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        AuthenticationResult result = oauth2UserAuthenticationService.authenticate(authenticationInfo);

        if (result == null || !result.isSuccess()) {
            handleAuthenticationFailedCallback(new AuthenticationFailedCallback.AuthenticationFailedCallbackData("Credentials are wrong or user does not exist"));
            return HandleResult.WRONG_USER_CREDENTIALS_HANDLE_RESULT;
        }

        Oauth2User user = result.getUser();

        handleAuthenticationSuccessCallback(new SuccessfulAuthenticationCallback.SuccessfulAuthenticationCallbackData(user));

        HandleResult handleResult = doHandleGrant(user, authorizationRequest, request, response);

        afterAllSuccess(new AfterAllSuccessCallbackData(handleResult.getRedirectUri(), user));


        publishEventOnSessionClose(handleResult);

        return handleResult;
    }

    public void handleAuthenticationSuccessCallback(SuccessfulAuthenticationCallback.SuccessfulAuthenticationCallbackData data) {

    }

    public void handleAuthenticationFailedCallback(AuthenticationFailedCallback.AuthenticationFailedCallbackData data) {

    }

    void afterAllSuccess(AfterAllSuccessCallbackData data) {

    }

    protected void publishEventOnSessionClose(HandleResult result) {
        if (result.shouldCloseSession()) {
            AuthorizationRequest request = AuthorizationRequestContextHolder.getContext().getRequest();
            publisher.publishEvent(new AuthorizationRequestProcessingFinishedKyrieEvent(request));
        }
    }

    @Data
    @AllArgsConstructor
    protected static class AfterAllSuccessCallbackData {
        // Generated redirect uri
        private String redirectUri;
        // Authenticated user
        private Oauth2User user;
    }
}

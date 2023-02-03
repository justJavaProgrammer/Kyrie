package com.odeyalo.kyrie.core.oauth2.support.consent;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Used to return the consent page and handle submission from consent
 */
public interface ConsentPageHandler {
    /**
     * Returns ready-to-use consent page.
     *
     * Note: The current user should be cached inside the session or other store to be aware about user in other requests,
     * that will be used lately in {@link #handleSubmit(AuthorizationRequest, HttpServletRequest, HttpServletResponse)}
     *
     * @param user - user that already authenticated but requires consent
     * @param authorizationRequest - authorization request associated with this consent page
     * @param request - current http request
     * @return - consent page
     */
    ModelAndView getConsentPage(Oauth2User user, AuthorizationRequest authorizationRequest, HttpServletRequest request);

    /**
     * Handle the submit of form, etc from consent page
     * @param authorizationRequest - authorization request associated with this consent page
     * @param request - current request
     * @param response - response associated with this request
     */
    void handleSubmit(AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response);

}

package com.odeyalo.kyrie.core.oauth2.support.consent;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import io.jsonwebtoken.lang.Assert;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Abstract ConsentPageHandler implementation that adds the additional methods to simplify method implementing for the child classes.
 */
public abstract class AbstractConsentPageHandler implements ConsentPageHandler {
    public static final String AUTHENTICATED_USER_ATTRIBUTE_NAME = "authenticated_user";

    @Override
    public void handleSubmit(AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        ConsentResult consentResult = isApproved(authorizationRequest, request);
        Oauth2User user = consentResult.getUser();

        if (consentResult.isApproved()) {
            onAccessApproved(user, authorizationRequest, request, response);
            return;
        }
        onAccessDenied(user, authorizationRequest, request, response);
    }

    /**
     * Method to determine if the user granted access
     * @param authorizationRequest - AuthorizationRequest associated with current authentication session
     * @param request - current request
     * @return - {@link ConsentResult#approved(Oauth2User)} if the user approved access for the client application, {@link ConsentResult#denied(Oauth2User)} in any other case
     */
    abstract ConsentResult isApproved(AuthorizationRequest authorizationRequest, HttpServletRequest request);

    /**
     * Will be invoked if user approved the access for the client application
     *
     * @param user - user that approved access
     * @param authorizationRequest - authorization request associated with this consent page
     * @param request              - current request
     * @param response             - response associated with this request
     */
    abstract void onAccessApproved(Oauth2User user, AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response);

    /**
     * Will be invoked if user denied access grant to the client application
     *
     * @param user - user that denied access
     * @param authorizationRequest - authorization request associated with this consent page
     * @param request              - current request
     * @param response             - response associated with this request
     */
    abstract void onAccessDenied(Oauth2User user, AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response);


    /**
     * Helper class that returns the result of the consent received from user
     */
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    protected static class ConsentResult {
        private final boolean isApproved;
        // User that granted access, the value MUST BE presented only if isApproved is true
        private final Oauth2User user;

        /**
         * The method should be called when the used approved the consent
         * @param user - user that approved the consent, never null
         * @return - approved ConsentResult
         */
        public static ConsentResult approved(Oauth2User user) {
            Assert.notNull(user, "The ConsentResult.approved() requires non-null user as parameter!");
            return new ConsentResult(true, user);
        }

        /**
         * The method should be called when the user denied the consent
         * @param user - user that denied the consent, never null
         * @return - denied ConsentResult
         */
        public static ConsentResult denied(Oauth2User user) {
            Assert.notNull(user, "The ConsentResult.denied() requires non-null user as parameter!");
            return new ConsentResult(false, user);
        }
    }
}

package com.odeyalo.kyrie.core.oauth2.support.grant;

import com.odeyalo.kyrie.core.authentication.Oauth2UserAuthenticationInfo;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Facade interface that used to authenticate the user and handle the grant after successful authentication.
 * The interface supports only redirect-based flows.
 */
public interface RedirectableAuthenticationGrantHandlerFacade {

    /**
     * Authenticate the user and handle grant if authentication was successful
     * @param authenticationInfo - provided credentials by user
     * @param authorizationRequest - current AuthorizationRequest
     * @param request - current http request
     * @param response - response associated with this request
     * @return = HandleResult that contains all required info about result
     */
    HandleResult handleGrant(Oauth2UserAuthenticationInfo authenticationInfo, AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response);

    // Todo: maybe create callbacks for authentication callback
//    default void successLoginCallback() {
//
//    }

    @RequiredArgsConstructor
    @AllArgsConstructor
    @Data
    class HandleResult {
        public static final String WRONG_USER_CREDENTIALS_ERROR_TYPE_NAME = "WRONG_USER_CREDENTIALS_ERROR";
        public static final String UNSUPPORTED_GRANT_TYPE_ERROR_TYPE_NAME = "UNSUPPORTED_GRANT_TYPE_ERROR";
        // Possible failed results that can be occurred
        public static final HandleResult WRONG_USER_CREDENTIALS_HANDLE_RESULT = failed(true, WRONG_USER_CREDENTIALS_ERROR_TYPE_NAME);
        public static final HandleResult UNSUPPORTED_GRANT_TYPE_HANDLE_RESULT = failed(true, UNSUPPORTED_GRANT_TYPE_ERROR_TYPE_NAME);

        private final boolean isSuccess;
        // True if session MUST BE closed after grant handling
        private final boolean shouldCloseSession;
        private String redirectUri;
        private String errorType;

        private HandleResult(boolean isSuccess, boolean shouldCloseSession, String redirectUri) {
            this.isSuccess = isSuccess;
            this.shouldCloseSession = shouldCloseSession;
            this.redirectUri = redirectUri;
        }

        public static HandleResult success(boolean shouldCloseSession, String redirectUri) {
            return new HandleResult(true, shouldCloseSession, redirectUri);
        }

        public static HandleResult failed(boolean shouldCloseSession, String errorType) {
            return new HandleResult(false, shouldCloseSession, errorType);
        }
    }
}


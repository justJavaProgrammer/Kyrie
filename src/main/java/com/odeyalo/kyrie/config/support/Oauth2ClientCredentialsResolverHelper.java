package com.odeyalo.kyrie.config.support;

import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;

import javax.servlet.http.HttpServletRequest;

/**
 * Helper class that used to resolve {@link Oauth2ClientCredentials} by HttpServletRequest.
 *
 * @see Oauth2ClientCredentials
 */
public interface Oauth2ClientCredentialsResolverHelper {
    /**
     * Flag method that indicate if implementation resolve {@link Oauth2ClientCredentials} from request.
     * @param request - current request
     * @return - true if implementation can resolve {@link Oauth2ClientCredentials}, false otherwise
     */
    boolean canBeResolved(HttpServletRequest request);

    /**
     * Method to resolve {@link Oauth2ClientCredentials} by {@link HttpServletRequest} data.
     * @param request - current request
     * @return - resolved Oauth2ClientCredentials, null otherwise.
     */
    Oauth2ClientCredentials resolveCredentials(HttpServletRequest request);

}

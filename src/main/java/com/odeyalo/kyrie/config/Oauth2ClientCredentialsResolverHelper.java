package com.odeyalo.kyrie.config;

import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;

import javax.servlet.http.HttpServletRequest;

/**
 * Used to resolve client credentials from http request
 */
public interface Oauth2ClientCredentialsResolverHelper {

    /**
     * Resolve client credentials from request.
     *
     * <p>
     * If requireClientSecret parameter set to false, then Oauth2ClientCredentials should only contain clientId,
     * if client_secret does not presented in request
     * </p>
     *
     * @param request             - current request
     * @param requireClientSecret - true if request must contain client secret, false otherwise
     * @return - resolved Oauth2ClientCredentials from request
     */
    Oauth2ClientCredentials resolveCredentials(HttpServletRequest request, boolean requireClientSecret);

}

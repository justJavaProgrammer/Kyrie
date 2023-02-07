package com.odeyalo.kyrie.config.support;

import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;
import com.odeyalo.kyrie.core.oauth2.support.Oauth2Constants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * {@link Oauth2ClientCredentialsResolverHelper} implementation that resolves {@link Oauth2ClientCredentials} from request parameters
 */
@Component
public class RequestParametersOauth2ClientCredentialsResolverHelper implements Oauth2ClientCredentialsResolverHelper {

    /**
     * Return true only if the request parameters contain client_id
     * @param request - current request
     * @return true only if the request parameters contain client_id, false otherwise
     */
    @Override
    public boolean canBeResolved(HttpServletRequest request) {
        return request.getParameter(Oauth2Constants.CLIENT_ID) != null;
    }

    @Override
    public Oauth2ClientCredentials resolveCredentials(HttpServletRequest request) {
        return parseOauth2ClientCredentialsByParameters(request);
    }

    /**
     * Used to resolve Oauth2ClientCredentials by request parameter that was provided in request.
     * @param request - request with parameters
     * @return - Oauth2ClientCredentials that was resolved from request parameters
     */
    private Oauth2ClientCredentials parseOauth2ClientCredentialsByParameters(HttpServletRequest request) {
        String clientId = request.getParameter(Oauth2Constants.CLIENT_ID);
        String clientSecret = request.getParameter(Oauth2Constants.CLIENT_SECRET);
        return Oauth2ClientCredentials.of(clientId, clientSecret);
    }
}

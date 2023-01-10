package com.odeyalo.kyrie.config;

import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

/**
 * {@link Oauth2ClientCredentialsResolverHelper} implementation that supports Oauth2 client authentication
 * through Basic Authentication and request parameters.
 * <p>The parameters that will be used to perform authentication</p>
 * <ul>
 *     <li>client_id</li>
 *     <li>client_secret</li>
 * </ul>
 */
public class DefaultOauth2ClientCredentialsResolverHelper implements Oauth2ClientCredentialsResolverHelper {
    public static final String BASIC_AUTHENTICATION_PREFIX = "Basic ";

    @Override
    public Oauth2ClientCredentials resolveCredentials(HttpServletRequest request, boolean requireClientSecret) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        Oauth2ClientCredentials clientCredentials;

        if (authHeader != null) {
            clientCredentials = parseBasicAuthentication(authHeader);
        } else {
            clientCredentials = getOauth2ClientCredentialsByParameters(request);
        }

        return clientCredentials;
    }

    /**
     * Using to resolve Oauth2ClientCredentials by request parameter that was provided in request.
     * @param request - request with parameters
     * @return - Oauth2ClientCredentials that was resolved from request parameters
     */
    private Oauth2ClientCredentials getOauth2ClientCredentialsByParameters(HttpServletRequest request) {
        String clientId = request.getParameter("client_id");
        String clientSecret = request.getParameter("client_secret");

        return Oauth2ClientCredentials.of(clientId, clientSecret);
    }

    /**
     * Using to resolve Oauth2ClientCredentials by 'Authorization' HTTP Header using Basic Authentication
     * @param authHeader - 'Authorization' header from request
     * @return - resolved {@link Oauth2ClientCredentials} or null if Authorization header contain not Basic-Authentication type of authentication.
     */
    private Oauth2ClientCredentials parseBasicAuthentication(String authHeader) {
        if (!authHeader.startsWith(BASIC_AUTHENTICATION_PREFIX)) {
            return null;
        }
        String basicHeaderValue = authHeader.substring(BASIC_AUTHENTICATION_PREFIX.length());
        String decodedCredentials = new String(Base64.getDecoder().decode(basicHeaderValue));
        String[] usernamePassword = decodedCredentials.split(":");
        if (usernamePassword.length != 2) {
            return null;
        }
        String clientId = usernamePassword[0];
        String clientSecret = usernamePassword[1];
        return Oauth2ClientCredentials.of(clientId, clientSecret);
    }
}

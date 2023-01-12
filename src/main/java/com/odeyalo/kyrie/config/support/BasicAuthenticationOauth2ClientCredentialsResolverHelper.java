package com.odeyalo.kyrie.config.support;

import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

/**
 * {@link Oauth2ClientCredentialsResolverHelper} implementation that resolves client authentication only through Basic Authentication Schema
 */
@Component
public class BasicAuthenticationOauth2ClientCredentialsResolverHelper implements Oauth2ClientCredentialsResolverHelper {
    public static final String BASIC_AUTHENTICATION_PREFIX = "Basic ";


    @Override
    public boolean canBeResolved(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        return authHeader != null && authHeader.startsWith(BASIC_AUTHENTICATION_PREFIX);
    }

    @Override
    public Oauth2ClientCredentials resolveCredentials(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null) {
            return null;
        }
        return parseBasicAuthentication(authHeader);
    }

    /**
     * Used to resolve Oauth2ClientCredentials by 'Authorization' HTTP Header using Basic Authentication
     * @param authHeader - 'Authorization' header from request
     * @return - resolved {@link Oauth2ClientCredentials} or null if Authorization header contain not Basic-Authentication type of authentication.
     */
    private Oauth2ClientCredentials parseBasicAuthentication(String authHeader) {
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

package com.odeyalo.kyrie.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;
import com.odeyalo.kyrie.core.oauth2.support.Oauth2Constants;
import lombok.Data;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

/**
 * {@link Oauth2ClientCredentialsResolverHelper} implementation that supports Oauth2 client authentication
 * through Basic Authentication, request parameters and JSON body.
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
        Map<String, String[]> parameters = request.getParameterMap();
        if (authHeader != null) {
            clientCredentials = parseBasicAuthentication(authHeader);
        } else if (parameters != null) {
            clientCredentials = getOauth2ClientCredentialsByParameters(request);
        } else {
            String body = getBody(request);
            clientCredentials = parseOauth2ClientCredentialsByBody(body);
        }

        return clientCredentials;
    }

    /**
     * Used to resolve Oauth2ClientCredentials by request parameter that was provided in request.
     * @param request - request with parameters
     * @return - Oauth2ClientCredentials that was resolved from request parameters
     */
    private Oauth2ClientCredentials getOauth2ClientCredentialsByParameters(HttpServletRequest request) {
        String clientId = request.getParameter("client_id");
        String clientSecret = request.getParameter("client_secret");

        return Oauth2ClientCredentials.of(clientId, clientSecret);
    }

    /**
     * Used to resolve Oauth2ClientCredentials by 'Authorization' HTTP Header using Basic Authentication
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

    /**
     * Used to resolve {@link Oauth2ClientCredentials} using JSON.
     * @param body - JSON body
     * @return - Oauth2ClientCredentials resolved from body, null otherwise, if body is malformed
     *
     * @see Oauth2ClientCredentials
     */
    private Oauth2ClientCredentials parseOauth2ClientCredentialsByBody(String body) {
        GenericClientCredentials credentials = convertBodyToCredentials(body);
        if (credentials == null) {
            return null;
        }
        return Oauth2ClientCredentials.of(credentials.getClientId(), credentials.getClientSecret());
    }

    /**
     * Convert JSON body to {@link GenericClientCredentials}
     * @param body - JSON body
     * @return - GenericClientCredentials resolved from body, null otherwise
     */
    private GenericClientCredentials convertBodyToCredentials(String body)  {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(body, GenericClientCredentials.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns body from request
     * @param request - current request
     * @return - body from request, null otherwise
     */
    private String getBody(HttpServletRequest request) {
        try {
            return request.getReader().lines().reduce("", String::concat);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Support inner class that used to map client id and client secret to Java Object from JSON
     */
    @Data
    static class GenericClientCredentials {
        @JsonProperty(Oauth2Constants.CLIENT_ID)
        private String clientId;
        @JsonProperty(Oauth2Constants.CLIENT_SECRET)
        private String clientSecret;
    }
}

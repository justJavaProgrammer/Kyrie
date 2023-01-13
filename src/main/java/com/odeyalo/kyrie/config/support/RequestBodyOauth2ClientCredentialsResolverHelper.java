package com.odeyalo.kyrie.config.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;
import com.odeyalo.kyrie.core.oauth2.support.Oauth2Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * {@link Oauth2ClientCredentialsResolverHelper} implementation that parses ONLY request body and resolves client credentials only from it.
 */
@Component
public class RequestBodyOauth2ClientCredentialsResolverHelper implements Oauth2ClientCredentialsResolverHelper {
    private final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public boolean canBeResolved(HttpServletRequest request) {
        String body = getBody(request);
        return MediaType.APPLICATION_JSON_VALUE.equals(request.getContentType()) && StringUtils.hasLength(body);
    }

    @Override
    public Oauth2ClientCredentials resolveCredentials(HttpServletRequest request) {
        String body = getBody(request);
        return parseOauth2ClientCredentialsByBody(body);
    }

    /**
     * Used to resolve {@link Oauth2ClientCredentials} using JSON.
     *
     * @param body - JSON body
     * @return - Oauth2ClientCredentials resolved from body, null otherwise, if body is malformed
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
     *
     * @param body - JSON body
     * @return - GenericClientCredentials resolved from body, null otherwise
     */
    private GenericClientCredentials convertBodyToCredentials(String body) {
        try {
            return mapper.readValue(body, GenericClientCredentials.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns body from request
     *
     * @param request - current request
     * @return - body from request, null otherwise
     */
    private String getBody(HttpServletRequest request) {
        try {
            return request.getReader().lines().collect(Collectors.joining());
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Support inner class that used to map client id and client secret to Java Object from JSON
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class GenericClientCredentials {
        @JsonProperty(Oauth2Constants.CLIENT_ID)
        private String clientId;
        @JsonProperty(Oauth2Constants.CLIENT_SECRET)
        private String clientSecret;
    }
}

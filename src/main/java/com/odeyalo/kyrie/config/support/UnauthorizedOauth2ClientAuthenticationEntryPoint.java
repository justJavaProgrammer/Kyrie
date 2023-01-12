package com.odeyalo.kyrie.config.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odeyalo.kyrie.exceptions.Oauth2ErrorType;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.odeyalo.kyrie.core.oauth2.support.Oauth2Constants.ERROR_DESCRIPTION_PARAMETER_NAME;
import static com.odeyalo.kyrie.core.oauth2.support.Oauth2Constants.ERROR_PARAMETER_NAME;

/**
 * Custom {@link AuthenticationEntryPoint} that used to return HTTP 401 to client, if client is not authorized or client credentials are wrong.
 *
 * @see AuthenticationEntryPoint
 * @see com.odeyalo.kyrie.config.configuration.KyrieOauth2ServerWebSecurityConfiguration
 */
public class UnauthorizedOauth2ClientAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final String UNAUTHORIZED_CLIENT_DESCRIPTION_VALUE = "The client is unauthorized. " +
            "To avoid the error add correct client_id and client secret to request parameters or in 'Authorization' Header using Basic Authentication Schema";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", 401);
        body.put("timestamp", System.currentTimeMillis());

        body.put(ERROR_PARAMETER_NAME, Oauth2ErrorType.INVALID_CLIENT.getErrorName());
        body.put(ERROR_DESCRIPTION_PARAMETER_NAME, UNAUTHORIZED_CLIENT_DESCRIPTION_VALUE);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(401);

        ObjectMapper mapper = new ObjectMapper();

        mapper.writeValue(response.getWriter(), body);
    }
}

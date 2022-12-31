package com.odeyalo.kyrie.config;

import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;
import com.odeyalo.kyrie.core.oauth2.client.ClientCredentialsValidator;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

/**
 * <p>Filter to verify the Oauth2 Client credentials.
 * The Oauth2ClientValidationFilter supports Basic authentication and authentication using request parameter.</p>
 * <ul>
 *     <li>Basic authentication - as described in <a href="https://www.rfc-editor.org/rfc/rfc7617">RFC-7617 The 'Basic' HTTP Authentication Scheme</a></li>
 *     <li>
 *         Authentication using request parameters - is used when no Authorization header is presented.
 *         <p>Required list of request parameters that must be presented if Authorization is not presented:</p>
 *         <ul>
 *             <li>client_id - is used to determine what client is trying to authenticate</li>
 *             <li>client_secret - to confirm that client credentials is correct</li>
 *         </ul>
 *     </li>
 * </ul>
 * <p>The filter is using to validate ONLY oauth2 clients using Oauth2 Specification and DOES NOT support other types of clients</p>
 * @see <a href="https://www.oauth.com/oauth2-servers/access-tokens/client-credentials/">Client credentials</a>
 * @see ClientCredentialsValidator
 * @see OncePerRequestFilter
 * @version 1.0
 */
public class Oauth2ClientValidationFilter extends OncePerRequestFilter {
    public static final String BASIC_AUTHENTICATION_PREFIX = "Basic ";
    private final ClientCredentialsValidator clientCredentialsValidator;

    public Oauth2ClientValidationFilter(ClientCredentialsValidator clientCredentialsValidator) {
        this.clientCredentialsValidator = clientCredentialsValidator;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        Oauth2ClientCredentials clientCredentials;
        if (authHeader == null) {
            clientCredentials = getOauth2ClientCredentialsByParameters(request);
        } else {
            clientCredentials = parseBasicAuthentication(authHeader);
        }
        if (clientCredentials != null) {
            clientCredentialsValidator.validateCredentials(clientCredentials);
        }
        filterChain.doFilter(request, response);
    }

    private Oauth2ClientCredentials getOauth2ClientCredentialsByParameters(HttpServletRequest request) {
        Oauth2ClientCredentials clientCredentials;
        String clientId = request.getParameter("client_id");
        String clientSecret = request.getParameter("client_secret");
        clientCredentials = Oauth2ClientCredentials.of(clientId, clientSecret);
        return clientCredentials;
    }


    private Oauth2ClientCredentials parseBasicAuthentication(String authHeader) {
        if (!authHeader.startsWith(BASIC_AUTHENTICATION_PREFIX)) {
            return null;
        }
        String basicHeaderValue = authHeader.substring(BASIC_AUTHENTICATION_PREFIX.length());
        String creds = new String(Base64.getDecoder().decode(basicHeaderValue));
        String[] usernamePassword = creds.split(":");
        if (usernamePassword.length != 2) {
            return null;
        }
        String clientId = usernamePassword[0];
        String clientSecret = usernamePassword[1];
        return Oauth2ClientCredentials.of(clientId, clientSecret);
    }
}

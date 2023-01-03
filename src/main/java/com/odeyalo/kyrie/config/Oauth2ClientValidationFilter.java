package com.odeyalo.kyrie.config;

import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;
import com.odeyalo.kyrie.core.oauth2.client.ClientCredentialsValidator;
import com.odeyalo.kyrie.core.oauth2.client.Oauth2Client;
import com.odeyalo.kyrie.core.oauth2.client.Oauth2ClientRepository;
import com.odeyalo.kyrie.core.support.ValidationResult;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
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
    private final Oauth2ClientRepository oauth2ClientRepository;

    public Oauth2ClientValidationFilter(ClientCredentialsValidator clientCredentialsValidator, Oauth2ClientRepository oauth2ClientRepository) {
        this.clientCredentialsValidator = clientCredentialsValidator;
        this.oauth2ClientRepository = oauth2ClientRepository;
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
        // If credentials are null then filter check is failed and other checks are useless
        if (clientCredentials == null) {
            filterChain.doFilter(request, response);
            return;
        }

        ValidationResult validationResult = clientCredentialsValidator.validateCredentials(clientCredentials);

        if (validationResult.isSuccess()) {
            Oauth2Client client = oauth2ClientRepository.findOauth2ClientById(clientCredentials.getClientId());
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(client, client.getPassword(), client.getAuthorities()));
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Using to resolve Oauth2ClientCredentials by request parameter that was provided in request.
     * @param request - request with parameters
     * @return - Oauth2ClientCredentials that was resolved from request parameters
     * or null if request does not contain client_id or client_secret parameters
     */
    private Oauth2ClientCredentials getOauth2ClientCredentialsByParameters(HttpServletRequest request) {
        String clientId = request.getParameter("client_id");
        String clientSecret = request.getParameter("client_secret");
        if (clientId == null || clientSecret == null) {
            return null;
        }
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

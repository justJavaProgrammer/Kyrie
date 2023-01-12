package com.odeyalo.kyrie.config;

import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;
import com.odeyalo.kyrie.core.oauth2.client.ClientCredentialsValidator;
import com.odeyalo.kyrie.core.oauth2.client.Oauth2Client;
import com.odeyalo.kyrie.core.oauth2.client.Oauth2ClientRepository;
import com.odeyalo.kyrie.core.support.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>Filter to verify the Oauth2 Client credentials.
 * The Oauth2ClientValidationFilter supports Basic authentication, authentication using request parameter and Authorization using JSON body.</p>
 * <ul>
 *     <li>Basic authentication - as described in <a href="https://www.rfc-editor.org/rfc/rfc7617">RFC-7617 The 'Basic' HTTP Authentication Scheme</a></li>
 *     <li>
 *         Authentication using request parameters - is used when no Authorization header is presented.
 *         <p>Required list of request parameters that must be presented if Authorization is not presented:</p>
 *         <ul>
 *             <li>client_id - is used to determine what client is trying to authenticate</li>
 *             <li>client_secret - to confirm that client credentials are correct</li>
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
    private final ClientCredentialsValidator clientCredentialsValidator;
    private final Oauth2ClientRepository oauth2ClientRepository;
    private final Oauth2ClientCredentialsResolverHelper clientCredentialsResolverHelper;
    private final Logger logger = LoggerFactory.getLogger(Oauth2ClientValidationFilter.class);

    public Oauth2ClientValidationFilter(ClientCredentialsValidator clientCredentialsValidator, Oauth2ClientRepository oauth2ClientRepository, Oauth2ClientCredentialsResolverHelper clientCredentialsResolverHelper) {
        this.clientCredentialsValidator = clientCredentialsValidator;
        this.oauth2ClientRepository = oauth2ClientRepository;
        this.clientCredentialsResolverHelper = clientCredentialsResolverHelper;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        Oauth2ClientCredentials clientCredentials = clientCredentialsResolverHelper.resolveCredentials(request, false);

        // If credentials are null then filter check is failed and other checks are useless
        if (clientCredentials == null) {
            filterChain.doFilter(request, response);
            return;
        }
        Oauth2Client client = oauth2ClientRepository.findOauth2ClientById(clientCredentials.getClientId());

        if (client == null || (client.getClientType() == Oauth2Client.ClientType.CONFIDENTIAL && clientCredentials.getClientSecret() == null)) {
            filterChain.doFilter(request, response);
            return;
        }

        ValidationResult validationResult = clientCredentialsValidator.validateCredentials(clientCredentials);

        if (validationResult.isSuccess()) {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(client, client.getPassword(), client.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            this.logger.debug("Set authentication: {}", authentication);
        }

        filterChain.doFilter(request, response);
    }
}

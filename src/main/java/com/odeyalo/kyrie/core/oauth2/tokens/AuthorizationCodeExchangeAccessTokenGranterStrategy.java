package com.odeyalo.kyrie.core.oauth2.tokens;

import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;
import com.odeyalo.kyrie.core.oauth2.client.Oauth2Client;
import com.odeyalo.kyrie.exceptions.InvalidClientCredentialsException;
import com.odeyalo.kyrie.exceptions.InvalidGrantOauth2Exception;
import com.odeyalo.kyrie.exceptions.Oauth2ErrorType;
import com.odeyalo.kyrie.exceptions.Oauth2Exception;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * <p> {@link AccessTokenGranterStrategy} implementation that used only for authorization code oauth2 flow and does not support other flow types</p>
 *
 * The implementation exchanges the provided authorization code for an access token, if authorization code is correct
 *
 * @see AccessTokenGranterStrategy
 * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-4.1.3">Access Token Request</a>
 * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-4.1.4">Access Token Response</a>
 */
public class AuthorizationCodeExchangeAccessTokenGranterStrategy implements AccessTokenGranterStrategy {
    private final AuthorizationCodeFlowAccessTokenReturner authorizationCodeFlowAccessTokenReturner;
    private static final String AUTHORIZATION_CODE_REQUEST_PARAMETER_VALUE = "code";

    public AuthorizationCodeExchangeAccessTokenGranterStrategy(AuthorizationCodeFlowAccessTokenReturner authorizationCodeFlowAccessTokenReturner) {
        this.authorizationCodeFlowAccessTokenReturner = authorizationCodeFlowAccessTokenReturner;
    }

    @Override
    public Oauth2AccessToken obtainAccessToken(TokenRequest request) throws Oauth2Exception {
        if (!isGrantValid(request)) {
            throw new InvalidGrantOauth2Exception(Oauth2ErrorType.INVALID_GRANT.getErrorName(), "The grant is invalid or malformed");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new InvalidClientCredentialsException("The client is unauthorized", "The client is unauthorized. To avoid the error add correct client_id and client secret");
        }

        Oauth2Client client = (Oauth2Client) authentication.getPrincipal();
        Oauth2ClientCredentials credentials = Oauth2ClientCredentials.of(client.getClientId(), client.getClientSecret());
        String authorizationCode = request.getRequestParameters().get(AUTHORIZATION_CODE_REQUEST_PARAMETER_VALUE);
        return authorizationCodeFlowAccessTokenReturner.getToken(credentials, authorizationCode);
    }

    @Override
    public AuthorizationGrantType grantType() {
        return AuthorizationGrantType.AUTHORIZATION_CODE;
    }
}

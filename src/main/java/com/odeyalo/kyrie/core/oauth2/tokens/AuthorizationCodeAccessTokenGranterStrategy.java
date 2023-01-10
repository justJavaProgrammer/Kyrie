package com.odeyalo.kyrie.core.oauth2.tokens;

import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;
import com.odeyalo.kyrie.core.oauth2.client.Oauth2Client;
import com.odeyalo.kyrie.exceptions.InvalidGrantOauth2Exception;
import com.odeyalo.kyrie.exceptions.Oauth2ErrorType;
import com.odeyalo.kyrie.exceptions.Oauth2Exception;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


/**
 * AccessTokenGranterStrategy implementation that used only for authorization code oauth2 flow and does not support other flow types
 */
@Component
public class AuthorizationCodeAccessTokenGranterStrategy implements AccessTokenGranterStrategy {
    private final AuthorizationCodeFlowAccessTokenReturner authorizationCodeFlowAccessTokenReturner;
    private static final String AUTHORIZATION_CODE_REQUEST_PARAMETER_VALUE = "code";

    public AuthorizationCodeAccessTokenGranterStrategy(AuthorizationCodeFlowAccessTokenReturner authorizationCodeFlowAccessTokenReturner) {
        this.authorizationCodeFlowAccessTokenReturner = authorizationCodeFlowAccessTokenReturner;
    }

    @Override
    public Oauth2AccessToken obtainAccessToken(TokenRequest request) throws Oauth2Exception {
        if (!isGrantValid(request)) {
            throw new InvalidGrantOauth2Exception(Oauth2ErrorType.INVALID_GRANT.getErrorName(), "The grant is invalid or malformed");
        }

        Oauth2Client client = (Oauth2Client) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Oauth2ClientCredentials credentials = Oauth2ClientCredentials.of(client.getClientId(), client.getClientSecret());
        String authorizationCode = request.getRequestParameters().get(AUTHORIZATION_CODE_REQUEST_PARAMETER_VALUE);
        return authorizationCodeFlowAccessTokenReturner.getToken(credentials, authorizationCode);
    }

    @Override
    public AuthorizationGrantType grantType() {
        return AuthorizationGrantType.AUTHORIZATION_CODE;
    }
}

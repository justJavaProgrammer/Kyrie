package com.odeyalo.kyrie.core.oauth2.tokens;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authentication.AuthenticationResult;
import com.odeyalo.kyrie.core.authentication.Oauth2UserAuthenticationInfo;
import com.odeyalo.kyrie.core.authentication.Oauth2UserAuthenticationService;
import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.oauth2.support.Oauth2Constants;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.Oauth2AccessTokenGenerator;
import com.odeyalo.kyrie.exceptions.InvalidGrantOauth2Exception;
import com.odeyalo.kyrie.exceptions.InvalidRequestOauth2Exception;
import com.odeyalo.kyrie.exceptions.Oauth2ErrorType;
import com.odeyalo.kyrie.exceptions.Oauth2Exception;

import java.util.Map;

/**
 * AccessTokenGranterStrategy implementation that used to handle only Password Flow and does not support other flow types.
 *
 * @see AccessTokenGranterStrategy
 */
public class PasswordFlowAccessTokenGranterStrategy implements AccessTokenGranterStrategy {
    private final Oauth2AccessTokenGenerator generator;
    private static final String USERNAME_REQUEST_PARAMETER_NAME = "username";
    private static final String PASSWORD_REQUEST_PARAMETER_NAME = "password";
    private static final String PARAMETER_NOT_SET_DESCRIPTION_FORMAT = "%1$s parameter is not set, add '%1$s' parameter to request parameters and try again";

    private final Oauth2UserAuthenticationService authenticationService;

    public PasswordFlowAccessTokenGranterStrategy(Oauth2AccessTokenGenerator generator, Oauth2UserAuthenticationService authenticationService) {
        this.generator = generator;
        this.authenticationService = authenticationService;
    }

    @Override
    public Oauth2AccessToken obtainAccessToken(TokenRequest request) throws Oauth2Exception {
        if (!isGrantValid(request)) {
            throw new InvalidGrantOauth2Exception(Oauth2ErrorType.INVALID_GRANT.getErrorName(), "The grant is invalid or malformed");
        }
        Map<String, String> parameters = request.getRequestParameters();
        String username = parameters.get(USERNAME_REQUEST_PARAMETER_NAME);
        String password = parameters.get(PASSWORD_REQUEST_PARAMETER_NAME);

        if (username == null) {
            throw new InvalidRequestOauth2Exception("Username parameter is not set", String.format(PARAMETER_NOT_SET_DESCRIPTION_FORMAT, USERNAME_REQUEST_PARAMETER_NAME));
        }
        if (password == null) {
            throw new InvalidRequestOauth2Exception("Password parameter is not set", String.format(PARAMETER_NOT_SET_DESCRIPTION_FORMAT, PASSWORD_REQUEST_PARAMETER_NAME));
        }

        String[] scopes = request.getScopes();

        if (scopes == null || scopes.length == 0) {
            throw new InvalidRequestOauth2Exception("The request does not contain 'scope' parameter", String.format(PARAMETER_NOT_SET_DESCRIPTION_FORMAT, Oauth2Constants.SCOPE));
        }

        AuthenticationResult result = authenticationService.authenticate(new Oauth2UserAuthenticationInfo(username, password));

        if (!result.isSuccess()) {
            throw new InvalidRequestOauth2Exception("The user credentials are wrong and authentication cannot be performed",
                    "The user credentials are wrong and authentication cannot be performed");
        }

        Oauth2User user = result.getUser();

        return generator.generateAccessToken(user, scopes);
    }

    @Override
    public AuthorizationGrantType grantType() {
        return AuthorizationGrantType.PASSWORD;
    }
}

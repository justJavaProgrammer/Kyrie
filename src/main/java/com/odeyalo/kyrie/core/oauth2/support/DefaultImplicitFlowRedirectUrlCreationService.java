package com.odeyalo.kyrie.core.oauth2.support;

import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.oauth2.Oauth2Token;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessToken;
import com.odeyalo.kyrie.support.Oauth2Utils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

/**
 * Default {@link ImplicitFlowRedirectUrlCreationService} implementation that just creates a redirect uri from provided {@link Oauth2Token}
 * @version 1.0
 */
public class DefaultImplicitFlowRedirectUrlCreationService implements ImplicitFlowRedirectUrlCreationService {

    /**
     * Create redirect URL only for IMPLICIT FLOW
     * @param request - AuthorizationRequest with all fields set
     * @param token - generated token from Oauth2FlowHandler
     * @return - redirect url ONLY for IMPLICIT FLOW.
     * @see <a href="https://www.rfc-editor.org/rfc/rfc6749#section-4.2.2">Implicit Flow Response</a>
     */
    @Override
    public String createRedirectUrl(AuthorizationRequest request, Oauth2Token token) {
        if (!(token instanceof Oauth2AccessToken)) {
            throw new UnsupportedOperationException(String.format(
                    "The redirect url can't be created since %s requires %s class. Actual class: %s",
                    DefaultImplicitFlowRedirectUrlCreationService.class.getSimpleName(),
                    Oauth2AccessToken.class.getSimpleName(), token.getClass().getSimpleName()));
        }
        Oauth2AccessToken accessToken = (Oauth2AccessToken) token;
        return UriComponentsBuilder.fromUriString(request.getRedirectUrl())
                .queryParamIfPresent(Oauth2Constants.STATE, Optional.ofNullable(request.getState()))
                .queryParam(Oauth2Constants.ACCESS_TOKEN, token.getTokenValue())
                .queryParam(Oauth2Constants.TOKEN_TYPE, accessToken.getTokenType().getValue())
                .queryParamIfPresent(Oauth2Constants.EXPIRES_IN, Oauth2Utils.getExpiresIn(accessToken))
                .toUriString();
    }

    @Override
    public AuthorizationGrantType supportedGrantType() {
        return AuthorizationGrantType.IMPLICIT;
    }
}

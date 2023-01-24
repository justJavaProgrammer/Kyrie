package com.odeyalo.kyrie.core.oauth2.support;

import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.oauth2.Oauth2Token;
import com.odeyalo.kyrie.core.oauth2.tokens.code.AuthorizationCode;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

/**
 * Create redirect url ONLY for authorization_code grant type and CODE response type
 * @version 1.0
 */
public class DefaultAuthorizationCodeFlowRedirectUrlCreationService implements AuthorizationCodeFlowRedirectUrlCreationService {

    @Override
    public String createRedirectUrl(AuthorizationRequest request, Oauth2Token token) {
        if (!(token instanceof AuthorizationCode)) {
            throw new UnsupportedOperationException(String.format(
                    "The redirect url can't be created since %s requires %s class. Actual class: %s",
                    DefaultAuthorizationCodeFlowRedirectUrlCreationService.class.getSimpleName(),
                    AuthorizationCode.class.getSimpleName(), token.getClass().getSimpleName()));
        }
        AuthorizationCode authorizationCode = (AuthorizationCode) token;
        return UriComponentsBuilder.fromUriString(request.getRedirectUrl())
                .queryParamIfPresent(Oauth2Constants.STATE, Optional.ofNullable(request.getState()))
                .queryParam(Oauth2ResponseType.CODE.getSimplifiedName(), authorizationCode.getCodeValue()).toUriString();
    }
}

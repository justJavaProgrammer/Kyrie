package com.odeyalo.kyrie.core.oauth2.support;

import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.oauth2.CombinedOauth2Token;
import com.odeyalo.kyrie.core.oauth2.Oauth2Token;
import com.odeyalo.kyrie.core.oauth2.flow.MultipleResponseTypeOidcOauth2FlowHandler;
import com.odeyalo.kyrie.support.Oauth2Utils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Create a redirection URL ONLY for flow with multiple response types that defined in OpenID Connect Specification.
 * The implementation supports ONLY OpenID Connect specified multiple response_type.
 * <p>Multiple response types list that Hybrid Flow supports:</p>
 * <ul>
 *     <li>id_token token is used to obtain an ID Token and Access Token</li>
 *     <li>code id_token is used to obtain an authorization code and ID Token</li>
 *     <li>code token is used to obtain an Authorization code and Access Token</li>
 *     <li>code id_token token is used to obtain an Authorization code, ID Token and Access Token</li>
 * </ul>
 *
 * @version 1.0
 * @see <a href="https://openid.net/specs/oauth-v2-multiple-response-types-1_0.html#Combinations">Possible OpenID Connect multiple Response Type combintations</a>
 */
public class DefaultMultipleResponseTypeFlowRedirectUrlCreationService implements MultipleResponseTypeFlowRedirectUrlCreationService {

    @Override
    public String createRedirectUrl(AuthorizationRequest request, Oauth2Token token) {
        if (request.getResponseTypes() == null || request.getResponseTypes().length <= 1) {
            throw new UnsupportedOperationException(
                    String.format("%s supports only multiple response types and requires 2 response types minimum. Received: %s",
                            DefaultMultipleResponseTypeFlowRedirectUrlCreationService.class.getSimpleName(), Arrays.toString(request.getResponseTypes())));
        }

        if (!(token instanceof CombinedOauth2Token)) {
            throw new UnsupportedOperationException(String.format(
                    "The redirect url can't be created since %s requires %s class. Actual class: %s",
                    DefaultMultipleResponseTypeFlowRedirectUrlCreationService.class.getSimpleName(),
                    CombinedOauth2Token.class.getSimpleName(), token.getClass().getSimpleName()));
        }

        CombinedOauth2Token combinedOauth2Token = (CombinedOauth2Token) token;
        Map<String, Object> additionalInfo = combinedOauth2Token.getAdditionalInfo();

        return UriComponentsBuilder.fromUriString(request.getRedirectUrl())
                .queryParamIfPresent(MultipleResponseTypeOidcOauth2FlowHandler.ACCESS_TOKEN_KEY, Optional.ofNullable(combinedOauth2Token.getTokenValue()))
                .queryParamIfPresent(Oauth2Constants.TOKEN_TYPE, Optional.ofNullable(additionalInfo.get(Oauth2Constants.TOKEN_TYPE)))
                .queryParamIfPresent(Oauth2Constants.EXPIRES_IN, Oauth2Utils.getExpiresIn(token))
                .queryParamIfPresent(MultipleResponseTypeOidcOauth2FlowHandler.ID_TOKEN_KEY, Optional.ofNullable(additionalInfo.get(MultipleResponseTypeOidcOauth2FlowHandler.ID_TOKEN_KEY)))
                .queryParamIfPresent(MultipleResponseTypeOidcOauth2FlowHandler.AUTHORIZATION_CODE_TOKEN_KEY, Optional.ofNullable(additionalInfo.get(MultipleResponseTypeOidcOauth2FlowHandler.AUTHORIZATION_CODE_TOKEN_KEY)))
                .queryParamIfPresent(Oauth2Constants.STATE, Optional.ofNullable(request.getState()))
                .toUriString();
    }

    @Override
    public AuthorizationGrantType supportedGrantType() {
        return AuthorizationGrantType.MULTIPLE;
    }
}

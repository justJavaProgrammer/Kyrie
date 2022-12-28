package com.odeyalo.kyrie.core.oauth2.flow;

import com.odeyalo.kyrie.core.Oauth2User;
import com.odeyalo.kyrie.core.authorization.AuthorizationRequest;
import com.odeyalo.kyrie.core.authorization.Oauth2ResponseType;
import com.odeyalo.kyrie.core.oauth2.CombinedOauth2Token;
import com.odeyalo.kyrie.core.oauth2.Oauth2ClientCredentials;
import com.odeyalo.kyrie.core.oauth2.Oauth2Token;
import com.odeyalo.kyrie.core.oauth2.oidc.OidcResponseType;
import com.odeyalo.kyrie.core.oauth2.oidc.generator.OidcOauth2TokenGeneratorFacade;
import com.odeyalo.kyrie.core.oauth2.support.Oauth2Constants;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessToken;
import com.odeyalo.kyrie.core.oauth2.tokens.code.AuthorizationCode;
import com.odeyalo.kyrie.core.oauth2.tokens.code.provider.AuthorizationCodeProvider;
import com.odeyalo.kyrie.core.oauth2.tokens.jwt.Oauth2AccessTokenGenerator;
import com.odeyalo.kyrie.support.Oauth2Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Default implementation that supports Id_token, access_token and code as response types
 */
@Component
public class DefaultMultipleResponseTypeOidcOauth2FlowHandler implements MultipleResponseTypeOidcOauth2FlowHandler {
    private final OidcOauth2TokenGeneratorFacade generatorFacade;
    private final Oauth2AccessTokenGenerator accessTokenGenerator;
    private final AuthorizationCodeProvider authorizationCodeProvider;

    @Autowired
    public DefaultMultipleResponseTypeOidcOauth2FlowHandler(OidcOauth2TokenGeneratorFacade generatorFacade, Oauth2AccessTokenGenerator generator, AuthorizationCodeProvider authorizationCodeProvider) {
        this.generatorFacade = generatorFacade;
        this.accessTokenGenerator = generator;
        this.authorizationCodeProvider = authorizationCodeProvider;
    }

    @Override
    public CombinedOauth2Token handleFlow(AuthorizationRequest request, Oauth2User user) {
        CombinedOauth2Token.CombinedOauth2TokenBuilder<?, ?> builder = CombinedOauth2Token.builder();
        Set<Oauth2ResponseType> types = Set.of(request.getResponseTypes());
        String[] scopes = request.getScopes();

        if (types.contains(OidcResponseType.ID_TOKEN)) {
            Oauth2ClientCredentials credentials = Oauth2ClientCredentials.of(request.getClientId());
            Oauth2Token oidcToken = generatorFacade.generateToken(credentials, user, scopes);
            builder.addInfo(ID_TOKEN_KEY, oidcToken.getTokenValue());
        }

        if (types.contains(OidcResponseType.TOKEN)) {
            Oauth2AccessToken accessToken = accessTokenGenerator.generateAccessToken(user, scopes);
            builder.expiresIn(accessToken.getExpiresIn())
                    .issuedAt(accessToken.getIssuedAt())
                    .tokenValue(accessToken.getTokenValue());
            builder.addInfo(Oauth2Constants.TOKEN_TYPE, accessToken.getTokenType().getValue());
            builder.addInfo(ACCESS_TOKEN_KEY, accessToken);
            builder.addInfoIfPresent(Oauth2Constants.EXPIRES_IN, Oauth2Utils.getExpiresIn(accessToken));
        }

        if (types.contains(OidcResponseType.CODE)) {
            AuthorizationCode authorizationCode = authorizationCodeProvider.getAuthorizationCode(request.getClientId(), user, scopes);
            builder.addInfo(AUTHORIZATION_CODE_TOKEN_KEY, authorizationCode.getCodeValue());
        }
        return builder.build();
    }
}

package com.odeyalo.kyrie.core.oauth2.tokens.facade;

import com.odeyalo.kyrie.core.oauth2.CombinedOauth2Token;
import com.odeyalo.kyrie.core.oauth2.support.Oauth2Constants;
import com.odeyalo.kyrie.core.oauth2.tokens.AccessTokenGranterStrategyFactory;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessToken;
import com.odeyalo.kyrie.core.oauth2.tokens.TokenRequest;
import com.odeyalo.kyrie.core.oauth2.tokens.customizer.Oauth2TokenCustomizerProcessorRegistry;
import com.odeyalo.kyrie.dto.Oauth2AccessTokenResponse;
import com.odeyalo.kyrie.support.Oauth2Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Default {@link AccessTokenGranterStrategyFacadeWrapper} implementation that uses
 * {@link com.odeyalo.kyrie.core.oauth2.tokens.customizer.Oauth2TokenCustomizerProcessor}
 * to customize the {@link com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessToken}
 */
public class SimpleAccessTokenGranterStrategyFacadeWrapper implements AccessTokenGranterStrategyFacadeWrapper {
    private final AccessTokenGranterStrategyFactory delegateFactory;
    private final Oauth2TokenCustomizerProcessorRegistry container;

    public SimpleAccessTokenGranterStrategyFacadeWrapper(AccessTokenGranterStrategyFactory delegateFactory, Oauth2TokenCustomizerProcessorRegistry container) {
        this.delegateFactory = delegateFactory;
        this.container = container;
    }

    @Override
    public CombinedOauth2Token getToken(TokenRequest request) {
        Oauth2AccessToken accessToken = delegateFactory.getGranter(request).obtainAccessToken(request);

        CombinedOauth2Token.CombinedOauth2TokenBuilder<?, ?> builder = CombinedOauth2Token
                .from(accessToken)
                .toBuilder()
                .addInfo(Oauth2Constants.TOKEN_TYPE, accessToken.getTokenType().getValue())
                .addInfo(Oauth2Constants.SCOPE, accessToken.getScope());

        container.getCustomizers().forEach(customizer -> customizer.customizeOauth2Token(accessToken, builder));

        return builder.build();
    }

    @Override
    public Oauth2AccessTokenResponse getResponse(TokenRequest request) {
        CombinedOauth2Token token = getToken(request);
        Map<String, Object> parameters = new HashMap<>(token.getAdditionalInfo());

        return Oauth2AccessTokenResponse
                .builder()
                .active(true)
                .token(token.getTokenValue())
                .tokenType((String) poll(parameters, Oauth2Constants.TOKEN_TYPE))
                .expiresIn(Oauth2Utils.getExpiresIn(token).orElse(0L))
                .scopes((String ) poll(parameters, Oauth2Constants.SCOPE))
                .additionalParameters(parameters)
                .build();
    }

    /**
     * Take  and remove element
     * @param parameters - parameters
     * @param key - key to get
     * @return - object that was took and removed
     */
    private Object poll(Map<String, Object> parameters, String key) {
        Object o = parameters.get(key);
        parameters.remove(key);
        return o;
    }
}

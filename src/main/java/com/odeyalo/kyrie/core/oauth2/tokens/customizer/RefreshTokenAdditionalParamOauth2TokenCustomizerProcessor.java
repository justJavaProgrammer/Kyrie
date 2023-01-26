package com.odeyalo.kyrie.core.oauth2.tokens.customizer;

import com.odeyalo.kyrie.core.oauth2.CombinedOauth2Token;
import com.odeyalo.kyrie.core.oauth2.Oauth2Token;
import com.odeyalo.kyrie.core.oauth2.RefreshToken;
import com.odeyalo.kyrie.core.oauth2.support.Oauth2Constants;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessToken;
import com.odeyalo.kyrie.core.oauth2.tokens.refresh.RefreshTokenProvider;
import com.odeyalo.kyrie.support.AdvancedStringUtils;
import com.odeyalo.kyrie.support.ClientId;
import com.odeyalo.kyrie.support.ClientIdAware;
import io.jsonwebtoken.lang.Assert;
import org.springframework.stereotype.Component;

/**
 * {@link Oauth2TokenCustomizerProcessor} implementation used to add 'refresh_token' parameter oauth2 token
 */
@Component
public class RefreshTokenAdditionalParamOauth2TokenCustomizerProcessor implements Oauth2TokenCustomizerProcessor, ClientIdAware {
    private final RefreshTokenProvider refreshTokenProvider;
    private ClientId clientId;

    public RefreshTokenAdditionalParamOauth2TokenCustomizerProcessor(RefreshTokenProvider refreshTokenProvider) {
        this.refreshTokenProvider = refreshTokenProvider;
    }

    @Override
    public void customizeOauth2Token(Oauth2Token original, CombinedOauth2Token.CombinedOauth2TokenBuilder<?, ?> builder) {
        Assert.notNull(clientId.getClientIdValue(), "Client id must not be null!");

        if (!(original instanceof Oauth2AccessToken)) {
            return;
        }
        Oauth2AccessToken accessToken = (Oauth2AccessToken) original;

        RefreshToken refreshToken = refreshTokenProvider.generateToken(clientId, AdvancedStringUtils.spaceDelimitedListToStringArray(accessToken.getScope()));
        // Add only refresh token value, since we don't need to provide refresh token expire time by specification
        builder.addInfo(Oauth2Constants.REFRESH_TOKEN, refreshToken.getTokenValue());
    }

    @Override
    public void setClientId(ClientId clientId) {
        this.clientId = clientId;
    }
}

package com.odeyalo.kyrie.core.oauth2.tokens.customizer;

import com.odeyalo.kyrie.core.oauth2.CombinedOauth2Token;
import com.odeyalo.kyrie.core.oauth2.Oauth2Token;
import com.odeyalo.kyrie.core.oauth2.support.Oauth2Constants;
import org.springframework.stereotype.Component;

/**
 * {@link Oauth2TokenCustomizerProcessor} implementation used to add 'refresh_token' parameter oauth2 token
 */
@Component
public class RefreshTokenAdditionalParamOauth2TokenCustomizerProcessor implements Oauth2TokenCustomizerProcessor {

    @Override
    public void customizeOauth2Token(Oauth2Token original, CombinedOauth2Token.CombinedOauth2TokenBuilder<?, ?> builder) {
        builder.addInfo(Oauth2Constants.REFRESH_TOKEN, "refresh_token");
    }
}

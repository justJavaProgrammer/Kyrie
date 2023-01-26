package com.odeyalo.kyrie.core.oauth2.tokens.facade;

import com.odeyalo.kyrie.core.oauth2.CombinedOauth2Token;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessToken;
import com.odeyalo.kyrie.core.oauth2.tokens.TokenRequest;
import com.odeyalo.kyrie.dto.Oauth2AccessTokenResponse;

/**
 * Facade interface that will automatically wrap the {@link Oauth2AccessToken} from {@link com.odeyalo.kyrie.core.oauth2.tokens.AccessTokenGranterStrategy} to {@link CombinedOauth2Token}
 */
public interface AccessTokenGranterStrategyFacadeWrapper {

    /**
     * Return the access token that was previously wrapped in {@link CombinedOauth2Token}
     * @param request - current TokenRequest
     * @return - access token that was wrapped, null otherwise
     */
    CombinedOauth2Token getToken(TokenRequest request);

    /**
     * Obtain token, customize it and wrap it to {@link Oauth2AccessTokenResponse}
     * @param request - current TokenRequest
     * @return - ready-to-use Oauth2AccessTokenResponse
     */
    Oauth2AccessTokenResponse getResponse(TokenRequest request);
}

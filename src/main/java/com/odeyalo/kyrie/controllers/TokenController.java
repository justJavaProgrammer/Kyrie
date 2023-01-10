package com.odeyalo.kyrie.controllers;

import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.oauth2.Oauth2TokenGeneratorFacade;
import com.odeyalo.kyrie.core.oauth2.tokens.AccessTokenGranterStrategyFactory;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessToken;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessTokenManager;
import com.odeyalo.kyrie.core.oauth2.tokens.TokenRequest;
import com.odeyalo.kyrie.dto.AccessTokenIntrospectionResponse;
import com.odeyalo.kyrie.dto.KyrieSuccessfulObtainTokenResponse;
import com.odeyalo.kyrie.support.Oauth2Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Controller to obtain an access token and token info
 */
public class TokenController {
    private final Logger logger = LoggerFactory.getLogger(TokenController.class);
    private final Oauth2AccessTokenManager tokenManager;
    private final Oauth2TokenGeneratorFacade generatorFacade;

    @Autowired
    private AccessTokenGranterStrategyFactory factory;
    public TokenController(Oauth2AccessTokenManager tokenManager, Oauth2TokenGeneratorFacade generatorFacade) {
        this.tokenManager = tokenManager;
        this.generatorFacade = generatorFacade;
    }

    /**
     * Method to process /token endpoint that support only 'application/json' type
     *
     * @return - access token
     */
    @PostMapping(value = "/token")
    public ResponseEntity<?> resolveAccessTokenUsingJson(@RequestParam("grant_type") AuthorizationGrantType grantType,
                                                         @RequestParam(value = "client_id", required = false) String clientId,
                                                         @RequestParam(value = "scopes", required = false) String[] scopes,
                                                         @RequestParam Map<String, String> params) {
        TokenRequest request = TokenRequest.builder()
                .clientId(clientId)
                .scopes(scopes)
                .requestParameters(params)
                .grantType(grantType)
                .build();
        return obtainAccessToken(request);
    }

//    @PostMapping(value = "/token", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
//    public ResponseEntity<?> resolveAccessTokenUsingFormData(@AdvancedModelAttribute GetAccessTokenRequestDTO body) {
//        return obtainAccessToken(body);
//    }

    @PostMapping(value = "/tokeninfo", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> tokenInfoRfc7662(@RequestParam String token) {
        logger.info("Received token: {}", token);
        Oauth2AccessToken info = tokenManager.getTokenInfo(token);
        AccessTokenIntrospectionResponse response = getAccessTokenIntrospectionResponse(info);
        logger.info("Body: {}", response);
        return ResponseEntity.ok(response);
    }

    private AccessTokenIntrospectionResponse getAccessTokenIntrospectionResponse(Oauth2AccessToken info) {
        if (info.isExpired()) {
            return AccessTokenIntrospectionResponse.nonActive();
        }
        return AccessTokenIntrospectionResponse.builder(true)
                .expiresIn(info.getExpiresIn().getEpochSecond())
                .scope(info.getScope()).build();
    }

    private ResponseEntity<KyrieSuccessfulObtainTokenResponse> obtainAccessToken(TokenRequest body) {
        logger.info("body {}", body);
        Oauth2AccessToken token = factory.getGranter(body).obtainAccessToken(body);
        return ResponseEntity.ok(
                new KyrieSuccessfulObtainTokenResponse(
                        token.getTokenValue(),
                        token.getTokenType().getValue(),
                        Oauth2Utils.getExpiresIn(token).orElse(token.getExpiresIn().getEpochSecond()),
                        token.getScope())
        );
    }
}


package com.odeyalo.kyrie.controllers;

import com.odeyalo.kyrie.core.authorization.AuthorizationGrantType;
import com.odeyalo.kyrie.core.oauth2.support.Oauth2Constants;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessToken;
import com.odeyalo.kyrie.core.oauth2.tokens.Oauth2AccessTokenManager;
import com.odeyalo.kyrie.core.oauth2.tokens.TokenRequest;
import com.odeyalo.kyrie.core.oauth2.tokens.facade.AccessTokenGranterStrategyFacadeWrapper;
import com.odeyalo.kyrie.dto.AccessTokenIntrospectionResponse;
import com.odeyalo.kyrie.dto.Oauth2AccessTokenResponse;
import com.odeyalo.kyrie.support.Oauth2Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller to obtain an access token and token info
 */
public class TokenController {
    private final Logger logger = LoggerFactory.getLogger(TokenController.class);
    private final Oauth2AccessTokenManager tokenManager;

    private final AccessTokenGranterStrategyFacadeWrapper wrapper;

    public TokenController(Oauth2AccessTokenManager tokenManager, AccessTokenGranterStrategyFacadeWrapper wrapper) {
        this.tokenManager = tokenManager;
        this.wrapper = wrapper;
    }

    /**
     * Method to process /token endpoint that support only 'application/json' type
     *
     * @return - access token
     */
    @PostMapping(value = "/token", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<?> resolveAccessTokenUsingParams(@RequestParam(Oauth2Constants.GRANT_TYPE) AuthorizationGrantType grantType,
                                                           @RequestParam(value = Oauth2Constants.CLIENT_ID, required = false) String clientId,
                                                           @RequestParam(value = Oauth2Constants.SCOPE, required = false) String[] scopes,
                                                           @RequestParam Map<String, String> params) {
        TokenRequest request = TokenRequest.builder()
                .clientId(clientId)
                .scopes(scopes)
                .requestParameters(params)
                .grantType(grantType)
                .build();
        return doObtainAccessToken(request);
    }

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resolveAccessTokenUsingJson(@RequestBody Map<String, Object> body) {
        Map<String, String> params = body.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (entry) -> (String) entry.getValue()));

        String grantType = params.get(Oauth2Constants.GRANT_TYPE);
        String clientId =  params.get(Oauth2Constants.CLIENT_ID);
        String rawScopes = params.get(Oauth2Constants.SCOPE);
        String[] scopes = Oauth2Utils.fromRawScopes(rawScopes);


        TokenRequest request = TokenRequest.builder()
                .clientId(clientId)
                .scopes(scopes)
                .requestParameters(params)
                .grantType(AuthorizationGrantType.fromSimplifiedName(grantType))
                .build();

        return doObtainAccessToken(request);
    }

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

    private ResponseEntity<?> doObtainAccessToken(TokenRequest body) {
        logger.info("body {}", body);
        Oauth2AccessTokenResponse tokenResponse = wrapper.getResponse(body);
        this.logger.info("Return: {}", tokenResponse);
        return ResponseEntity.ok(tokenResponse);

    }
}

